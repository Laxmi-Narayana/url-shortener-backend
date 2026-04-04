package com.lucke.url_shortener.repository;

import com.lucke.url_shortener.model.entity.UrlMapping;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findByShortCode(String shortCode);

    @Modifying
    @Query("update UrlMapping u set u.clickCount = u.clickCount + 1 where u.shortCode = :shortCode")
    void incrementClickCount(@Param("shortCode") String shortCode);
}
