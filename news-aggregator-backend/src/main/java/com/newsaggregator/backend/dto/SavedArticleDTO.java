package com.newsaggregator.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedArticleDTO {
    private Long id; // ID of the NewsArticle
    private String title;
    private String url;
    private String imageUrl;
    private String sourceName;
    private LocalDateTime savedAt; // When the user saved it
}