package com.bytedesk.service.quality.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class QualityRuleDTO {

    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    private String categoryUid;
    
    @Min(value = 0, message = "Min response time must be positive")
    private Integer minResponseTime;
    
    @Min(value = 0, message = "Min solution time must be positive")
    private Integer minSolutionTime;
    
    private String forbiddenWords;
    
    private String requiredWords;
    
    private String scoreRules;
    
    private Boolean enabled = true;
} 