package com.lucke.url_shortener.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UrlMappingDTO implements Serializable {
    private String originalUrl;
    private String shortCode;
    private String shortUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Long clickCount;
}
