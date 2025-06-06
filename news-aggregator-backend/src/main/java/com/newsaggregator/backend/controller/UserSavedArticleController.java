package com.newsaggregator.backend.controller;

import com.newsaggregator.backend.dto.SavedArticleDTO;
import com.newsaggregator.backend.service.UserSavedArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/saved-articles")
public class UserSavedArticleController {

    private static final Logger logger = LoggerFactory.getLogger(UserSavedArticleController.class);

    private final UserSavedArticleService userSavedArticleService;

    @Autowired
    public UserSavedArticleController(UserSavedArticleService userSavedArticleService) {
        this.userSavedArticleService = userSavedArticleService;
    }

    // IMPORTANT: For now, userId is hardcoded or passed as a path variable.
    // In actual production, this userId would come from the authenticated user's JWT token.
    @PostMapping("/{articleId}")
    public ResponseEntity<SavedArticleDTO> saveArticle(
            @PathVariable Long userId,
            @PathVariable Long articleId) {
        logger.info("Received request to save article ID: {} for user ID: {}", articleId, userId);
        SavedArticleDTO savedArticle = userSavedArticleService.saveArticleForUser(userId, articleId);
        logger.info("Article ID: {} saved for user ID: {}", articleId, userId);
        return new ResponseEntity<>(savedArticle, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SavedArticleDTO>> getSavedArticles(@PathVariable Long userId) {
        logger.info("Received request to get saved articles for user ID: {}", userId);
        List<SavedArticleDTO> savedArticles = userSavedArticleService.getSavedArticlesForUser(userId);
        logger.info("Returning {} saved articles for user ID: {}", savedArticles.size(), userId);
        return ResponseEntity.ok(savedArticles);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> removeSavedArticle(
            @PathVariable Long userId,
            @PathVariable Long articleId) {
        logger.info("Received request to remove saved article ID: {} for user ID: {}", articleId, userId);
        userSavedArticleService.removeSavedArticle(userId, articleId);
        logger.info("Article ID: {} removed from saved for user ID: {}", articleId, userId);
        return ResponseEntity.noContent().build();
    }
}