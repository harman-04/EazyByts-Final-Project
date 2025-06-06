package com.newsaggregator.backend.runner;

import com.newsaggregator.backend.service.NewsArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Needed for user creation
import com.newsaggregator.backend.dto.UserRegistrationDTO;
import com.newsaggregator.backend.service.UserService;


@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final NewsArticleService newsArticleService;
    private final UserService userService; // To create a test user
    private final PasswordEncoder passwordEncoder; // To hash passwords

    @Autowired
    public DataInitializer(NewsArticleService newsArticleService, UserService userService, PasswordEncoder passwordEncoder) {
        this.newsArticleService = newsArticleService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder; // You'll need to configure this bean
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Application started. Running data initialization...");

        // Ensure PasswordEncoder bean is available.
        // We'll add the security config for this in the next part.
        // For now, if you get an error here, you can comment out the userService.registerUser
        // or manually create the bean (not recommended to do it outside SecurityConfig).

        // Populate initial users
        try {
            logger.info("Attempting to register test users...");
            userService.registerUser(new UserRegistrationDTO("testuser", "test@example.com", "password123"));
            userService.registerUser(new UserRegistrationDTO("admin", "admin@example.com", "adminpass"));
            logger.info("Test users registered (if not already existing).");
        } catch (Exception e) {
            logger.warn("Could not register test users (might already exist or security not configured): {}", e.getMessage());
        }


        // Populate initial news articles, categories, and sources
//        try {
//            logger.info("Attempting to populate initial news data...");
//            newsArticleService.populateInitialData();
//            logger.info("Initial news data populated (if not already existing).");
//        } catch (Exception e) {
//            logger.error("Error populating initial news data: {}", e.getMessage(), e);
//        }

        logger.info("Data initialization complete.");
    }
}