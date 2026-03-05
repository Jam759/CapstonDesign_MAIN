package com.Hoseo.CapstoneDesign.global.config;

import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {

    @Bean
    public Cache<String, Object> simpleCache() {
        return Caffeine.newBuilder()
                .recordStats()
                .maximumSize(100_000)
                .expireAfterWrite(Duration.ofSeconds(60))
                .build();
    }

    @Bean
    public Cache<String, AccessTokenBlackListCache> accessTokenCache() {
        return Caffeine.newBuilder()
                .recordStats()
                .maximumSize(10_000)
                .expireAfter(new Expiry<String, AccessTokenBlackListCache>() {
                    @Override
                    public long expireAfterCreate(String key, AccessTokenBlackListCache value, long currentTime) {
                        return value.getTtlMillis() != null
                                ? TimeUnit.MILLISECONDS.toNanos(value.getTtlMillis())
                                : TimeUnit.MILLISECONDS.toNanos(60_000);
                    }

                    @Override
                    public long expireAfterUpdate(String key, AccessTokenBlackListCache value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(String key, AccessTokenBlackListCache value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .build();
    }

}
