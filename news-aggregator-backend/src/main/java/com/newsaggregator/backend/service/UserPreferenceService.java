package com.newsaggregator.backend.service;

import com.newsaggregator.backend.dto.UserPreferenceDTO;
import com.newsaggregator.backend.entity.Category;
import com.newsaggregator.backend.entity.Source;
import com.newsaggregator.backend.entity.User;
import com.newsaggregator.backend.entity.UserPreference;
import com.newsaggregator.backend.exception.ResourceNotFoundException;
import com.newsaggregator.backend.repository.UserPreferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserPreferenceService {

    private static final Logger logger = LoggerFactory.getLogger(UserPreferenceService.class);

    private final UserPreferenceRepository userPreferenceRepository;
    private final UserService userService; // To get User entity
    private final SourceService sourceService; // To get Source entities
    private final CategoryService categoryService; // To get Category entities

    @Autowired
    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository,
                                 UserService userService,
                                 SourceService sourceService,
                                 CategoryService categoryService) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.userService = userService;
        this.sourceService = sourceService;
        this.categoryService = categoryService;
    }

    /**
     * Converts UserPreference entity to DTO.
     */
    private UserPreferenceDTO convertToDto(UserPreference userPreference) {
        UserPreferenceDTO dto = new UserPreferenceDTO();
        dto.setUserId(userPreference.getUser().getId());
        dto.setUsername(userPreference.getUser().getUsername());
        dto.setPreferredKeywords(userPreference.getPreferredKeywords());
        dto.setPreferredSourceIds(userPreference.getPreferredSources().stream()
                .map(Source::getId)
                .collect(Collectors.toList()));
        dto.setPreferredCategoryIds(userPreference.getPreferredCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toList()));
        return dto;
    }

    @Transactional(readOnly = true)
    public UserPreferenceDTO getUserPreferences(Long userId) {
        logger.debug("Fetching preferences for user ID: {}", userId);
        User user = userService.findUserById(userId)
                .orElseThrow(() -> {
                    logger.warn("User with ID: {} not found for fetching preferences.", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });

        UserPreference userPreference = userPreferenceRepository.findByUser(user)
                .orElseGet(() -> {
                    logger.info("No preferences found for user ID: {}, creating default.", userId);
                    return createDefaultUserPreference(user); // Create a default if not found
                });
        return convertToDto(userPreference);
    }

    @Transactional
    public UserPreferenceDTO updateOrCreateUserPreferences(Long userId, UserPreferenceDTO preferenceDTO) {
        logger.info("Updating/creating preferences for user ID: {}", userId);
        User user = userService.findUserById(userId)
                .orElseThrow(() -> {
                    logger.warn("User with ID: {} not found for updating preferences.", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });

        UserPreference userPreference = userPreferenceRepository.findByUser(user)
                .orElseGet(() -> {
                    logger.info("No existing preferences found for user ID: {}, creating new one.", userId);
                    return new UserPreference(); // Create new if not exists
                });

        userPreference.setUser(user);
        userPreference.setPreferredKeywords(preferenceDTO.getPreferredKeywords());

        // Set preferred sources
        Set<Source> preferredSources = new HashSet<>();
        if (preferenceDTO.getPreferredSourceIds() != null) {
            for (Long sourceId : preferenceDTO.getPreferredSourceIds()) {
                sourceService.getSourceById(sourceId).ifPresent(preferredSources::add);
            }
        }
        userPreference.setPreferredSources(preferredSources);

        // Set preferred categories
        Set<Category> preferredCategories = new HashSet<>();
        if (preferenceDTO.getPreferredCategoryIds() != null) {
            for (Long categoryId : preferenceDTO.getPreferredCategoryIds()) {
                categoryService.getCategoryById(categoryId).ifPresent(preferredCategories::add);
            }
        }
        userPreference.setPreferredCategories(preferredCategories);

        UserPreference savedPreference = userPreferenceRepository.save(userPreference);
        logger.info("User preferences for user ID: {} updated successfully.", userId);
        return convertToDto(savedPreference);
    }

    @Transactional
    public UserPreference createDefaultUserPreference(User user) {
        UserPreference newPreference = new UserPreference();
        newPreference.setUser(user);
        newPreference.setPreferredKeywords(""); // Empty by default
        // Add default sources/categories if desired, or leave empty
        UserPreference savedPreference = userPreferenceRepository.save(newPreference);
        logger.info("Default preferences created for new user: {}", user.getUsername());
        return savedPreference;
    }
}