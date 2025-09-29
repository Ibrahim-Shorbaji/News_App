package com.global.news_app.payload.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class NewsResponse {
    private Long id;
    private String title;
    private String titleAr;
    private String description;
    private String descriptionAr;
    private LocalDate publishDate;
    private String imageUrl;
    private String status;      // PENDING / APPROVED
    private String authorName;  // اسم الكاتب

    public NewsResponse(Long id, String title, String titleAr, String description, String descriptionAr,
                        LocalDate publishDate, String imageUrl, String status, String authorName) {
        this.id = id;
        this.title = title;
        this.titleAr = titleAr;
        this.description = description;
        this.descriptionAr = descriptionAr;
        this.publishDate = publishDate;
        this.imageUrl = imageUrl;
        this.status = status;
        this.authorName = authorName;
    }

    // Getters only (read-only response)
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getTitleAr() { return titleAr; }
    public String getDescription() { return description; }
    public String getDescriptionAr() { return descriptionAr; }
    public LocalDate getPublishDate() { return publishDate; }
    public String getImageUrl() { return imageUrl; }
    public String getStatus() { return status; }
    public String getAuthorName() { return authorName; }
}
