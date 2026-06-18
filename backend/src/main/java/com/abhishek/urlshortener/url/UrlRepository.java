package com.abhishek.urlshortener.url;

import com.abhishek.urlshortener.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    boolean existsByCustomAlias(String customAlias);

    List<Url> findByUserOrderByCreatedAtDesc(User user);
}