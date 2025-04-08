/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-01 11:32:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 16:14:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup_message;

import com.bytedesk.core.message.AbstractMessageEntity;

/**
 * 分表存储技能组客服消息
 * 同步message中客服消息，包括uid。用于查询技能组消息，减少message表压力
 */
// @Entity
// @Data
// @SuperBuilder
// @NoArgsConstructor // 添加无参构造函数
// @Accessors(chain = true)
// @EqualsAndHashCode(callSuper = true)
// @Table(name = "bytedesk_service_workgroup_message")
public class WorkgroupMessage extends AbstractMessageEntity {

    private static final long serialVersionUID = 1L;
    
    // 可以在这里添加 WorkgroupMessage 特有的字段（如果有的话）
}
