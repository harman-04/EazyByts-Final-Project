package com.newsaggregator.backend.controller;

import com.newsaggregator.backend.dto.CategoryDTO;
import com.newsaggregator.backend.entity.Category;
import com.newsaggregator.backend.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Helper to convert Entity to DTO
    private CategoryDTO convertToDto(Category category) {
        return new CategoryDTO(category.getId(), category.getName());
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        logger.info("Received request to get all categories.");
        List<CategoryDTO> categories = categoryService.getAllCategories().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        logger.info("Returning {} categories.", categories.size());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        logger.info("Received request to get category by ID: {}", id);
        return categoryService.getCategoryById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Handled by GlobalExceptionHandler
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        logger.info("Received request to create category: {}", categoryDTO.getName());
        Category category = new Category(categoryDTO.getName());
        Category createdCategory = categoryService.createCategory(category);
        logger.info("Category '{}' created successfully.", createdCategory.getName());
        return new ResponseEntity<>(convertToDto(createdCategory), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        logger.info("Received request to update category ID: {}", id);
        Category categoryDetails = new Category(categoryDTO.getName()); // Only name can be updated
        Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
        logger.info("Category ID: {} updated successfully.", id);
        return ResponseEntity.ok(convertToDto(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        logger.info("Received request to delete category ID: {}", id);
        categoryService.deleteCategory(id);
        logger.info("Category ID: {} deleted successfully.", id);
        return ResponseEntity.noContent().build();
    }
}