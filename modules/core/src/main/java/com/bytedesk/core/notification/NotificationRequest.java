/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-01 09:28:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-15 13:39:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notification;

import com.bytedesk.core.base.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest extends BaseRequest {
    
    private static final long serialVersionUID = 1L;
    
    private String title;

    private String deptUid;

    private String type;

    private String status;

    private String level;

    private String extra;

    private String creatorUid;

    /**
     * 前端自定义访客标识（如 visitor_001），用于 visitor 端未读通知数查询
     * 后端通过 native query join visitor 表自动解析为系统 uid
     */
    private String visitorUid;

}
