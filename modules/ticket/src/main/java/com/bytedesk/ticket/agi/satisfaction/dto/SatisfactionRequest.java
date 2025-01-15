package com.bytedesk.ticket.agi.satisfaction.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SatisfactionRequest {
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;
    
    private String comment;
    
    @Min(value = 1, message = "Response time satisfaction must be between 1 and 5")
    @Max(value = 5, message = "Response time satisfaction must be between 1 and 5")
    private Integer responseTimeSatisfaction;
    
    @Min(value = 1, message = "Solution satisfaction must be between 1 and 5")
    @Max(value = 5, message = "Solution satisfaction must be between 1 and 5")
    private Integer solutionSatisfaction;
    
    @Min(value = 1, message = "Service attitude satisfaction must be between 1 and 5")
    @Max(value = 5, message = "Service attitude satisfaction must be between 1 and 5")
    private Integer serviceAttitudeSatisfaction;
} 