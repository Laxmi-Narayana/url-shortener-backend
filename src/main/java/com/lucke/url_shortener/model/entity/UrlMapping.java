package com.lucke.url_shortener.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "url_mapping",
        indexes = {
                @Index(name = "idx_short_code", columnList = "short_code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="original_url", nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(name="short_code",unique=true, length = 10)
    private String shortCode;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="expires_at")
    private LocalDateTime expiresAt;

    @Column(name="click_count", nullable = false)
    private Long clickCount;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.clickCount == null) {
            this.clickCount = 0L;
        }
    }

}
