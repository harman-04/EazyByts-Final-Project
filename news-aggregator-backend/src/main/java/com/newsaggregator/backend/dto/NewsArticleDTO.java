package com.newsaggregator.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Combines @Getter, @Setter, @ToString, @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleDTO {
    private Long id;
    private String title;
    private String description;
    private String url;
    private String imageUrl;
    private LocalDateTime publishedAt;
    private String sourceName; // Display source name directly
    private String categoryName; // Display category name directly
}