/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 10:17:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-08 10:40:10
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
import com.bytedesk.kbase.service_settings.InviteSettings;
import com.bytedesk.kbase.service_settings.ServiceSettingsRequest;
import com.bytedesk.service.leave_msg.settings.LeaveMsgSettingsRequest;
import com.bytedesk.service.queue.settings.QueueSettingsRequest;
import com.bytedesk.service.settings.RobotSettingsRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
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
    private LeaveMsgSettingsRequest leaveMsgSettings = new LeaveMsgSettingsRequest();

    @Builder.Default
    private RobotSettingsRequest robotSettings = new RobotSettingsRequest();

    @Builder.Default
    private ServiceSettingsRequest serviceSettings = new ServiceSettingsRequest();

    @Builder.Default
    private QueueSettingsRequest queueSettings = new QueueSettingsRequest();

    @Builder.Default
    private InviteSettings inviteSettings = new InviteSettings();

    // 注意：此处不能命名为agents，因与agent中agents类型不同, 否则会报错
    @NotEmpty(message = "agentUids must not be empty")
    @Builder.Default
    private List<String> agentUids = new ArrayList<String>();
}
