/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 10:16:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-25 13:12:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
import com.bytedesk.kbase.service_settings.ServiceSettingsRequest;
import com.bytedesk.service.leave_msg.settings.LeaveMsgSettingsRequest;
import com.bytedesk.service.queue.settings.QueueSettings;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class AgentRequest extends BaseRequest {

    @NotBlank
    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_AGENT_AVATAR_URL;

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
    private LeaveMsgSettingsRequest leaveMsgSettings = new LeaveMsgSettingsRequest();

    // 一对一人工客服，不支持机器人接待
    // @Builder.Default
    // private RobotSettingsRequest robotSettings = new RobotSettingsRequest();

    @Builder.Default
    private ServiceSettingsRequest serviceSettings = new ServiceSettingsRequest();

    @Builder.Default
    private AutoReplySettings autoReplySettings = new AutoReplySettings();

    @Builder.Default
    private QueueSettings queueSettings = new QueueSettings();

    @Builder.Default
    private Integer maxThreadCount = 10;

    @Builder.Default
    private Integer currentThreadCount = 0;
    
    //
    @NotBlank
    private String memberUid;

}
