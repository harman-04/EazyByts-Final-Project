package com.newsaggregator.backend.service;

import com.newsaggregator.backend.dto.SavedArticleDTO;
import com.newsaggregator.backend.entity.NewsArticle;
import com.newsaggregator.backend.entity.User;
import com.newsaggregator.backend.entity.UserSavedArticle;
import com.newsaggregator.backend.exception.DuplicateResourceException;
import com.newsaggregator.backend.exception.ResourceNotFoundException;
import com.newsaggregator.backend.repository.NewsArticleRepository;
import com.newsaggregator.backend.repository.UserSavedArticleRepository;
import com.newsaggregator.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSavedArticleService {

    private static final Logger logger = LoggerFactory.getLogger(UserSavedArticleService.class);

    private final UserSavedArticleRepository userSavedArticleRepository;
    private final UserRepository userRepository;
    private final NewsArticleRepository newsArticleRepository;

    @Autowired
    public UserSavedArticleService(UserSavedArticleRepository userSavedArticleRepository,
                                   UserRepository userRepository,
                                   NewsArticleRepository newsArticleRepository) {
        this.userSavedArticleRepository = userSavedArticleRepository;
        this.userRepository = userRepository;
        this.newsArticleRepository = newsArticleRepository;
    }

    /**
     * Converts UserSavedArticle entity to DTO.
     */
    private SavedArticleDTO convertToDto(UserSavedArticle userSavedArticle) {
        SavedArticleDTO dto = new SavedArticleDTO();
        dto.setId(userSavedArticle.getNewsArticle().getId());
        dto.setTitle(userSavedArticle.getNewsArticle().getTitle());
        dto.setUrl(userSavedArticle.getNewsArticle().getUrl());
        dto.setImageUrl(userSavedArticle.getNewsArticle().getImageUrl());
        dto.setSourceName(userSavedArticle.getNewsArticle().getSource() != null ? userSavedArticle.getNewsArticle().getSource().getName() : null);
        dto.setSavedAt(userSavedArticle.getSavedAt());
        return dto;
    }

    @Transactional
    public SavedArticleDTO saveArticleForUser(Long userId, Long articleId) {
        logger.info("Attempting to save article ID: {} for user ID: {}", articleId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User with ID: {} not found for saving article.", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });

        NewsArticle newsArticle = newsArticleRepository.findById(articleId)
                .orElseThrow(() -> {
                    logger.warn("News article with ID: {} not found to be saved.", articleId);
                    return new ResourceNotFoundException("NewsArticle", "id", articleId);
                });

        if (userSavedArticleRepository.existsByUserAndNewsArticle(user, newsArticle)) {
            logger.warn("Article ID: {} is already saved by user ID: {}. Skipping.", articleId, userId);
            throw new DuplicateResourceException("Saved Article", "user and article", "User " + userId + " saved Article " + articleId);
        }

        UserSavedArticle userSavedArticle = new UserSavedArticle();
        userSavedArticle.setUser(user);
        userSavedArticle.setNewsArticle(newsArticle);
        userSavedArticle.setSavedAt(LocalDateTime.now()); // Set savedAt explicitly or rely on @PrePersist

        UserSavedArticle saved = userSavedArticleRepository.save(userSavedArticle);
        logger.info("Article ID: {} saved successfully for user ID: {}.", articleId, userId);
        return convertToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<SavedArticleDTO> getSavedArticlesForUser(Long userId) {
        logger.debug("Fetching saved articles for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User with ID: {} not found for retrieving saved articles.", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });

        List<UserSavedArticle> savedArticles = userSavedArticleRepository.findByUser(user);
        return savedArticles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeSavedArticle(Long userId, Long articleId) {
        logger.info("Attempting to remove saved article ID: {} for user ID: {}", articleId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User with ID: {} not found for removing saved article.", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });

        NewsArticle newsArticle = newsArticleRepository.findById(articleId)
                .orElseThrow(() -> {
                    logger.warn("News article with ID: {} not found for removing saved article.", articleId);
                    return new ResourceNotFoundException("NewsArticle", "id", articleId);
                });

        UserSavedArticle userSavedArticle = userSavedArticleRepository.findByUserAndNewsArticle(user, newsArticle)
                .orElseThrow(() -> {
                    logger.warn("Article ID: {} was not found as saved by user ID: {}. Cannot remove.", articleId, userId);
                    return new ResourceNotFoundException("Saved Article", "user and article", "User " + userId + " Article " + articleId);
                });

        userSavedArticleRepository.delete(userSavedArticle);
        logger.info("Article ID: {} successfully removed from saved for user ID: {}.", articleId, userId);
    }
}