package com.newsaggregator.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceDTO {
    private Long userId;
    private String username;
    private String preferredKeywords; // Comma-separated string
    private List<Long> preferredSourceIds; // List of source IDs
    private List<Long> preferredCategoryIds; // List of category IDs
}