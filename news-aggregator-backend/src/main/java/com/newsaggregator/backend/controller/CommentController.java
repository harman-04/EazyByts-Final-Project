package com.newsaggregator.backend.controller;

import com.newsaggregator.backend.dto.CommentDTO;
import com.newsaggregator.backend.dto.CreateCommentDTO;
import com.newsaggregator.backend.service.CommentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles/{articleId}/comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // IMPORTANT: For now, userId is hardcoded or passed as a path variable.
    // In actual production, this userId would come from the authenticated user's JWT token.
    @PostMapping
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long articleId,
            @RequestParam Long userId, // Temporarily pass userId, will be from SecurityContext
            @Valid @RequestBody CreateCommentDTO createCommentDTO) {
        logger.info("Received request to add comment to article ID: {} by user ID: {}", articleId, userId);
        CommentDTO comment = commentService.addComment(articleId, userId, createCommentDTO);
        logger.info("Comment added to article ID: {} by user ID: {}", articleId, userId);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getCommentsForArticle(@PathVariable Long articleId) {
        logger.info("Received request to get comments for article ID: {}", articleId);
        List<CommentDTO> comments = commentService.getCommentsByArticleId(articleId);
        logger.info("Returning {} comments for article ID: {}", comments.size(), articleId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long articleId, // Just for path consistency, not used in service
            @PathVariable Long commentId,
            @RequestParam Long userId) { // Temporarily pass userId, will be from SecurityContext
        logger.info("Received request to delete comment ID: {} for article ID: {} by user ID: {}", commentId, articleId, userId);
        commentService.deleteComment(commentId, userId);
        logger.info("Comment ID: {} deleted by user ID: {}", commentId, userId);
        return ResponseEntity.noContent().build();
    }
}