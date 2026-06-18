package com.abhishek.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitConfig {
    private int urlCreationPerHour = 20;
    private int redirectsPerMinute = 100;

    public int getUrlCreationPerHour() {
        return urlCreationPerHour;
    }

    public void setUrlCreationPerHour(int urlCreationPerHour) {
        this.urlCreationPerHour = urlCreationPerHour;
    }

    public int getRedirectsPerMinute() {
        return redirectsPerMinute;
    }

    public void setRedirectsPerMinute(int redirectsPerMinute) {
        this.redirectsPerMinute = redirectsPerMinute;
    }
}