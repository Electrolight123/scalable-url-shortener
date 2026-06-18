package com.abhishek.urlshortener.url;

import com.abhishek.urlshortener.analytics.AnalyticsService;
import com.abhishek.urlshortener.config.CacheService;
import com.abhishek.urlshortener.config.RateLimitConfig;
import com.abhishek.urlshortener.exception.BadRequestException;
import com.abhishek.urlshortener.exception.NotFoundException;
import com.abhishek.urlshortener.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private CacheService cacheService;

    @Mock
    private RateLimitConfig rateLimitConfig;

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private HttpServletRequest httpServletRequest;

    private UrlService urlService;
    private User user;

    @BeforeEach
    void setUp() {
        urlService = new UrlService(
                urlRepository,
                cacheService,
                rateLimitConfig,
                analyticsService,
                "http://localhost:8080"
        );

        user = new User(
                "Abhishek",
                "abhishek@example.com",
                "encoded-password"
        );

        user.setId(1L);
    }

    @Test
    void createUrlShouldCreateUrlWithCustomAlias() {
        when(rateLimitConfig.getUrlCreationPerHour())
                .thenReturn(20);

        CreateUrlRequest request = new CreateUrlRequest();
        request.setOriginalUrl("https://www.github.com");
        request.setCustomAlias("github-link");

        when(cacheService.increment(
                eq("rate:create:1"),
                any(Duration.class)
        )).thenReturn(1L);

        when(urlRepository.existsByCustomAlias("github-link"))
                .thenReturn(false);

        when(urlRepository.existsByShortCode("github-link"))
                .thenReturn(false);

        when(urlRepository.save(any(Url.class)))
                .thenAnswer(invocation -> {
                    Url url = invocation.getArgument(0);
                    url.setId(10L);
                    return url;
                });

        UrlResponse response = urlService.createUrl(request, user);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("https://www.github.com", response.getOriginalUrl());
        assertEquals("github-link", response.getShortCode());
        assertEquals(
                "http://localhost:8080/github-link",
                response.getShortUrl()
        );

        verify(cacheService).increment(
                eq("rate:create:1"),
                any(Duration.class)
        );

        verify(urlRepository).save(any(Url.class));
    }

    @Test
    void createUrlShouldFailWhenAliasAlreadyExists() {
        when(rateLimitConfig.getUrlCreationPerHour())
                .thenReturn(20);

        CreateUrlRequest request = new CreateUrlRequest();
        request.setOriginalUrl("https://www.github.com");
        request.setCustomAlias("github-link");

        when(cacheService.increment(
                eq("rate:create:1"),
                any(Duration.class)
        )).thenReturn(1L);

        when(urlRepository.existsByCustomAlias("github-link"))
                .thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> urlService.createUrl(request, user)
        );

        assertEquals(
                "Custom alias is already taken",
                exception.getMessage()
        );

        verify(urlRepository, never()).save(any(Url.class));
    }

    @Test
    void createUrlShouldFailForInvalidUrl() {
        when(rateLimitConfig.getUrlCreationPerHour())
                .thenReturn(20);

        CreateUrlRequest request = new CreateUrlRequest();
        request.setOriginalUrl("invalid-url");

        when(cacheService.increment(
                eq("rate:create:1"),
                any(Duration.class)
        )).thenReturn(1L);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> urlService.createUrl(request, user)
        );

        assertEquals(
                "Invalid URL",
                exception.getMessage()
        );

        verify(urlRepository, never()).save(any(Url.class));
    }

    @Test
    void myUrlsShouldReturnUsersUrls() {
        Url url = createTestUrl();

        when(urlRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of(url));

        List<UrlResponse> response = urlService.myUrls(user);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("github-link", response.get(0).getShortCode());
        assertEquals(
                "https://www.github.com",
                response.get(0).getOriginalUrl()
        );

        verify(urlRepository)
                .findByUserOrderByCreatedAtDesc(user);
    }

    @Test
    void resolveOriginalUrlShouldReturnCachedUrl() {
        when(rateLimitConfig.getRedirectsPerMinute())
                .thenReturn(100);

        when(httpServletRequest.getRemoteAddr())
                .thenReturn("127.0.0.1");

        when(cacheService.increment(
                eq("rate:redirect:127.0.0.1"),
                any(Duration.class)
        )).thenReturn(1L);

        when(cacheService.get("url:github-link"))
                .thenReturn("https://www.github.com");

        Url url = createTestUrl();

        when(urlRepository.findByShortCode("github-link"))
                .thenReturn(Optional.of(url));

        String result = urlService.resolveOriginalUrl(
                "github-link",
                httpServletRequest
        );

        assertEquals(
                "https://www.github.com",
                result
        );

        verify(analyticsService).recordClick(
                url,
                httpServletRequest
        );

        verify(cacheService, never()).set(
                any(),
                any(),
                any(Duration.class)
        );
    }

    @Test
    void resolveOriginalUrlShouldReadDatabaseWhenCacheMisses() {
        when(rateLimitConfig.getRedirectsPerMinute())
                .thenReturn(100);

        when(httpServletRequest.getRemoteAddr())
                .thenReturn("127.0.0.1");

        when(cacheService.increment(
                eq("rate:redirect:127.0.0.1"),
                any(Duration.class)
        )).thenReturn(1L);

        when(cacheService.get("url:github-link"))
                .thenReturn(null);

        Url url = createTestUrl();

        when(urlRepository.findByShortCode("github-link"))
                .thenReturn(Optional.of(url));

        String result = urlService.resolveOriginalUrl(
                "github-link",
                httpServletRequest
        );

        assertEquals(
                "https://www.github.com",
                result
        );

        verify(cacheService).set(
                eq("url:github-link"),
                eq("https://www.github.com"),
                any(Duration.class)
        );

        verify(analyticsService).recordClick(
                url,
                httpServletRequest
        );
    }

    @Test
    void resolveOriginalUrlShouldFailWhenUrlDoesNotExist() {
        when(rateLimitConfig.getRedirectsPerMinute())
                .thenReturn(100);

        when(httpServletRequest.getRemoteAddr())
                .thenReturn("127.0.0.1");

        when(cacheService.increment(
                eq("rate:redirect:127.0.0.1"),
                any(Duration.class)
        )).thenReturn(1L);

        when(cacheService.get("url:missing"))
                .thenReturn(null);

        when(urlRepository.findByShortCode("missing"))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> urlService.resolveOriginalUrl(
                        "missing",
                        httpServletRequest
                )
        );

        assertEquals(
                "Short URL not found",
                exception.getMessage()
        );

        verify(analyticsService, never())
                .recordClick(any(), any());
    }

    @Test
    void resolveOriginalUrlShouldFailWhenUrlIsExpired() {
        when(rateLimitConfig.getRedirectsPerMinute())
                .thenReturn(100);

        when(httpServletRequest.getRemoteAddr())
                .thenReturn("127.0.0.1");

        when(cacheService.increment(
                eq("rate:redirect:127.0.0.1"),
                any(Duration.class)
        )).thenReturn(1L);

        when(cacheService.get("url:github-link"))
                .thenReturn(null);

        Url url = createTestUrl();
        url.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(urlRepository.findByShortCode("github-link"))
                .thenReturn(Optional.of(url));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> urlService.resolveOriginalUrl(
                        "github-link",
                        httpServletRequest
                )
        );

        assertEquals(
                "Short URL has expired",
                exception.getMessage()
        );

        verify(analyticsService, never())
                .recordClick(any(), any());

        verify(cacheService, never()).set(
                any(),
                any(),
                any(Duration.class)
        );
    }

    @Test
    void deleteUrlShouldDeactivateOwnedUrl() {
        Url url = createTestUrl();

        when(urlRepository.findById(10L))
                .thenReturn(Optional.of(url));

        urlService.deleteUrl(10L, user);

        assertFalse(url.isActive());

        verify(cacheService)
                .delete("url:github-link");
    }

    private Url createTestUrl() {
        Url url = new Url();

        url.setId(10L);
        url.setUser(user);
        url.setOriginalUrl("https://www.github.com");
        url.setShortCode("github-link");
        url.setCustomAlias("github-link");
        url.setActive(true);
        url.setCreatedAt(LocalDateTime.now());

        return url;
    }
}