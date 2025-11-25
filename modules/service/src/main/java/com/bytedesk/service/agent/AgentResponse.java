/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 10:17:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-05 09:57:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.member.MemberProtobuf;
import com.bytedesk.service.agent_settings.AgentSettingsResponse;
import com.bytedesk.service.queue.AgentQueueStatsResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AgentResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String nickname;

    private String avatar;

    private String mobile;

    private String email;

    private String description;

    private String status;

    private Boolean connected;

    private Boolean enabled;

    /**
     * Agent settings (loaded from settings entity)
     */
    private AgentSettingsResponse settings;

    /**
     * Agent queue statistics (loaded from queue service)
     */
    private AgentQueueStatsResponse queueStats;

    private MemberProtobuf member;

    private String userUid;
}
