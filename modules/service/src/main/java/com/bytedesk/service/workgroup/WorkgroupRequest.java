/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 10:17:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-17 09:08:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.kbase.settings.ServiceSettingsRequest;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettingsRequest;
import com.bytedesk.service.queue.settings.QueueSettingsRequest;
import com.bytedesk.service.settings.RobotSettingsRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class WorkgroupRequest extends BaseRequest {

    @NotBlank
    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultWorkGroupAvatarUrl();

    @Builder.Default
    private String description = I18Consts.I18N_WORKGROUP_DESCRIPTION;

    @Builder.Default
    private String routingMode = WorkgroupRoutingModeEnum.ROUND_ROBIN.name();

    @Builder.Default
    private String status = WorkgroupStateEnum.AVAILABLE.name();
    
    @Builder.Default
    private MessageLeaveSettingsRequest messageLeaveSettings = new MessageLeaveSettingsRequest();

    @Builder.Default
    private RobotSettingsRequest robotSettings = new RobotSettingsRequest();

    @Builder.Default
    private ServiceSettingsRequest serviceSettings = new ServiceSettingsRequest();

    @Builder.Default
    private QueueSettingsRequest queueSettings = new QueueSettingsRequest();

    private Boolean enabled;

    // @Builder.Default
    // private InviteSettings inviteSettings = new InviteSettings();

    // 是否统一入口
    // @Builder.Default
    // private Boolean isUnifiedEntry = false;

    // 路由技能组，仅用于统一入口技能组
    // @Builder.Default
    // private List<String> routingWorkgroupUids = new ArrayList<>();

    // 注意：此处不能命名为agents，因与agent中agents类型不同, 否则会报错
    @NotEmpty(message = "agentUids must not be empty")
    @Builder.Default
    private List<String> agentUids = new ArrayList<String>();

    private String messageLeaveAgentUid;
}
