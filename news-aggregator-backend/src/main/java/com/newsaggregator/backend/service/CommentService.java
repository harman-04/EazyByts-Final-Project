package com.newsaggregator.backend.service;

import com.newsaggregator.backend.dto.CommentDTO;
import com.newsaggregator.backend.dto.CreateCommentDTO;
import com.newsaggregator.backend.entity.Comment;
import com.newsaggregator.backend.entity.NewsArticle;
import com.newsaggregator.backend.entity.User;
import com.newsaggregator.backend.exception.ResourceNotFoundException;
import com.newsaggregator.backend.repository.CommentRepository;
import com.newsaggregator.backend.repository.NewsArticleRepository;
import com.newsaggregator.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NewsArticleRepository newsArticleRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, NewsArticleRepository newsArticleRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.newsArticleRepository = newsArticleRepository;
    }

    /**
     * Converts a Comment entity to its DTO representation.
     */
    private CommentDTO convertToDto(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUsername(comment.getUser() != null ? comment.getUser().getUsername() : "Anonymous"); // Handle potential null user
        dto.setNewsArticleId(comment.getNewsArticle() != null ? comment.getNewsArticle().getId() : null);
        return dto;
    }

    @Transactional
    public CommentDTO addComment(Long articleId, Long userId, CreateCommentDTO createCommentDTO) {
        logger.info("Attempting to add comment to article ID: {} by user ID: {}", articleId, userId);

        NewsArticle newsArticle = newsArticleRepository.findById(articleId)
                .orElseThrow(() -> {
                    logger.warn("News article with ID: {} not found for commenting.", articleId);
                    return new ResourceNotFoundException("NewsArticle", "id", articleId);
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User with ID: {} not found to post comment.", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });

        Comment comment = new Comment();
        comment.setContent(createCommentDTO.getContent());
        comment.setNewsArticle(newsArticle);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment (ID: {}) added successfully by user '{}' to article '{}'",
                savedComment.getId(), user.getUsername(), newsArticle.getTitle());
        return convertToDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByArticleId(Long articleId) {
        logger.debug("Fetching comments for article ID: {}", articleId);
        NewsArticle newsArticle = newsArticleRepository.findById(articleId)
                .orElseThrow(() -> {
                    logger.warn("News article with ID: {} not found for fetching comments.", articleId);
                    return new ResourceNotFoundException("NewsArticle", "id", articleId);
                });

        List<Comment> comments = commentRepository.findByNewsArticleOrderByCreatedAtDesc(newsArticle);
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        logger.info("Attempting to delete comment ID: {} by user ID: {}", commentId, userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.warn("Comment with ID: {} not found for deletion.", commentId);
                    return new ResourceNotFoundException("Comment", "id", commentId);
                });

        if (!comment.getUser().getId().equals(userId)) {
            logger.warn("User ID: {} attempted to delete comment ID: {} which belongs to user ID: {}",
                    userId, commentId, comment.getUser().getId());
            throw new SecurityException("User not authorized to delete this comment"); // Or custom UnauthorizedException
        }

        commentRepository.delete(comment);
        logger.info("Comment ID: {} deleted successfully by user ID: {}", commentId, userId);
    }
}