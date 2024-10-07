package com.example.TinyUrl.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public String shortenUrl(String originalUrl, Long expirationMinutes) {
        // Check for invalid expiration times (negative or zero)
        if (expirationMinutes != null && expirationMinutes <= 0) {
            throw new IllegalArgumentException("Expiration time must be greater than 0.");
        }

        String shortUrl = BASE_URL + generateShortCode();
        LocalDateTime expirationDate = calculateExpiration(expirationMinutes);

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(shortUrl);
        url.setExpirationDate(expirationDate);
        repository.save(url);

        return shortUrl;
    }

    public String shortenUrlWithCustom(String originalUrl, String customUrl, Long expirationMinutes) {
        // Check for invalid expiration times (negative or zero)
        if (expirationMinutes != null && expirationMinutes <= 0) {
            throw new IllegalArgumentException("Expiration time must be greater than 0.");
        }

        String fullCustomUrl = BASE_URL + customUrl;

        // Check if the custom URL is already in use
        if (repository.findByShortUrl(fullCustomUrl) != null) {
            throw new IllegalArgumentException("Custom short URL '" + customUrl + "' is already in use!");
        }

        // Set the expiration date
        LocalDateTime expirationDate = calculateExpiration(expirationMinutes);

        // Save the custom URL with expiration
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(fullCustomUrl);
        url.setExpirationDate(expirationDate);
        repository.save(url);

        return fullCustomUrl;
    }


    public String getOriginalUrl(String shortUrl) {
        Url url = repository.findByShortUrl(shortUrl);
        
        // Check if the URL is expired
        if (url != null && url.getExpirationDate().isAfter(LocalDateTime.now())) {
            return url.getOriginalUrl();
        }

        return null; // Return null if the URL is expired or not found
    }

    private LocalDateTime calculateExpiration(Long expirationMinutes) {
        if (expirationMinutes == null || expirationMinutes <= 0) {
            return LocalDateTime.now().plus(12, ChronoUnit.HOURS); // Default to 12 hours
        }
        return LocalDateTime.now().plus(expirationMinutes, ChronoUnit.MINUTES);
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
