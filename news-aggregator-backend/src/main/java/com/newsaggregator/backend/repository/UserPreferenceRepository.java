package com.newsaggregator.backend.repository;

import com.newsaggregator.backend.entity.User;
import com.newsaggregator.backend.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUser(User user);
}