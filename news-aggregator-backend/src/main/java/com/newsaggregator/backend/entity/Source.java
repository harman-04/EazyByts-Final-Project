package com.newsaggregator.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "sources", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Source {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "BBC News", "CNN"

    @Column(nullable = true, length = 2048) // Increased length for potentially long URLs
    private String baseUrl; // Base URL of the source, e.g., "https://www.bbc.com/news"

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Source(String name, String baseUrl) {
        this.name = name;
        this.baseUrl = baseUrl;
    }

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

    // equals and hashCode for proper collection behavior
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return Objects.equals(id, source.id) &&
                Objects.equals(name, source.name); // Consider name for uniqueness if ID not assigned yet
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}