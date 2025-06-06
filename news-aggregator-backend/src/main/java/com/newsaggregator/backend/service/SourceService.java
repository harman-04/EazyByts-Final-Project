package com.newsaggregator.backend.service;

import com.newsaggregator.backend.entity.Source;
import com.newsaggregator.backend.exception.DuplicateResourceException;
import com.newsaggregator.backend.exception.ResourceNotFoundException;
import com.newsaggregator.backend.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SourceService {

    private static final Logger logger = LoggerFactory.getLogger(SourceService.class);

    private final SourceRepository sourceRepository;

    @Autowired
    public SourceService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Transactional(readOnly = true)
    public List<Source> getAllSources() {
        logger.debug("Fetching all sources.");
        return sourceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Source> getSourceById(Long id) {
        logger.debug("Fetching source with ID: {}", id);
        return sourceRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Source> getSourceByName(String name) {
        logger.debug("Fetching source by name: {}", name);
        return sourceRepository.findByName(name);
    }

    @Transactional
    public Source createSource(Source source) {
        logger.info("Attempting to create source: {}", source.getName());
        if (sourceRepository.findByName(source.getName()).isPresent()) {
            logger.warn("Source with name '{}' already exists. Aborting creation.", source.getName());
            throw new DuplicateResourceException("Source", "name", source.getName());
        }
        Source savedSource = sourceRepository.save(source);
        logger.info("Source '{}' created successfully with ID: {}", savedSource.getName(), savedSource.getId());
        return savedSource;
    }

    @Transactional
    public Source updateSource(Long id, Source sourceDetails) {
        logger.info("Attempting to update source with ID: {}", id);
        Source existingSource = sourceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Source with ID: {} not found for update.", id);
                    return new ResourceNotFoundException("Source", "id", id);
                });

        // Check if the new name conflicts with another existing source (excluding itself)
        if (!existingSource.getName().equalsIgnoreCase(sourceDetails.getName())) {
            if (sourceRepository.findByName(sourceDetails.getName()).isPresent()) {
                logger.warn("Update failed: Source with name '{}' already exists for another ID.", sourceDetails.getName());
                throw new DuplicateResourceException("Source", "name", sourceDetails.getName());
            }
        }

        existingSource.setName(sourceDetails.getName());
        existingSource.setBaseUrl(sourceDetails.getBaseUrl());
        // createdAt is @PrePersist, updatedAt is @PreUpdate handled by entity lifecycle

        Source updatedSource = sourceRepository.save(existingSource);
        logger.info("Source with ID: {} updated successfully.", updatedSource.getId());
        return updatedSource;
    }

    @Transactional
    public void deleteSource(Long id) {
        logger.info("Attempting to delete source with ID: {}", id);
        if (!sourceRepository.existsById(id)) {
            logger.warn("Source with ID: {} not found for deletion.", id);
            throw new ResourceNotFoundException("Source", "id", id);
        }
        sourceRepository.deleteById(id);
        logger.info("Source with ID: {} deleted successfully.", id);
    }
}