package com.example.TinyUrl.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.example.TinyUrl.model.Url;
import com.example.TinyUrl.repository.UrlRepository;

@Service
public class UrlService {
    private final UrlRepository repository;
    private final String BASE_URL = "http://localhost:9595/";

    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }

    public String shortenUrl(String originalUrl) {
        String shortUrl = BASE_URL + generateShortCode();
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(shortUrl);
        repository.save(url);
        return shortUrl;
    }

    public String getOriginalUrl(String shortUrl) {
        Url url = repository.findByShortUrl(shortUrl);
        return (url != null) ? url.getOriginalUrl() : null;
    }

    private String generateShortCode() {
        int codeLength = 6;
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < codeLength; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}
