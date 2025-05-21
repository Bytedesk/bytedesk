/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-23 13:09:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-23 16:35:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.service.message_leave.MessageLeaveNotifyTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageLeaveSettingsRequest  implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // 留言开关
    @Builder.Default
    private Boolean messageLeaveEnabled = true;

    @NotBlank
    @Builder.Default
    private String messageLeaveTip = I18Consts.I18N_LEAVEMSG_TIP;

    // 处理留言agent
    @Builder.Default
    private String messageLeaveAgentUid = BytedeskConsts.EMPTY_STRING;

    // 是否支持留言提醒
    @Builder.Default
    private Boolean messageLeaveNotify = false;

    // 留言提醒类型
    @Builder.Default
    private String messageLeaveNotifyType = MessageLeaveNotifyTypeEnum.EMAIL.name();

    // 留言提醒邮箱
    @Builder.Default
    private String messageLeaveNotifyEmail = BytedeskConsts.EMPTY_STRING;

    @Builder.Default
    private String messageLeaveNotifyMobile = BytedeskConsts.EMPTY_STRING;

    // @Builder.Default
    // private String messageLeaveNotifyWechat = BytedeskConsts.EMPTY_STRING;

    // 留言提醒时间
    // @Builder.Default
    // private String messageLeaveNotifyTime = BytedeskConsts.EMPTY_STRING;

    // 留言表单
    @Builder.Default
    private String messageLeaveForm = BytedeskConsts.EMPTY_JSON_STRING;

    // @Builder.Default
    // @OneToMany(fetch = FetchType.EAGER)
    // private List<WorktimeRequest> worktimes = new ArrayList<>();
    
    @Builder.Default
    private List<String> worktimeUids = new ArrayList<>();

    
}
