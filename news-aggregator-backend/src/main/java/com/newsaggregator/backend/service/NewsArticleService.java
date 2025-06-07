package com.newsaggregator.backend.service;

import com.newsaggregator.backend.dto.NewsArticleDTO;
import com.newsaggregator.backend.dto.NewsArticlePageResponse;
import com.newsaggregator.backend.entity.NewsArticle;
import com.newsaggregator.backend.entity.Category;
import com.newsaggregator.backend.entity.Source;
import com.newsaggregator.backend.exception.ResourceNotFoundException;
import com.newsaggregator.backend.repository.NewsArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NewsArticleService {

    private static final Logger logger = LoggerFactory.getLogger(NewsArticleService.class);

    private final NewsArticleRepository newsArticleRepository;
    private final SourceService sourceService;
    private final CategoryService categoryService;

    @Autowired
    public NewsArticleService(NewsArticleRepository newsArticleRepository,
                              SourceService sourceService,
                              CategoryService categoryService) {
        this.newsArticleRepository = newsArticleRepository;
        this.sourceService = sourceService;
        this.categoryService = categoryService;
    }

    /**
     * Converts a NewsArticle entity to its DTO representation.
     */
    public NewsArticleDTO convertToDto(NewsArticle article) {
        NewsArticleDTO dto = new NewsArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setDescription(article.getDescription());
        dto.setUrl(article.getUrl());
        dto.setImageUrl(article.getImageUrl());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setSourceName(article.getSource() != null ? article.getSource().getName() : null);
        dto.setCategoryName(article.getCategory() != null ? article.getCategory().getName() : null);
        return dto;
    }

    /**
     * Saves a single news article.
     * This method is intended for internal use, especially by the news fetching scheduler.
     * It handles finding/creating source/category and preventing duplicates.
     */
    @Transactional
    public NewsArticle saveNewsArticle(String title, String description, String url, String imageUrl,
                                       LocalDateTime publishedAt, String sourceName, String categoryName) {
        logger.debug("Attempting to save news article: {}", title);

        // Prevent duplicate articles based on URL
        if (newsArticleRepository.findByUrl(url).isPresent()) {
            logger.debug("Article with URL '{}' already exists. Skipping.", url);
            return null; // Indicate that it was not saved (as it's a duplicate)
        }

        // Find or create Source
        Source source = sourceService.getSourceByName(sourceName)
                .orElseGet(() -> {
                    logger.info("Source '{}' not found, creating new one.", sourceName);
                    return sourceService.createSource(new Source(sourceName, null)); // Base URL can be null for now
                });

        // Find or create Category
        Category category = categoryService.getCategoryByName(categoryName)
                .orElseGet(() -> {
                    logger.info("Category '{}' not found, creating new one.", categoryName);
                    return categoryService.createCategory(new Category(categoryName));
                });

        NewsArticle newArticle = new NewsArticle();
        newArticle.setTitle(title);
        newArticle.setDescription(description);
        newArticle.setUrl(url);
        newArticle.setImageUrl(imageUrl); // <<< CORRECTED LINE HERE
        newArticle.setPublishedAt(publishedAt);
        newArticle.setSource(source);
        newArticle.setCategory(category);

        NewsArticle savedArticle = newsArticleRepository.save(newArticle);
        logger.info("Saved news article: {} (ID: {})", savedArticle.getTitle(), savedArticle.getId());
        return savedArticle;
    }

    /**
     * Fetches all news articles with pagination and sorting.
     */
    @Transactional(readOnly = true)
    public NewsArticlePageResponse getAllNewsArticles(int page, int size, String sortBy, String sortDir) {
        logger.debug("Fetching all news articles (page: {}, size: {}, sortBy: {}, sortDir: {})", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<NewsArticle> articlesPage = newsArticleRepository.findAll(pageable);

        List<NewsArticleDTO> content = articlesPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new NewsArticlePageResponse(
                content,
                articlesPage.getNumber(),
                articlesPage.getSize(),
                articlesPage.getTotalElements(),
                articlesPage.getTotalPages(),
                articlesPage.isLast()
        );
    }

    /**
     * Fetches a single news article by ID.
     */
    @Transactional(readOnly = true)
    public NewsArticleDTO getNewsArticleById(Long id) {
        logger.debug("Fetching news article by ID: {}", id);
        NewsArticle article = newsArticleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("News article with ID: {} not found.", id);
                    return new ResourceNotFoundException("NewsArticle", "id", id);
                });
        return convertToDto(article);
    }

    /**
     * Fetches news articles based on search criteria, with pagination and sorting,
     * including optional date range.
     */
    @Transactional(readOnly = true)
    public NewsArticlePageResponse searchNewsArticles(String keyword, Long categoryId, Long sourceId,
                                                      LocalDateTime startDate, LocalDateTime endDate,
                                                      int page, int size, String sortBy, String sortDir) {
        logger.debug("Searching news articles with keyword: '{}', categoryId: {}, sourceId: {}, startDate: {}, endDate: {} (page: {}, size: {}, sortBy: {}, sortDir: {})",
                keyword, categoryId, sourceId, startDate, endDate, page, size, sortBy, sortDir);

        // Build Sort object
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        // Build Pageable object with pagination and sorting
        Pageable pageable = PageRequest.of(page, size, sort);

        // Initialize spec as null, then build it conditionally
        Specification<NewsArticle> spec = null;

        if (keyword != null && !keyword.isBlank()) {
            String lowerCaseKeyword = "%" + keyword.toLowerCase() + "%";
            Specification<NewsArticle> keywordSpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), lowerCaseKeyword),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), lowerCaseKeyword)
                    );
            spec = combineSpecifications(spec, keywordSpec);
        }

        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
            Specification<NewsArticle> categorySpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category"), category);
            spec = combineSpecifications(spec, categorySpec);
        }

        if (sourceId != null) {
            Source source = sourceService.getSourceById(sourceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Source", "id", sourceId));
            Specification<NewsArticle> sourceSpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("source"), source);
            spec = combineSpecifications(spec, sourceSpec);
        }

        // New: Add date range filtering
        if (startDate != null) {
            Specification<NewsArticle> startDateSpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("publishedAt"), startDate);
            spec = combineSpecifications(spec, startDateSpec);
        }

        if (endDate != null) {
            Specification<NewsArticle> endDateSpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("publishedAt"), endDate);
            spec = combineSpecifications(spec, endDateSpec);
        }

        Page<NewsArticle> articlesPage;
        if (spec != null) {
            articlesPage = newsArticleRepository.findAll(spec, pageable);
        } else {
            articlesPage = newsArticleRepository.findAll(pageable); // If no filters, just get all with pagination and sorting
        }

        List<NewsArticleDTO> content = articlesPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new NewsArticlePageResponse(
                content,
                articlesPage.getNumber(),
                articlesPage.getSize(),
                articlesPage.getTotalElements(),
                articlesPage.getTotalPages(),
                articlesPage.isLast()
        );
    }

    // Helper method to combine specifications
    private Specification<NewsArticle> combineSpecifications(Specification<NewsArticle> currentSpec, Specification<NewsArticle> newSpec) {
        if (currentSpec == null) {
            return newSpec;
        } else {
            return currentSpec.and(newSpec);
        }
    }

    // This method will be used by the scheduler to initially populate data
    // We'll replace this with real API calls later
    @Transactional
    public void populateInitialData() {
        logger.info("Populating initial dummy news data.");

        Source source1 = sourceService.createSource(new Source("TechCrunch", "https://techcrunch.com"));
        Source source2 = sourceService.createSource(new Source("The New York Times", "https://www.nytimes.com"));
        Source source3 = sourceService.createSource(new Source("ESPN", "https://www.espn.com"));

        Category cat1 = categoryService.createCategory(new Category("Technology"));
        Category cat2 = categoryService.createCategory(new Category("Politics"));
        Category cat3 = categoryService.createCategory(new Category("Sports"));
        Category cat4 = categoryService.createCategory(new Category("Science"));

        saveNewsArticle("Apple unveils Vision Pro", "Apple announced its new mixed reality headset, Vision Pro, at WWDC.",
                "https://example.com/apple-vision-pro-1", "https://example.com/apple-vision-pro.jpg",
                LocalDateTime.now().minusDays(1), source1.getName(), cat1.getName());

        saveNewsArticle("New AI Model Breaks Records", "Researchers developed a new AI model that sets benchmarks in natural language processing.",
                "https://example.com/ai-model-2", "https://example.com/ai-model.jpg",
                LocalDateTime.now().minusHours(5), source1.getName(), cat1.getName());

        saveNewsArticle("Government Passes Landmark Bill", "The new legislation aims to address climate change impacts.",
                "https://example.com/government-bill-3", "https://example.com/bill.jpg",
                LocalDateTime.now().minusDays(2), source2.getName(), cat2.getName());

        saveNewsArticle("Local Team Wins Championship", "The city's beloved basketball team secured the national championship title.",
                "https://example.com/championship-4", "https://example.com/team-win.jpg",
                LocalDateTime.now().minusHours(10), source3.getName(), cat3.getName());

        saveNewsArticle("Breakthrough in Cancer Research", "Scientists discover a new compound showing promise in treating certain cancers.",
                "https://example.com/cancer-research-5", "https://example.com/research.jpg",
                LocalDateTime.now().minusHours(2), source2.getName(), cat4.getName());

        saveNewsArticle("Another Tech Article", "More insights into the latest tech trends.",
                "https://example.com/another-tech-article-6", "https://example.com/tech-trend.jpg",
                LocalDateTime.now().minusHours(1), source1.getName(), cat1.getName());

        logger.info("Initial dummy news data population complete.");
    }
}