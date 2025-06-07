package com.newsaggregator.backend.controller;

import com.newsaggregator.backend.dto.NewsArticleDTO;
import com.newsaggregator.backend.dto.NewsArticlePageResponse;
import com.newsaggregator.backend.service.NewsArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; // New Import
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // New Import
import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class NewsArticleController {

    private static final Logger logger = LoggerFactory.getLogger(NewsArticleController.class);

    private final NewsArticleService newsArticleService;

    @Autowired
    public NewsArticleController(NewsArticleService newsArticleService) {
        this.newsArticleService = newsArticleService;
    }

    @GetMapping
    public ResponseEntity<NewsArticlePageResponse> getAllNewsArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long sourceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate, // New param
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {   // New param

        logger.info("Received request for news articles. Page: {}, Size: {}, SortBy: {}, SortDir: {}, Keyword: '{}', CategoryId: {}, SourceId: {}, StartDate: {}, EndDate: {}",
                page, size, sortBy, sortDir, keyword, categoryId, sourceId, startDate, endDate);

        NewsArticlePageResponse response;
        // The searchNewsArticles method now handles all filtering combinations
        response = newsArticleService.searchNewsArticles(
                keyword, categoryId, sourceId, startDate, endDate, page, size, sortBy, sortDir
        );

        logger.info("Returning page {} of news articles. Total elements: {}", response.getPageNumber(), response.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsArticleDTO> getNewsArticleById(@PathVariable Long id) {
        logger.info("Received request to get news article by ID: {}", id);
        NewsArticleDTO article = newsArticleService.getNewsArticleById(id);
        logger.info("Returning news article with ID: {}", id);
        return ResponseEntity.ok(article);
    }
}