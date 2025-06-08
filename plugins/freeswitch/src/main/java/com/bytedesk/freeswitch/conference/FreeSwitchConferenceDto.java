/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 16:01:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 17:26:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
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
