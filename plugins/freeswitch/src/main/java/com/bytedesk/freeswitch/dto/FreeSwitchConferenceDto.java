package com.bytedesk.freeswitch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * FreeSwitch会议室DTO
 */
@Data
public class FreeSwitchConferenceDto {
    
    private Long id;
    
    private String name;
    
    private String description;
    
    private Integer maxMembers;
    
    private Integer currentMembers;
    
    private Boolean active;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastActivityTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 计算属性
    public String getOccupancyRate() {
        if (maxMembers == null || maxMembers == 0) {
            return "无限制";
        }
        
        double rate = (double) currentMembers / maxMembers * 100;
        return String.format("%.1f%%", rate);
    }
    
    public String getStatus() {
        if (!active) {
            return "未激活";
        } else if (currentMembers == 0) {
            return "空闲";
        } else if (maxMembers != null && currentMembers >= maxMembers) {
            return "已满";
        } else {
            return "进行中";
        }
    }
    
    public boolean isFull() {
        return maxMembers != null && currentMembers >= maxMembers;
    }
    
    public boolean isEmpty() {
        return currentMembers == 0;
    }
}
