package com.global.news_app.controllers;

import com.global.news_app.models.User;
import com.global.news_app.payload.request.NewsRequest;
import com.global.news_app.payload.response.NewsResponse;
import com.global.news_app.security.services.UserDetailsImpl;
import com.global.news_app.service.NewsService;
import com.global.news_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final UserService userService;

    public NewsController(NewsService newsService, UserService userService) {
        this.newsService = newsService;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('WRITER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<NewsResponse> createNews(
            @Valid @RequestBody NewsRequest newsRequest,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        User userEntity = userService.getUserByEmail(currentUser.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(newsService.createNews(newsRequest, userEntity));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<NewsResponse>> getAllNews() {
        newsService.softDeleteExpiredNews();
        return ResponseEntity.ok(newsService.getAllNews());
    }

    @PreAuthorize("hasAnyRole('NORMAL','ADMIN')")
    @GetMapping("/approved")
    public ResponseEntity<List<NewsResponse>> getApprovedNews() {
        newsService.softDeleteExpiredNews();
        return ResponseEntity.ok(newsService.getApprovedNews());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<NewsResponse>> getPendingNews() {
        newsService.softDeleteExpiredNews();
        return ResponseEntity.ok(newsService.getPendingNews());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<NewsResponse> approveNews(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.approveNews(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<NewsResponse> rejectNews(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.rejectNews(id));
    }

    @PreAuthorize("hasAnyRole('WRITER','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNews(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        User userEntity = userService.getUserByEmail(currentUser.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        newsService.deleteNews(id, userEntity);
        return ResponseEntity.ok("News deleted successfully.");
    }
}
