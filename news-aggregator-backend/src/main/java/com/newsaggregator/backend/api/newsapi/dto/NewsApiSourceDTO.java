package com.newsaggregator.backend.api.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewsApiSourceDTO {
    private String id; // GNews might not have 'id' or it might be null
    private String name; // GNews has 'name'

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}