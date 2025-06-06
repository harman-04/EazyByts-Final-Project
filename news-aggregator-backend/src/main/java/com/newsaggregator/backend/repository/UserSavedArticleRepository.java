package com.newsaggregator.backend.repository;

import com.newsaggregator.backend.entity.NewsArticle;
import com.newsaggregator.backend.entity.User;
import com.newsaggregator.backend.entity.UserSavedArticle;
import com.newsaggregator.backend.entity.UserSavedArticle.UserSavedArticleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSavedArticleRepository extends JpaRepository<UserSavedArticle, UserSavedArticleId> {
    List<UserSavedArticle> findByUser(User user);
    Optional<UserSavedArticle> findByUserAndNewsArticle(User user, NewsArticle newsArticle);
    boolean existsByUserAndNewsArticle(User user, NewsArticle newsArticle);
}