package com.bytedesk.ticket.agi.assignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class AssignmentRuleRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    private Long categoryId;
    
    private String priorityLevel;
    
    private List<String> requiredSkills;
    
    @NotNull(message = "Max active tickets is required")
    private Integer maxActiveTickets = 10;
    
    private Boolean considerWorkload = true;
    
    private Boolean considerOnlineStatus = true;
    
    private Double weight = 1.0;
    
    private Boolean enabled = true;
} 