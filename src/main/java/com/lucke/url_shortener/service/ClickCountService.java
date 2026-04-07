package com.lucke.url_shortener.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickCountService {

    private static final String CLICK_KEY_PREFIX = "clicks:";

    private final StringRedisTemplate stringRedisTemplate;

    public void increment(String shortCode) {
        String key = CLICK_KEY_PREFIX + shortCode;
        stringRedisTemplate.opsForValue().increment(key);
        log.debug("Incremented click count in Redis for: {}", shortCode);
    }

    public Long getCount(String shortCode) {
        String key = CLICK_KEY_PREFIX + shortCode;
        String val = stringRedisTemplate.opsForValue().get(key);
        return val != null ? Long.parseLong(val) : 0L;
    }

    public Long getBufferedCount(String shortCode) {
        String val = stringRedisTemplate.opsForValue().get(CLICK_KEY_PREFIX + shortCode);
        return val != null ? Long.parseLong(val) : 0L;
    }
}