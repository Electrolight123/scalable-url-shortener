package com.abhishek.urlshortener.analytics;

import com.abhishek.urlshortener.url.Url;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "click_events")
public class ClickEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "url_id")
    private Url url;

    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String userAgent;

    @Column(columnDefinition = "TEXT")
    private String referrer;

    @Column(nullable = false)
    private LocalDateTime clickedAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public Url getUrl() {
        return url;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public void setClickedAt(LocalDateTime clickedAt) {
        this.clickedAt = clickedAt;
    }
}