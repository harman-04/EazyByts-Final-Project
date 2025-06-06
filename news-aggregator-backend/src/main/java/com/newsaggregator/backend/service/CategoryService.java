package com.newsaggregator.backend.service;

import com.newsaggregator.backend.entity.Category;
import com.newsaggregator.backend.exception.DuplicateResourceException;
import com.newsaggregator.backend.exception.ResourceNotFoundException;
import com.newsaggregator.backend.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        logger.debug("Fetching all categories.");
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        logger.debug("Fetching category with ID: {}", id);
        return categoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        logger.debug("Fetching category by name: {}", name);
        return categoryRepository.findByName(name);
    }

    @Transactional
    public Category createCategory(Category category) {
        logger.info("Attempting to create category: {}", category.getName());
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            logger.warn("Category with name '{}' already exists. Aborting creation.", category.getName());
            throw new DuplicateResourceException("Category", "name", category.getName());
        }
        Category savedCategory = categoryRepository.save(category);
        logger.info("Category '{}' created successfully with ID: {}", savedCategory.getName(), savedCategory.getId());
        return savedCategory;
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        logger.info("Attempting to update category with ID: {}", id);
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Category with ID: {} not found for update.", id);
                    return new ResourceNotFoundException("Category", "id", id);
                });

        // Check if the new name conflicts with another existing category (excluding itself)
        if (!existingCategory.getName().equalsIgnoreCase(categoryDetails.getName())) {
            if (categoryRepository.findByName(categoryDetails.getName()).isPresent()) {
                logger.warn("Update failed: Category with name '{}' already exists for another ID.", categoryDetails.getName());
                throw new DuplicateResourceException("Category", "name", categoryDetails.getName());
            }
        }

        existingCategory.setName(categoryDetails.getName());
        // createdAt is @PrePersist, updatedAt is @PreUpdate handled by entity lifecycle

        Category updatedCategory = categoryRepository.save(existingCategory);
        logger.info("Category with ID: {} updated successfully.", updatedCategory.getId());
        return updatedCategory;
    }

    @Transactional
    public void deleteCategory(Long id) {
        logger.info("Attempting to delete category with ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            logger.warn("Category with ID: {} not found for deletion.", id);
            throw new ResourceNotFoundException("Category", "id", id);
        }
        categoryRepository.deleteById(id);
        logger.info("Category with ID: {} deleted successfully.", id);
    }
}