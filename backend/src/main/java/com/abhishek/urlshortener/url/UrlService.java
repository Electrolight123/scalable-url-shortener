package com.abhishek.urlshortener.url;

import com.abhishek.urlshortener.analytics.AnalyticsService;
import com.abhishek.urlshortener.config.CacheService;
import com.abhishek.urlshortener.config.RateLimitConfig;
import com.abhishek.urlshortener.exception.BadRequestException;
import com.abhishek.urlshortener.exception.NotFoundException;
import com.abhishek.urlshortener.exception.RateLimitException;
import com.abhishek.urlshortener.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class UrlService {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 7;

    private final UrlRepository urlRepository;
    private final CacheService cacheService;
    private final RateLimitConfig rateLimitConfig;
    private final AnalyticsService analyticsService;
    private final Random random = new Random();
    private final String baseUrl;

    public UrlService(
            UrlRepository urlRepository,
            CacheService cacheService,
            RateLimitConfig rateLimitConfig,
            AnalyticsService analyticsService,
            @Value("${app.base-url}") String baseUrl
    ) {
        this.urlRepository = urlRepository;
        this.cacheService = cacheService;
        this.rateLimitConfig = rateLimitConfig;
        this.analyticsService = analyticsService;
        this.baseUrl = baseUrl;
    }

    @Transactional
    public UrlResponse createUrl(CreateUrlRequest request, User user) {
        rateLimitUrlCreation(user);

        validateOriginalUrl(request.getOriginalUrl());

        String customAlias = cleanAlias(request.getCustomAlias());
        String shortCode;

        if (customAlias != null) {
            if (urlRepository.existsByCustomAlias(customAlias) || urlRepository.existsByShortCode(customAlias)) {
                throw new BadRequestException("Custom alias is already taken");
            }

            shortCode = customAlias;
        } else {
            shortCode = generateUniqueCode();
        }

        Url url = new Url();
        url.setUser(user);
        url.setOriginalUrl(request.getOriginalUrl());
        url.setCustomAlias(customAlias);
        url.setShortCode(shortCode);
        url.setExpiresAt(request.getExpiresAt());

        Url saved = urlRepository.save(url);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UrlResponse> myUrls(User user) {
        return urlRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteUrl(Long id, User user) {
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("URL not found"));

        if (!url.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot delete this URL");
        }

        url.setActive(false);
        cacheService.delete(cacheKey(url.getShortCode()));
    }

    @Transactional
    public String resolveOriginalUrl(String shortCode, HttpServletRequest request) {
        rateLimitRedirect(request);

        String cachedOriginalUrl = cacheService.get(cacheKey(shortCode));

        if (cachedOriginalUrl != null) {
            urlRepository.findByShortCode(shortCode)
                    .ifPresent(url -> analyticsService.recordClick(url, request));

            return cachedOriginalUrl;
        }

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new NotFoundException("Short URL not found"));

        if (!url.isActive()) {
            throw new BadRequestException("Short URL is inactive");
        }

        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Short URL has expired");
        }

        analyticsService.recordClick(url, request);

        cacheService.set(cacheKey(shortCode), url.getOriginalUrl(), Duration.ofHours(24));

        return url.getOriginalUrl();
    }

    private void validateOriginalUrl(String originalUrl) {
        try {
            URI uri = URI.create(originalUrl);

            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new BadRequestException("Invalid URL");
            }

            if (!uri.getScheme().equals("http") && !uri.getScheme().equals("https")) {
                throw new BadRequestException("Only http and https URLs are allowed");
            }
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid URL");
        }
    }

    private String cleanAlias(String alias) {
        if (alias == null || alias.isBlank()) {
            return null;
        }

        String cleaned = alias.trim();

        if (!cleaned.matches("^[a-zA-Z0-9_-]{3,50}$")) {
            throw new BadRequestException(
                    "Alias must be 3-50 characters and contain only letters, numbers, hyphen, or underscore"
            );
        }

        return cleaned;
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String code = randomCode();

            if (!urlRepository.existsByShortCode(code)) {
                return code;
            }
        }

        throw new BadRequestException("Could not generate unique short code");
    }

    private String randomCode() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        return builder.toString();
    }

    private UrlResponse toResponse(Url url) {
        return new UrlResponse(
                url.getId(),
                url.getOriginalUrl(),
                url.getShortCode(),
                baseUrl + "/" + url.getShortCode(),
                url.getExpiresAt(),
                url.getCreatedAt()
        );
    }

    private String cacheKey(String shortCode) {
        return "url:" + shortCode;
    }

    private void rateLimitUrlCreation(User user) {
        String key = "rate:create:" + user.getId();

        Long count = cacheService.increment(key, Duration.ofHours(1));

        if (count != null && count > rateLimitConfig.getUrlCreationPerHour()) {
            throw new RateLimitException("URL creation limit exceeded. Try again later.");
        }
    }

    private void rateLimitRedirect(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String key = "rate:redirect:" + ip;

        Long count = cacheService.increment(key, Duration.ofMinutes(1));

        if (count != null && count > rateLimitConfig.getRedirectsPerMinute()) {
            throw new RateLimitException("Too many redirect requests. Try again later.");
        }
    }
}