package com.abhishek.urlshortener.analytics;

import java.time.LocalDateTime;

public class ClickSummary {
    private String ipAddress;
    private String userAgent;
    private String referrer;
    private LocalDateTime clickedAt;

    public ClickSummary(
            String ipAddress,
            String userAgent,
            String referrer,
            LocalDateTime clickedAt
    ) {
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.referrer = referrer;
        this.clickedAt = clickedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getReferrer() {
        return referrer;
    }

    public LocalDateTime getClickedAt() {
        return clickedAt;
    }
}