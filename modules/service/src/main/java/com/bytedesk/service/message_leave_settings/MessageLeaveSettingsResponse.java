/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-23 13:10:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-23 16:35:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave_settings;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.service.message_leave.MessageLeaveNotifyTypeEnum;
import com.bytedesk.service.worktime.WorktimeResponse;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageLeaveSettingsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    // 留言开关
    @Builder.Default
    private Boolean messageLeaveEnabled = true;

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

    /** work time */
    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER)
    private List<WorktimeResponse> worktimes = new ArrayList<>();
    
    /**
     * 从 MessageLeaveSettings 实体创建 MessageLeaveSettingsResponse
     * @param settings MessageLeaveSettings 实体
     * @return MessageLeaveSettingsResponse 对象,如果 settings 为 null 则返回 null
     */
    public static MessageLeaveSettingsResponse fromEntity(MessageLeaveSettingsEntity settings) {
        if (settings == null) {
            return null;
        }
        return MessageLeaveSettingsResponse.builder()
                .messageLeaveEnabled(settings.getMessageLeaveEnabled())
                .messageLeaveTip(settings.getMessageLeaveTip())
                .messageLeaveAgentUid(settings.getMessageLeaveAgentUid())
                .messageLeaveNotify(settings.getMessageLeaveNotify())
                .messageLeaveNotifyType(settings.getMessageLeaveNotifyType())
                .messageLeaveNotifyEmail(settings.getMessageLeaveNotifyEmail())
                .messageLeaveNotifyMobile(settings.getMessageLeaveNotifyMobile())
                .messageLeaveForm(settings.getMessageLeaveForm())
                .build();
    }
    
}
