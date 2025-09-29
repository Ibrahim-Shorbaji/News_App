package com.global.news_app.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; 

    @Column(nullable = false)
    private String titleAr; 

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; 

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(nullable = false)
    private LocalDate publishDate = LocalDate.now();

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NewsStatus status = NewsStatus.PENDING;

    private Boolean deleted = false; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    public News() {}

    public News(String title, String titleAr, String description, String descriptionAr,
                LocalDate publishDate, String imageUrl, User author) {
        this.title = title;
        this.titleAr = titleAr;
        this.description = description;
        this.descriptionAr = descriptionAr;
        this.publishDate = publishDate;
        this.imageUrl = imageUrl;
        this.author = author;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTitleAr() { return titleAr; }
    public void setTitleAr(String titleAr) { this.titleAr = titleAr; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDescriptionAr() { return descriptionAr; }
    public void setDescriptionAr(String descriptionAr) { this.descriptionAr = descriptionAr; }

    public LocalDate getPublishDate() { return publishDate; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public NewsStatus getStatus() { return status; }
    public void setStatus(NewsStatus status) { this.status = status; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }

    public User getAuthor() { return author; }
    public void setAuthor(User writer) { this.author = writer; }
}
