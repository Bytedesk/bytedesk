/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:00:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-01 10:41:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest extends BaseRequest {
    
    private static final long serialVersionUID = 1L;

    
    private String topic;

    private String threadUid;

    private String threadType;
    
    private String userUid;

    private String nickname;

    // 客服端可以更加消息类型过滤消息
    private String type;

    // used for client query
    private String componentType;

    private String searchDate;


    /**
     * 判断是否为客服类型会话
     */
    public boolean isCustomerServiceType() {
        return "AGENT".equals(threadType) || "WORKGROUP".equals(threadType) || 
               "ROBOT".equals(threadType) || "UNIFIED".equals(threadType);
    }

    /**
     * 判断是否为成员类型会话
     */
    public boolean isMemberType() {
        return "MEMBER".equals(threadType);
    }

    /**
     * 判断是否为群聊类型会话
     */
    public boolean isGroupType() {
        return "GROUP".equals(threadType);
    }
}
