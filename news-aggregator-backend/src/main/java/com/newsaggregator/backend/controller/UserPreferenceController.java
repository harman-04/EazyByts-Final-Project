package com.newsaggregator.backend.controller;

import com.newsaggregator.backend.dto.UserPreferenceDTO;
import com.newsaggregator.backend.service.UserPreferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/preferences")
public class UserPreferenceController {

    private static final Logger logger = LoggerFactory.getLogger(UserPreferenceController.class);

    private final UserPreferenceService userPreferenceService;

    @Autowired
    public UserPreferenceController(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

    // IMPORTANT: For now, userId is hardcoded or passed as a path variable.
    // In actual production, this userId would come from the authenticated user's JWT token.
    @GetMapping
    public ResponseEntity<UserPreferenceDTO> getUserPreferences(@PathVariable Long userId) {
        logger.info("Received request to get preferences for user ID: {}", userId);
        UserPreferenceDTO preferences = userPreferenceService.getUserPreferences(userId);
        logger.info("Returning preferences for user ID: {}", userId);
        return ResponseEntity.ok(preferences);
    }

    @PutMapping
    public ResponseEntity<UserPreferenceDTO> updateOrCreateUserPreferences(
            @PathVariable Long userId,
            @RequestBody UserPreferenceDTO preferenceDTO) {
        logger.info("Received request to update/create preferences for user ID: {}", userId);
        UserPreferenceDTO updatedPreferences = userPreferenceService.updateOrCreateUserPreferences(userId, preferenceDTO);
        logger.info("Preferences for user ID: {} updated/created successfully.", userId);
        return ResponseEntity.ok(updatedPreferences);
    }
}