package com.lucke.url_shortener.service;

import com.lucke.url_shortener.model.dto.UrlMappingDTO;
import com.lucke.url_shortener.model.request.UrlMappingRequest;
import io.lettuce.core.dynamic.annotation.Param;

public interface UrlMappingService {

    UrlMappingDTO getUrlMappingByShortCode(String shortCode);
    UrlMappingDTO createShortUrl(UrlMappingRequest request);
    void incrementClickCount(@Param("shortCode") String shortCode);
    void deleteUrl(String shortCode);
}