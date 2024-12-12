package com.bytedesk.ticket.worktime.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Data
public class WorkTimeConfigRequest {
    
    @NotBlank(message = "Timezone is required")
    private String timezone;
    
    @NotBlank(message = "Work days is required")
    @Pattern(regexp = "^[1-7](,[1-7])*$", message = "Invalid work days format")
    private String workDays;
    
    @NotBlank(message = "Work hours is required")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3])[0-5][0-9]-([01]?[0-9]|2[0-3])[0-5][0-9]$", 
            message = "Invalid work hours format")
    private String workHours;
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3])[0-5][0-9]-([01]?[0-9]|2[0-3])[0-5][0-9]$", 
            message = "Invalid lunch break format")
    private String lunchBreak;
    
    private List<String> holidays;
    private List<String> specialWorkdays;
} 