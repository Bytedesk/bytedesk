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
package com.bytedesk.service.message_leave_settings;

import jakarta.validation.constraints.NotBlank;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.service.message_leave.MessageLeaveNotifyTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.Builder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageLeaveSettingsRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;
    
    // 留言开关
    @Builder.Default
    private Boolean messageLeaveEnabled = true;

    @NotBlank
    @Builder.Default
    private String messageLeaveTip = I18Consts.I18N_MESSAGE_LEAVE_TIP;

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

    

    /**
     * 是否启用“留言表单”方式：
     * - true: 访客端在会话中发送表单消息进行留言
     * - false: 访客端使用对话框方式留言
     */
    @Builder.Default
    private Boolean messageLeaveFormEnabled = false;

    /**
     * 是否使用自定义留言表单（关联 FormEntity.uid）：
     * - true: 使用 messageLeaveFormUid 指定的自定义表单
     * - false: 使用系统自带表单（由前端内置/写死 schema）
     */
    @Builder.Default
    private Boolean messageLeaveCustomFormEnabled = false;

    /**
     * 自定义留言表单 uid（FormEntity.uid）
     */
    @Builder.Default
    private String messageLeaveFormUid = BytedeskConsts.EMPTY_STRING;

    // 留言表单
    @Builder.Default
    private String messageLeaveForm = BytedeskConsts.EMPTY_JSON_STRING;

    // ===== 备选接待（客服/工作组）=====

    // 是否启用备选接待客服
    @Builder.Default
    private Boolean messageLeaveBackupAgentEnabled = false;

    // 备选接待客服 uid（AgentEntity.uid）
    @Builder.Default
    private String messageLeaveBackupAgentUid = BytedeskConsts.EMPTY_STRING;

    // 是否启用备选接待工作组
    @Builder.Default
    private Boolean messageLeaveBackupWorkgroupEnabled = false;

    // 备选接待工作组 uid（WorkgroupEntity.uid）
    @Builder.Default
    private String messageLeaveBackupWorkgroupUid = BytedeskConsts.EMPTY_STRING;

    
}
