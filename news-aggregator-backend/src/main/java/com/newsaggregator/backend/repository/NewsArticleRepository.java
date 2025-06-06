package com.newsaggregator.backend.repository;

import com.newsaggregator.backend.entity.NewsArticle;
import com.newsaggregator.backend.entity.Category;
import com.newsaggregator.backend.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long>, JpaSpecificationExecutor<NewsArticle> {
    Optional<NewsArticle> findByUrl(String url);

    // Custom query methods (Spring Data JPA automatically implements these)
    List<NewsArticle> findByTitleContainingIgnoreCase(String titleKeyword);
    List<NewsArticle> findByCategory(Category category);
    List<NewsArticle> findBySource(Source source);

    // Find by multiple criteria - useful for personalized feeds later
    List<NewsArticle> findByTitleContainingIgnoreCaseAndCategoryAndSource(String titleKeyword, Category category, Source source);

    // Ordered by published date
    List<NewsArticle> findAllByOrderByPublishedAtDesc();
    List<NewsArticle> findByCategoryOrderByPublishedAtDesc(Category category);
    List<NewsArticle> findBySourceOrderByPublishedAtDesc(Source source);

    // Pagination example (will be used later)
    // Page<NewsArticle> findAll(Pageable pageable);
}