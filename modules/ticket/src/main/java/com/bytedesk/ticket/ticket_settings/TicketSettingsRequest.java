/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-12 12:10:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_settings;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsRequest;
import com.bytedesk.ticket.ticket_settings_category.TicketCategorySettingsRequest;
import com.bytedesk.ticket.ticket_settings_notification.TicketNotificationSettingsRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.Builder;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class TicketSettingsRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    /**
     * 是否设为组织默认配置（同一 org 仅允许一个默认，服务端在事务内保证唯一）
     */
    private Boolean isDefault;

    /**
     * 是否启用（默认为 true）
     */
    private Boolean enabled;

    /**
     * 是否启用自定义表单（默认为 false）
     */
    private Boolean customFormEnabled;

    /**
     * 绑定的流程 UID（若为空则沿用默认流程）
     */
    private String processUid;

    /**
     * 绑定的表单 UID（若为空则沿用默认表单）
     */
    private String formUid;

    // 子配置请求（仅提供非 draft 版本，服务端自动维护草稿）
    private TicketBasicSettingsRequest basicSettings;
    private TicketNotificationSettingsRequest notificationSettings;
    private TicketCategorySettingsRequest categorySettings;

}
