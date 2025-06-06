
// src/main/java/com/newsaggregator/backend/api/newsapi/dto/NewsApiArticleDTO.java
package com.newsaggregator.backend.api.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewsApiArticleDTO {
    private NewsApiSourceDTO source; // This looks compatible
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage; // GNews uses 'image'
    private String publishedAt; // GNews uses 'publishedAt'

    // GNews specific field, if you want to capture it
    private String content;

    // Getters and Setters
    public NewsApiSourceDTO getSource() { return source; }
    public void setSource(NewsApiSourceDTO source) { this.source = source; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    @JsonProperty("image") // Map GNews's 'image' to 'urlToImage'
    public String getUrlToImage() { return urlToImage; }
    public void setUrlToImage(String urlToImage) { this.urlToImage = urlToImage; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}

// src/main/java/com/newsaggregator/backend/api/newsapi/dto/NewsApiSourceDTO.java
// This DTO seems directly compatible for GNews as well
