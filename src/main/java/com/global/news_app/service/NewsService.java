package com.global.news_app.service;

import com.global.news_app.mapper.NewsMapper;
import com.global.news_app.models.*;
import com.global.news_app.payload.request.NewsRequest;
import com.global.news_app.payload.response.NewsResponse;
import com.global.news_app.repository.NewsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public NewsResponse createNews(NewsRequest newsRequest, User author) {
        News news = NewsMapper.toEntity(newsRequest, author);
        news.setStatus(NewsStatus.PENDING);
        News saved = newsRepository.save(news);
        return NewsMapper.toDto(saved);
    }

    public List<NewsResponse> getAllNews() {
        return newsRepository.findByIsDeletedFalse()
                .stream()
                .map(NewsMapper::toDto)
                .toList();
    }

    public List<NewsResponse> getApprovedNews() {
        return newsRepository.findByStatusAndIsDeletedFalse(NewsStatus.APPROVED.name())
                .stream()
                .map(NewsMapper::toDto)
                .toList();
    }

    public List<NewsResponse> getPendingNews() {
        return newsRepository.findByStatusAndIsDeletedFalse(NewsStatus.PENDING.name())
                .stream()
                .map(NewsMapper::toDto)
                .toList();
    }

    public NewsResponse approveNews(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        news.setStatus(NewsStatus.APPROVED);
        return NewsMapper.toDto(newsRepository.save(news));
    }

    public NewsResponse rejectNews(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        news.setStatus(NewsStatus.REJECTED);
        return NewsMapper.toDto(newsRepository.save(news));
    }

    public void deleteNews(Long id, User user) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));

        Set<ERole> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        if (roles.contains(ERole.ROLE_ADMIN)) {
            newsRepository.deleteById(id);
        } else if (roles.contains(ERole.ROLE_WRITER)) {
            if (news.getStatus() == NewsStatus.PENDING) {
                newsRepository.deleteById(id);
            } else {
                throw new RuntimeException("Writers can only delete pending news");
            }
        } else {
            throw new RuntimeException("You are not authorized to delete news");
        }
    }

    public void softDeleteExpiredNews() {
        LocalDate today = LocalDate.now();
        List<News> expired = newsRepository.findExpiredNews(today);

        expired.forEach(news -> {
            news.setDeleted(true);
            newsRepository.save(news);
        });
    }
}
