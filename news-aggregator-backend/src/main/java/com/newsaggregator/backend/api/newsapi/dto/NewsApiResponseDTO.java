// src/main/java/com/newsaggregator/backend/api/newsapi/dto/NewsApiResponseDTO.java
package com.newsaggregator.backend.api.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class NewsApiResponseDTO {
    // NewsAPI.org has 'status' and 'totalResults'
    // GNews has 'totalArticles' and also implies a status through HTTP response code

    private String status; // Keep for NewsAPI.org compatibility if needed, though GNews doesn't explicitly return 'status:ok' in body.
    @JsonProperty("totalArticles") // This maps GNews's 'totalArticles' to 'totalResults' if you want to keep that field name.
    private Integer totalResults; // Renamed for GNews, or keep if you intend to use it for both.

    private List<NewsApiArticleDTO> articles;

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getTotalResults() { return totalResults; }
    public void setTotalResults(Integer totalResults) { this.totalResults = totalResults; }

    public List<NewsApiArticleDTO> getArticles() { return articles; }
    public void setArticles(List<NewsApiArticleDTO> articles) { this.articles = articles; }
}
