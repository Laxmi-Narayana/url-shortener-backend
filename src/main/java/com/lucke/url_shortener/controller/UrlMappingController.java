package com.lucke.url_shortener.controller;

import com.lucke.url_shortener.model.dto.UrlMappingDTO;
import com.lucke.url_shortener.model.request.UrlMappingRequest;
import com.lucke.url_shortener.service.UrlMappingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping("/urls/{shortCode}")
    public ResponseEntity<UrlMappingDTO> getStats(@PathVariable String shortCode) {
        UrlMappingDTO dto = urlMappingService.getUrlMappingByShortCode(shortCode);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/urls")
    public ResponseEntity<UrlMappingDTO> createShortUrl(
            @Valid @RequestBody UrlMappingRequest request) {
        UrlMappingDTO response = urlMappingService.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/urls")
    public ResponseEntity<Page<UrlMappingDTO>> getAllUrls(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(urlMappingService.getAllUrls(page, size));
    }

    @DeleteMapping("/urls/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        urlMappingService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build(); // 204
    }
}
