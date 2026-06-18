package com.abhishek.urlshortener.analytics;

import com.abhishek.urlshortener.url.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    long countByUrl(Url url);

    List<ClickEvent> findTop100ByUrlOrderByClickedAtDesc(Url url);
}