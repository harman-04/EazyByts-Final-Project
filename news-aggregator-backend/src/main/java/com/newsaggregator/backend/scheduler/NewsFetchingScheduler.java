package com.newsaggregator.backend.scheduler;

import com.newsaggregator.backend.api.newsapi.NewsApiClient;
import com.newsaggregator.backend.api.newsapi.dto.NewsApiArticleDTO;
import com.newsaggregator.backend.api.newsapi.dto.NewsApiResponseDTO;
import com.newsaggregator.backend.service.NewsArticleService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime; // Import LocalDateTime
import java.time.format.DateTimeFormatter; // Import DateTimeFormatter
import java.time.format.DateTimeParseException; // Import DateTimeParseException
import java.util.List;

@Component
public class NewsFetchingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NewsFetchingScheduler.class);

    private final NewsApiClient newsApiClient;
    private final NewsArticleService newsArticleService;

    @Value("${news.fetching.enabled:false}")
    private boolean fetchingEnabled;

    @Value("${gnews.default-query}")
    private String defaultQuery;

    @Value("${gnews.articles-per-page:10}") // Corrected default value here too
    private int articlesPerPage;

    @Value("${gnews.max-pages:1}")
    private int maxPages;

    // Define a formatter for ISO 8601 format (e.g., "2024-01-01T10:00:00Z")
    // Use ISO_OFFSET_DATE_TIME or ISO_INSTANT if the string includes a 'Z' or offset
    // For GNews, 'publishedAt' seems to be like "2024-06-06T20:00:00Z", which is ISO_INSTANT or compatible with ISO_DATE_TIME if you parse it carefully.
    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    // Or, a more lenient one if ISO_OFFSET_DATE_TIME gives issues with 'Z'
    // private static final DateTimeFormatter CUSTOM_ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");


    @Autowired
    public NewsFetchingScheduler(NewsApiClient newsApiClient, NewsArticleService newsArticleService) {
        this.newsApiClient = newsApiClient;
        this.newsArticleService = newsArticleService;
    }

    @PostConstruct
    public void init() {
        if (!fetchingEnabled) {
            logger.warn("News fetching scheduler is DISABLED. Set 'news.fetching.enabled=true' in application.properties to enable.");
        } else {
            logger.info("News fetching scheduler is ENABLED. Will fetch news based on cron expression.");
        }
    }

    @Scheduled(cron = "${news.fetching.cron}")
    public void scheduledNewsFetch() {
        if (!fetchingEnabled) {
            logger.info("News fetching is disabled, skipping scheduled run.");
            return;
        }
        logger.info("Starting scheduled news fetch...");
        fetchAndSaveNewsArticles();
        logger.info("Scheduled news fetch completed.");
    }

    public void fetchAndSaveNewsArticles() {
        for (int page = 1; page <= maxPages; page++) {
            try {
                NewsApiResponseDTO response = newsApiClient.fetchTopHeadlines(defaultQuery, articlesPerPage, page);
                if (response != null && response.getArticles() != null) {
                    List<NewsApiArticleDTO> articles = response.getArticles();
                    logger.info("Processing {} articles from GNews (page {}/{})", articles.size(), page, maxPages);
                    for (NewsApiArticleDTO apiArticle : articles) {
                        // Validate minimum required fields
                        if (apiArticle.getTitle() == null || apiArticle.getTitle().trim().isEmpty() ||
                                apiArticle.getUrl() == null || apiArticle.getUrl().trim().isEmpty() ||
                                apiArticle.getPublishedAt() == null || // This is the field we need to parse
                                apiArticle.getSource() == null || apiArticle.getSource().getName() == null || apiArticle.getSource().getName().trim().isEmpty()) {
                            logger.warn("Skipping incomplete article: Title='{}', URL='{}', PublishedAt='{}', Source='{}'",
                                    apiArticle.getTitle(), apiArticle.getUrl(), apiArticle.getPublishedAt(),
                                    apiArticle.getSource() != null ? apiArticle.getSource().getName() : "null");
                            continue;
                        }

                        // --- START OF FIX ---
                        LocalDateTime publishedDateTime = null;
                        try {
                            // Attempt to parse the publishedAt string into LocalDateTime
                            // NewsAPI.org and GNews often use ISO 8601 with 'Z' for Zulu time (UTC)
                            // ISO_OFFSET_DATE_TIME handles 'Z' and other offsets.
                            publishedDateTime = LocalDateTime.parse(apiArticle.getPublishedAt(), ISO_DATE_TIME_FORMATTER);
                        } catch (DateTimeParseException e) {
                            logger.error("Failed to parse publishedAt date string '{}' for article '{}'. Error: {}",
                                    apiArticle.getPublishedAt(), apiArticle.getTitle(), e.getMessage());
                            // If parsing fails, you might choose to skip the article,
                            // or set a default/current time, or handle it based on your requirements.
                            // For now, we'll log and continue to the next article if parsing is essential.
                            continue; // Skip this article if date cannot be parsed
                        }
                        // --- END OF FIX ---

                        // Determine category
                        String categoryName = determineCategory(apiArticle.getTitle() + " " + apiArticle.getDescription());

                        newsArticleService.saveNewsArticle(
                                apiArticle.getTitle(),
                                apiArticle.getDescription(),
                                apiArticle.getUrl(),
                                apiArticle.getUrlToImage(),
                                publishedDateTime, // Pass the parsed LocalDateTime object
                                apiArticle.getSource().getName(),
                                categoryName
                        );
                    }
                }
            } catch (Exception e) {
                logger.error("Error fetching or saving news for page {}: {}", page, e.getMessage(), e);
            }
        }
    }

    private String determineCategory(String text) {
        String lowerCaseText = text.toLowerCase();
        if (lowerCaseText.contains("tech") || lowerCaseText.contains("apple") || lowerCaseText.contains("google") || lowerCaseText.contains("software") || lowerCaseText.contains("ai")) {
            return "Technology";
        } else if (lowerCaseText.contains("politic") || lowerCaseText.contains("government") || lowerCaseText.contains("election") || lowerCaseText.contains("parliament")) {
            return "Politics";
        } else if (lowerCaseText.contains("sport") || lowerCaseText.contains("football") || lowerCaseText.contains("basketball") || lowerCaseText.contains("game") || lowerCaseText.contains("match")) {
            return "Sports";
        } else if (lowerCaseText.contains("business") || lowerCaseText.contains("market") || lowerCaseText.contains("economy") || lowerCaseText.contains("finance")) {
            return "Business";
        } else if (lowerCaseText.contains("health") || lowerCaseText.contains("medical") || lowerCaseText.contains("disease") || lowerCaseText.contains("vaccine")) {
            return "Health";
        } else if (lowerCaseText.contains("science") || lowerCaseText.contains("research") || lowerCaseText.contains("discover") || lowerCaseText.contains("astronomy")) {
            return "Science";
        } else {
            return "General";
        }
    }
}