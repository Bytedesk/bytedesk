/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-03 15:30:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 15:30:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    
    /**
     * 用户ID
     */
    private String uid;
    
    /**
     * SIP用户名
     */
    private String sipUsername;
    
    /**
     * 用户姓名
     */
    private String name;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 用户状态
     */
    private String status;
    
    /**
     * 当前是否在通话中
     */
    private boolean inCall;
    
    /**
     * 当前通话ID
     */
    private String currentCallId;
    
    /**
     * 最后活动时间
     */
    private LocalDateTime lastActive;
    
    /**
     * 所属部门
     */
    private String department;
    
    /**
     * 技能标签
     */
    private String[] skills;
}
