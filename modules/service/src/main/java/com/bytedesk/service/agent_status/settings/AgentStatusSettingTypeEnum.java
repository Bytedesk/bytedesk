/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 08:57:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status.settings;

public enum AgentStatusSettingTypeEnum {
    // 审核时间段类型：ANY_TIME(任意时间)或CUSTOM_TIME(自定义时间)
    ANY_TIME,
    CUSTOM_TIME,
    // 审核方式：ALWAYS_MANUAL(始终等待人工审核)、AUTO_APPROVE(超时自动通过)、AUTO_REJECT(超时自动拒绝)
    ALWAYS_MANUAL,
    AUTO_APPROVE
}
