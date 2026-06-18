package com.abhishek.urlshortener.analytics;

import com.abhishek.urlshortener.exception.NotFoundException;
import com.abhishek.urlshortener.url.Url;
import com.abhishek.urlshortener.url.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {
    private final ClickEventRepository clickEventRepository;
    private final UrlRepository urlRepository;

    public AnalyticsService(
            ClickEventRepository clickEventRepository,
            UrlRepository urlRepository
    ) {
        this.clickEventRepository = clickEventRepository;
        this.urlRepository = urlRepository;
    }

    @Transactional
    public void recordClick(Url url, HttpServletRequest request) {
        ClickEvent event = new ClickEvent();

        event.setUrl(url);
        event.setIpAddress(request.getRemoteAddr());
        event.setUserAgent(request.getHeader("User-Agent"));
        event.setReferrer(request.getHeader("Referer"));

        clickEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new NotFoundException("Short URL not found"));

        long totalClicks = clickEventRepository.countByUrl(url);

        var recentClicks = clickEventRepository.findTop100ByUrlOrderByClickedAtDesc(url)
                .stream()
                .map(event -> new ClickSummary(
                        event.getIpAddress(),
                        event.getUserAgent(),
                        event.getReferrer(),
                        event.getClickedAt()
                ))
                .toList();

        return new AnalyticsResponse(
                url.getShortCode(),
                url.getOriginalUrl(),
                totalClicks,
                recentClicks
        );
    }
}