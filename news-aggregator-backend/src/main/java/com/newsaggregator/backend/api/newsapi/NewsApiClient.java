package com.newsaggregator.backend.api.newsapi; // Keep package name for now

import com.newsaggregator.backend.api.newsapi.dto.NewsApiResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NewsApiClient {

    private static final Logger logger = LoggerFactory.getLogger(NewsApiClient.class);
    private final RestTemplate restTemplate;

    @Value("${gnews.base-url}") // Updated property name
    private String baseUrl;

    @Value("${gnews.api-key}") // Updated property name
    private String apiKey;

    public NewsApiClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    // Renamed for GNews, but kept public for scheduler
    public NewsApiResponseDTO fetchTopHeadlines(String query, int pageSize, int page) {
        // GNews 'search' endpoint is similar to NewsAPI's 'everything'
        // For 'top-headlines' GNews has a specific endpoint, but 'search' is more flexible
        String url = UriComponentsBuilder.fromUriString(baseUrl + "search") // GNews search endpoint
                .queryParam("q", query)
                .queryParam("lang", "en") // GNews uses 'lang' not 'language'
                .queryParam("max", pageSize) // GNews uses 'max' for page size
                .queryParam("page", page) // GNews uses 'page'
                .queryParam("apikey", apiKey) // GNews uses 'apikey'
                .toUriString();

        logger.debug("Fetching news from GNews: {}", url);

        try {
            NewsApiResponseDTO response = restTemplate.getForObject(url, NewsApiResponseDTO.class);

            if (response != null) {
                logger.debug("Raw GNews Response - Total Articles: {}", response.getTotalResults());
                if (response.getArticles() != null) {
                    logger.debug("Number of articles deserialized: {}", response.getArticles().size());
                } else {
                    logger.debug("Articles list is null in GNews response.");
                }

                if (response.getArticles() == null || response.getArticles().isEmpty()) {
                    logger.warn("GNews response 'articles' list is empty after deserialization.");
                }
                logger.info("Successfully fetched {} articles from GNews for query '{}', page {}",
                        response.getArticles() != null ? response.getArticles().size() : 0, query, page);
            }
            return response;

        } catch (RestClientException e) {
            logger.error("Error fetching news from GNews: {}", e.getMessage(), e);
            return null;
        }
    }
}