package com.lucke.url_shortener.service.impl;

import com.lucke.url_shortener.exception.UrlExpiredException;
import com.lucke.url_shortener.exception.UrlNotFoundException;
import com.lucke.url_shortener.model.dto.UrlMappingDTO;
import com.lucke.url_shortener.model.request.UrlMappingRequest;
import com.lucke.url_shortener.model.entity.UrlMapping;
import com.lucke.url_shortener.repository.UrlMappingRepository;
import com.lucke.url_shortener.service.UrlMappingService;
import com.lucke.url_shortener.util.Base62Encoder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlMappingServiceImpl implements UrlMappingService {

    @Value("${app.base-url}")
    private String baseUrl;

    private final UrlMappingRepository urlMappingRepository;

    @Override
    @Transactional
    @Cacheable(value = "urlCache", key = "#shortCode")
    public UrlMappingDTO getUrlMappingByShortCode(String shortCode) {
        System.out.println("CACHE MISS — hitting DB for: " + shortCode);

        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("short code doesn't exist"));

        if (urlMapping.getExpiresAt() != null &&
                urlMapping.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("URL has expired");
        }

        return toDTO(urlMapping);
    }

    private UrlMappingDTO toDTO(UrlMapping urlMapping) {
        return UrlMappingDTO.builder()
                .originalUrl(urlMapping.getOriginalUrl())
                .shortCode(urlMapping.getShortCode())
                .shortUrl(baseUrl + "/api/v1/" + urlMapping.getShortCode())
                .createdAt(urlMapping.getCreatedAt())
                .expiresAt(urlMapping.getExpiresAt())
                .clickCount(urlMapping.getClickCount())
                .build();
    }

    @Override
    @Transactional
    @CachePut(value = "urlCache", key = "#result.shortCode")
    public UrlMappingDTO createShortUrl(UrlMappingRequest request) {

        UrlMapping entity = new UrlMapping();
        entity.setOriginalUrl(request.getOriginalUrl());

        entity.setExpiresAt(
                request.getExpiresAt()!=null
                        ? request.getExpiresAt()
                        : LocalDateTime.now().plusDays(30)
        );

        UrlMapping saved = urlMappingRepository.save(entity);
        String shortCode = Base62Encoder.encode(saved.getId());
        saved.setShortCode(shortCode);

        urlMappingRepository.save(saved);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public void incrementClickCount(String shortCode) {
        urlMappingRepository.incrementClickCount(shortCode);
    }

    @Override
    @Transactional
    @CacheEvict(value = "urlCache", key = "#shortCode")
    public void deleteUrl(String shortCode) {
        urlMappingRepository.findByShortCode(shortCode)
                .ifPresent(urlMappingRepository::delete);

    }

}
