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

import com.bytedesk.core.base.BaseResponse;
import lombok.AllArgsConstructor;
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

    private String messageLeaveTip;

    // 处理留言agent
    private String messageLeaveAgentUid;

    // 是否支持留言提醒
    private Boolean messageLeaveNotify;

    // 留言提醒类型
    private String messageLeaveNotifyType;

    // 留言提醒邮箱
    private String messageLeaveNotifyEmail;

    private String messageLeaveNotifyMobile;

    // @Builder.Default
    // private String messageLeaveNotifyWechat = BytedeskConsts.EMPTY_STRING;

    // 留言提醒时间
    // @Builder.Default
    // private String messageLeaveNotifyTime = BytedeskConsts.EMPTY_STRING;

    /**
     * 是否启用“留言表单”方式：
     * - true: 访客端在会话中发送表单消息进行留言
     * - false: 访客端使用对话框方式留言
     */
    private Boolean messageLeaveFormEnabled;

    /**
     * 客服离线时是否允许访客继续发送消息。
     */
    private Boolean messageLeaveAllowVisitorSendWhenOffline;

    /**
     * 是否使用自定义留言表单（关联 FormEntity.uid）：
     * - true: 使用 messageLeaveFormUid 指定的自定义表单
     * - false: 使用系统自带表单（由前端内置/写死 schema）
     */
    private Boolean messageLeaveCustomFormEnabled;

    /**
     * 自定义留言表单 uid（FormEntity.uid）
     */
    private String messageLeaveFormUid;

    // 留言表单
    private String messageLeaveForm;

    // ===== 备选接待（客服/工作组）=====

    private Boolean messageLeaveBackupAgentEnabled;

    private String messageLeaveBackupAgentUid;

    private Boolean messageLeaveBackupWorkgroupEnabled;

    private String messageLeaveBackupWorkgroupUid;

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
                .messageLeaveTip(settings.getMessageLeaveTip())
                .messageLeaveAgentUid(settings.getMessageLeaveAgentUid())
                .messageLeaveNotify(settings.getMessageLeaveNotify())
                .messageLeaveNotifyType(settings.getMessageLeaveNotifyType())
                .messageLeaveNotifyEmail(settings.getMessageLeaveNotifyEmail())
                .messageLeaveNotifyMobile(settings.getMessageLeaveNotifyMobile())
                .messageLeaveForm(settings.getMessageLeaveForm())
                .messageLeaveFormEnabled(settings.getMessageLeaveFormEnabled())
                .messageLeaveAllowVisitorSendWhenOffline(!Boolean.FALSE.equals(settings.getMessageLeaveAllowVisitorSendWhenOffline()))
                .messageLeaveCustomFormEnabled(settings.getMessageLeaveCustomFormEnabled())
                .messageLeaveFormUid(settings.getMessageLeaveFormUid())
                .messageLeaveBackupAgentEnabled(settings.getMessageLeaveBackupAgentEnabled())
                .messageLeaveBackupAgentUid(settings.getMessageLeaveBackupAgentUid())
                .messageLeaveBackupWorkgroupEnabled(settings.getMessageLeaveBackupWorkgroupEnabled())
                .messageLeaveBackupWorkgroupUid(settings.getMessageLeaveBackupWorkgroupUid())
                .build();
    }
    
}
