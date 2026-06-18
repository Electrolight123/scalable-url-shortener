package com.abhishek.urlshortener.url;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class CreateUrlRequest {
    @NotBlank
    private String originalUrl;

    private String customAlias;

    private LocalDateTime expiresAt;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}