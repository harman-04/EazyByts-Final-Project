package com.newsaggregator.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDTO {
    @NotBlank(message = "Comment content cannot be empty")
    @Size(max = 1000, message = "Comment content cannot exceed 1000 characters")
    private String content;
    // userId and articleId will be handled by the controller/service based on context
}