package com.bytedesk.voc.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedbackRequest {
    
    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 5000, message = "Content length must be between 10 and 5000")
    private String content;
    
    @NotBlank(message = "Type is required")
    private String type;
} 