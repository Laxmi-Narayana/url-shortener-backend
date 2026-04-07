package com.lucke.url_shortener.scheduler;

import com.lucke.url_shortener.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClickSyncScheduler {

    private static final String CLICK_KEY_PREFIX = "clicks:";

    private final StringRedisTemplate stringRedisTemplate;
    private final UrlMappingRepository urlMappingRepository;
    private final CacheManager cacheManager;

    @Scheduled(fixedDelay = 120_000) // every 2 minutes
    @Transactional
    public void flushClickCountsToDb() {
        Set<String> keys = stringRedisTemplate.keys(CLICK_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) return;

        log.info("Flushing click counts for {} short codes", keys.size());

        for (String key : keys) {
            String val = stringRedisTemplate.opsForValue().getAndDelete(key);
            if (val == null) continue;

            long delta = Long.parseLong(val);
            String shortCode = key.substring(CLICK_KEY_PREFIX.length());

            urlMappingRepository.incrementClickCountBy(shortCode, delta);

            // evict cache so next read gets fresh clickCount from DB
            var cache = cacheManager.getCache("urlCache");
            if (cache != null) cache.evict(shortCode);

            log.debug("Flushed {} clicks for shortCode: {}", delta, shortCode);
        }
    }
}