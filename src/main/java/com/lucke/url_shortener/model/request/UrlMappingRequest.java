package com.lucke.url_shortener.model.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UrlMappingRequest {
    @NotBlank(message = "Original URL cannot be empty")
    @Size(min = 10, message = "URL must be at least 10 characters long")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "URL must start with http:// or https://"
    )
    private String originalUrl;
    private LocalDateTime expiresAt;
}
