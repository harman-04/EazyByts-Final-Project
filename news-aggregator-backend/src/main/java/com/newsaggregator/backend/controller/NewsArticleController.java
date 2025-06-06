package com.newsaggregator.backend.controller;

import com.newsaggregator.backend.dto.NewsArticleDTO;
import com.newsaggregator.backend.service.NewsArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<NewsArticleDTO>> getAllNewsArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long sourceId) {

        logger.info("Received request for news articles. Page: {}, Size: {}, Keyword: '{}', CategoryId: {}, SourceId: {}",
                page, size, keyword, categoryId, sourceId);

        List<NewsArticleDTO> articles;
        if (keyword != null || categoryId != null || sourceId != null) {
            articles = newsArticleService.searchNewsArticles(keyword, categoryId, sourceId, page, size);
        } else {
            articles = newsArticleService.getAllNewsArticles(page, size);
        }

        logger.info("Returning {} news articles.", articles.size());
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsArticleDTO> getNewsArticleById(@PathVariable Long id) {
        logger.info("Received request to get news article by ID: {}", id);
        NewsArticleDTO article = newsArticleService.getNewsArticleById(id);
        logger.info("Returning news article with ID: {}", id);
        return ResponseEntity.ok(article);
    }
}