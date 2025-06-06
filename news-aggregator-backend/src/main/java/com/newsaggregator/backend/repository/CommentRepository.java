package com.newsaggregator.backend.repository;

import com.newsaggregator.backend.entity.Comment;
import com.newsaggregator.backend.entity.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByNewsArticleOrderByCreatedAtDesc(NewsArticle newsArticle);
}