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

import lombok.Data;

/**
 * 邮件同步概览状态
 */
@Data
public class EmailSyncOverview {
    
    // 总的启用邮件账户数
    private int totalEnabledEmails;
    
    // 当前活跃的同步任务数
    private int totalActiveTasks;
    
    // 总的同步任务数（包括未运行的）
    private int totalSyncTasks;
    
    // 已连接的邮件账户数
    private int connectedCount;
    
    // 计算连接率
    public double getConnectionRate() {
        if (totalEnabledEmails == 0) {
            return 0.0;
        }
        return (double) connectedCount / totalEnabledEmails * 100;
    }
    
}
