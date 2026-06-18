package com.abhishek.urlshortener.config;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

public class CacheService {
    private final StringRedisTemplate redis;

    public CacheService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public String get(String key) {
        return redis.opsForValue().get(key);
    }

    public void set(String key, String value, Duration ttl) {
        redis.opsForValue().set(key, value, ttl);
    }

    public void delete(String key) {
        redis.delete(key);
    }

    public Long increment(String key, Duration ttl) {
        Long value = redis.opsForValue().increment(key);

        if (value != null && value == 1L) {
            redis.expire(key, ttl);
        }

        return value;
    }
}