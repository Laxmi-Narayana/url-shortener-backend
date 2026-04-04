package com.lucke.url_shortener.controller;

import com.lucke.url_shortener.model.dto.UrlMappingDTO;
import com.lucke.url_shortener.model.request.UrlMappingRequest;
import com.lucke.url_shortener.service.UrlMappingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UrlMappingController {
    private final UrlMappingService urlMappingService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        UrlMappingDTO urlMappingDTO = urlMappingService.getUrlMappingByShortCode(shortCode);
        urlMappingService.incrementClickCount(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location",urlMappingDTO.getOriginalUrl())
                .build();
    }

    @PostMapping("/urls")
    public ResponseEntity<UrlMappingDTO> createShortUrl(
            @Valid @RequestBody UrlMappingRequest request) {
        UrlMappingDTO response = urlMappingService.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        urlMappingService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build(); // 204
    }
}
