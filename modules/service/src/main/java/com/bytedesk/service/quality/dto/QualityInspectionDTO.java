package com.bytedesk.service.quality.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class QualityInspectionDTO {
    
    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be between 0 and 100")
    @Max(value = 100, message = "Score must be between 0 and 100")
    private Integer score;
    
    @NotNull(message = "Response score is required")
    @Min(0) @Max(100)
    private Integer responseScore;
    
    @NotNull(message = "Attitude score is required") 
    @Min(0) @Max(100)
    private Integer attitudeScore;
    
    @NotNull(message = "Solution score is required")
    @Min(0) @Max(100)
    private Integer solutionScore;
    
    @NotNull(message = "Standard score is required")
    @Min(0) @Max(100)
    private Integer standardScore;
    
    private String comment;
} 