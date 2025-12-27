/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 23:33:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 12:01:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave_settings;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.service.message_leave.MessageLeaveNotifyTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@lombok.EqualsAndHashCode(callSuper = true)
@Builder
@Entity
@Table(
    name = "bytedesk_service_message_leave_settings",
    indexes = {
        @Index(name = "idx_msgleave_settings_uid", columnList = "uuid")
    }
)
@AllArgsConstructor
@NoArgsConstructor
public class MessageLeaveSettingsEntity extends BaseEntity {

    @Builder.Default
    @Column(name = "is_leave_msg_enabled")
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

    // @Builder.Default
    // private String messageLeaveNotifyTime = BytedeskConsts.EMPTY_STRING;

    // 留言表单
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String messageLeaveForm = BytedeskConsts.EMPTY_JSON_STRING;

    // TODO: 通知:邮箱、企业微信、钉钉、飞书、短信等

    /**
     * 从 MessageLeaveSettingsRequest 创建 MessageLeaveSettings 实体
     * 如果 request 为 null，返回默认构建的实体
     * 
     * @param request MessageLeaveSettingsRequest 对象，可以为 null
     * @param modelMapper ModelMapper 实例用于对象映射
     * @return MessageLeaveSettings 实体，永远不为 null
     */
    public static MessageLeaveSettingsEntity fromRequest(MessageLeaveSettingsRequest request, ModelMapper modelMapper) {
        if (request == null || modelMapper == null) {
            return MessageLeaveSettingsEntity.builder().build();
        }
        
        return modelMapper.map(request, MessageLeaveSettingsEntity.class);
    }

}
