package com.example.TinyUrl.controller;

import org.springframework.http.ResponseEntity;
// Add imports for ResponseEntity
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.example.TinyUrl.service.UrlService;

// Add the Controller annotation
@Controller
public class UrlController {
    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @GetMapping("/") // Entry point to the home page
    public String home() {
        return "index"; // Returns the Thymeleaf template name
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(
        @RequestParam String originalUrl, 
        @RequestParam(required = false) String customUrl,
        @RequestParam(required = false) Long expirationMinutes) {

        try {
            String shortUrl;
            if (customUrl != null && !customUrl.isEmpty()) {
                shortUrl = service.shortenUrlWithCustom(originalUrl, customUrl, expirationMinutes);
            } else {
                shortUrl = service.shortenUrl(originalUrl, expirationMinutes);
            }
            return ResponseEntity.ok(shortUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Send error message to the frontend
        }
    }




    @GetMapping("/{shortUrl}")
    public RedirectView redirectToOriginal(@PathVariable String shortUrl) {
        String fullShortUrl = "http://localhost:9595/" + shortUrl;
        String originalUrl = service.getOriginalUrl(fullShortUrl);

        if (originalUrl != null) {
            return new RedirectView(originalUrl);  // Redirects to the original URL
        } else {
            return new RedirectView("/error");  // Redirects to an error page
        }
    }
}
