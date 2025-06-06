package com.newsaggregator.backend.controller;

import com.newsaggregator.backend.dto.SourceDTO;
import com.newsaggregator.backend.entity.Source;
import com.newsaggregator.backend.service.SourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sources")
public class SourceController {

    private static final Logger logger = LoggerFactory.getLogger(SourceController.class);

    private final SourceService sourceService;

    @Autowired
    public SourceController(SourceService sourceService) {
        this.sourceService = sourceService;
    }

    // Helper to convert Entity to DTO
    private SourceDTO convertToDto(Source source) {
        return new SourceDTO(source.getId(), source.getName(), source.getBaseUrl());
    }

    @GetMapping
    public ResponseEntity<List<SourceDTO>> getAllSources() {
        logger.info("Received request to get all sources.");
        List<SourceDTO> sources = sourceService.getAllSources().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        logger.info("Returning {} sources.", sources.size());
        return ResponseEntity.ok(sources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SourceDTO> getSourceById(@PathVariable Long id) {
        logger.info("Received request to get source by ID: {}", id);
        return sourceService.getSourceById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Handled by GlobalExceptionHandler
    }

    @PostMapping
    public ResponseEntity<SourceDTO> createSource(@RequestBody SourceDTO sourceDTO) {
        logger.info("Received request to create source: {}", sourceDTO.getName());
        Source source = new Source(sourceDTO.getName(), sourceDTO.getBaseUrl());
        Source createdSource = sourceService.createSource(source);
        logger.info("Source '{}' created successfully.", createdSource.getName());
        return new ResponseEntity<>(convertToDto(createdSource), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SourceDTO> updateSource(@PathVariable Long id, @RequestBody SourceDTO sourceDTO) {
        logger.info("Received request to update source ID: {}", id);
        Source sourceDetails = new Source(sourceDTO.getName(), sourceDTO.getBaseUrl());
        Source updatedSource = sourceService.updateSource(id, sourceDetails);
        logger.info("Source ID: {} updated successfully.", id);
        return ResponseEntity.ok(convertToDto(updatedSource));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSource(@PathVariable Long id) {
        logger.info("Received request to delete source ID: {}", id);
        sourceService.deleteSource(id);
        logger.info("Source ID: {} deleted successfully.", id);
        return ResponseEntity.noContent().build();
    }
}