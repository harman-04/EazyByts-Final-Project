package com.newsaggregator.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_saved_articles")
@IdClass(UserSavedArticle.UserSavedArticleId.class) // Define composite primary key
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"user", "newsArticle"})
public class UserSavedArticle {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private NewsArticle newsArticle;

    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    // Composite Primary Key Class
    @NoArgsConstructor // Lombok won't generate no-arg for inner classes automatically
    public static class UserSavedArticleId implements Serializable {
        private Long user; // Corresponds to the field name 'user' in UserSavedArticle
        private Long newsArticle; // Corresponds to the field name 'newsArticle' in UserSavedArticle

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserSavedArticleId that = (UserSavedArticleId) o;
            return Objects.equals(user, that.user) && Objects.equals(newsArticle, that.newsArticle);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, newsArticle);
        }
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        savedAt = LocalDateTime.now();
    }
}