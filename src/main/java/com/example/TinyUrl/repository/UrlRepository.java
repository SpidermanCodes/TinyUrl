package com.example.TinyUrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.TinyUrl.model.Url;

public interface UrlRepository extends JpaRepository<Url, Long> {
    
    Url findByShortUrl(String shortUrl);
}