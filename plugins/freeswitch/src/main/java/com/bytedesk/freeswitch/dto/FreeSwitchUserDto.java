package com.bytedesk.freeswitch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * FreeSwitch用户DTO
 */
@Data
public class FreeSwitchUserDto {
    
    private Long id;
    
    private String username;
    
    private String domain;
    
    private String email;
    
    private String displayName;
    
    private Boolean enabled;
    
    private Boolean online;
    
    private String lastIpAddress;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogoutTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationTime;
    
    private Integer totalCalls;
    
    private Long totalCallDuration;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
