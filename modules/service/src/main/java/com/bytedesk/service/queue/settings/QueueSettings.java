/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-23 15:22:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-23 15:24:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue.settings;

import lombok.Data;

import java.io.Serializable;

import lombok.Builder;

@Data
@Builder
public class QueueSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private int maxWaiting; // 最大等待人数

    private int maxWaitTime; // 最大等待时间(秒)
    
    private String queueTip; // 排队提示
    
}

