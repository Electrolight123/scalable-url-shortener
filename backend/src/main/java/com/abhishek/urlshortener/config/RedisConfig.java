package com.abhishek.urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {
    @Bean
    public CacheService cacheService(StringRedisTemplate redisTemplate) {
        return new CacheService(redisTemplate);
    }
}