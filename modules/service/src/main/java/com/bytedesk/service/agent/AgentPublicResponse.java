/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-04-25 14:24:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-04-25 14:24:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.service.agent;

/**
 * 访客端公开的客服信息（最小字段集）
 */
public record AgentPublicResponse(
        String uid,
        String userUid,
        String nickname,
        String agentNo,
        String avatar,
        String status,
        String description) {

    public static AgentPublicResponse from(AgentEntity agent) {
        return new AgentPublicResponse(
                agent.getUid(),
                agent.getUserUid(),
                agent.getNickname(),
                agent.getAgentNo(),
                agent.getAvatar(),
                agent.getStatus(),
                agent.getDescription());
    }
}