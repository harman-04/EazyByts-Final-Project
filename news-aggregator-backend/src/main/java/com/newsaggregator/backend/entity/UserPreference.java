package com.newsaggregator.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"user", "preferredSources", "preferredCategories"})
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "preferred_keywords", length = 1024) // Store as comma-separated string
    private String preferredKeywords;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_preferred_sources",
            joinColumns = @JoinColumn(name = "preference_id"),
            inverseJoinColumns = @JoinColumn(name = "source_id")
    )
    private Set<Source> preferredSources = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_preferred_categories",
            joinColumns = @JoinColumn(name = "preference_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> preferredCategories = new HashSet<>();

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
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPreference that = (UserPreference) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(user, that.user); // User is unique for preference
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user);
    }
}