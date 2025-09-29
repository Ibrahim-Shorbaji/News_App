package com.global.news_app.mapper;

import com.global.news_app.models.News;
import com.global.news_app.models.User;
import com.global.news_app.payload.request.NewsRequest;
import com.global.news_app.payload.response.NewsResponse;

public class NewsMapper {

    public static News toEntity(NewsRequest dto, User author) {
        News news = new News();
        news.setTitle(dto.getTitle());
        news.setTitleAr(dto.getTitleAr());
        news.setDescription(dto.getDescription());
        news.setDescriptionAr(dto.getDescriptionAr());
        news.setImageUrl(dto.getImageUrl());
        news.setAuthor(author);
        return news;
    }

    public static NewsResponse toDto(News news) {
        return new NewsResponse(
                news.getId(),
                news.getTitle(),
                news.getTitleAr(),
                news.getDescription(),
                news.getDescriptionAr(),
                news.getPublishDate(),
                news.getImageUrl(),
                news.getStatus().name(),
                news.getAuthor().getUsername()
        );
    }
}
