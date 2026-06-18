package com.abhishek.urlshortener.url;

import java.time.LocalDateTime;

public class UrlResponse {
    private Long id;
    private String originalUrl;
    private String shortCode;
    private String shortUrl;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public UrlResponse(
            Long id,
            String originalUrl,
            String shortCode,
            String shortUrl,
            LocalDateTime expiresAt,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}