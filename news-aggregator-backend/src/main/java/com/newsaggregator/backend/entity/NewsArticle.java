package com.newsaggregator.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "news_articles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "url") // Ensure unique URLs for articles
})
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"source", "category"}) // Exclude relations to prevent recursion in toString
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512) // Adjusted length for titles
    private String title;

    @Column(columnDefinition = "TEXT") // Use TEXT for potentially long descriptions
    private String description;

    @Column(nullable = false, unique = true, length = 2048) // URLs can be very long
    private String url;

    @Column(name = "image_url", length = 2048) // Image URLs can also be long
    private String imageUrl;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading for performance
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading for performance
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Lifecycle Callbacks for auditing
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void voidOnUpdate() { // Renamed to avoid conflicts if parent/child also have pre-update
        updatedAt = LocalDateTime.now();
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsArticle that = (NewsArticle) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(url, that.url); // URL is a good unique identifier
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url);
    }
}