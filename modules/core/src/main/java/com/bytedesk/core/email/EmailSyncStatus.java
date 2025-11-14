/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-01 14:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-01 14:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email;

import java.time.ZonedDateTime;

import lombok.Data;

/**
 * 邮件同步状态信息
 */
@Data
public class EmailSyncStatus {
    
    // 邮件配置UID
    private String emailUid;
    
    // 是否正在运行同步任务
    private boolean running;
    
    // 是否启用
    private boolean enabled;
    
    // 是否启用自动同步
    private boolean autoSyncEnabled;
    
    // 最后同步时间
    private ZonedDateTime lastSyncTime;
    
    // 连接状态
    private String connectionStatus;
    
    // 错误信息
    private String errorMessage;
    
}
