/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 10:18:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-10 15:23:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.kbase.service_settings.InviteSettings;
import com.bytedesk.kbase.service_settings.ServiceSettingsResponse;
import com.bytedesk.service.leave_msg.settings.LeaveMsgSettingsResponse;
import com.bytedesk.service.queue.settings.QueueSettingsResponse;
import com.bytedesk.service.settings.RobotSettingsResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkgroupResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;    

    private String nickname;

    private String avatar;

    private String description;

    private String routingMode;

    private String status;

    private LeaveMsgSettingsResponse leaveMsgSettings;

    private RobotSettingsResponse robotSettings;

    private ServiceSettingsResponse serviceSettings;

    private QueueSettingsResponse queueSettings;

    private InviteSettings inviteSettings;

    // 
    private List<UserProtobuf> agents;
}
