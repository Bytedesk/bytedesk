/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 10:16:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 15:45:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.kbase.auto_reply.settings.AutoReplySettings;
import com.bytedesk.kbase.settings.ServiceSettingsRequest;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettingsRequest;
import com.bytedesk.service.queue.settings.QueueSettings;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class AgentRequest extends BaseRequest {

    @NotBlank
    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultAgentAvatarUrl();

    @NotBlank
    private String mobile;

    @NotBlank
    @Email(message = "email format error")
    private String email;

    @Builder.Default
    private String description = I18Consts.I18N_AGENT_DESCRIPTION;

    @Builder.Default
    private String status = AgentStatusEnum.OFFLINE.name();

    @Builder.Default
    private boolean connected = false;

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private MessageLeaveSettingsRequest messageLeaveSettings = new MessageLeaveSettingsRequest();

    // 一对一人工客服，不支持机器人接待
    // @Builder.Default
    // private RobotSettingsRequest robotSettings = new RobotSettingsRequest();

    @Builder.Default
    private ServiceSettingsRequest serviceSettings = new ServiceSettingsRequest();

    @Builder.Default
    private AutoReplySettings autoReplySettings = new AutoReplySettings();

    @Builder.Default
    private QueueSettings queueSettings = new QueueSettings();

    // @Builder.Default
    // private InviteSettings inviteSettings = new InviteSettings();

    @Builder.Default
    private Integer maxThreadCount = 10;

    @Builder.Default
    private Integer timeoutRemindTime = 5;

    // 超时提醒提示
    @Builder.Default
    private String timeoutRemindTip = I18Consts.I18N_AGENT_TIMEOUT_TIP;

    //
    @NotBlank
    private String memberUid;

}
