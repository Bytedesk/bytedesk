/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 23:34:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 16:49:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.settings;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@lombok.EqualsAndHashCode(callSuper = true)
@Builder
@Entity
@Table(
    name = "bytedesk_kbase_auto_reply_settings",
    indexes = {
        @Index(name = "idx_autoreply_settings_uid", columnList = "uuid")
    }
)
@AllArgsConstructor
@NoArgsConstructor
public class AutoReplySettingsEntity extends BaseEntity {

    // 自动回复开关
    @Builder.Default
    @Column(name = "is_autoreply_enabled")
    private Boolean autoReplyEnabled = false;

    // 自动回复类型
    @Builder.Default
    private String autoReplyType = AutoReplyTypeEnum.FIXED.name();

    // 固定回复类型所需要字段
    @Builder.Default
    private String autoReplyUid = BytedeskConsts.EMPTY_STRING;
    
    // 自动回复内容类型
    @Builder.Default
    private String autoReplyContentType = MessageTypeEnum.TEXT.name();

    // 自动回复内容
    @Builder.Default
    private String autoReplyContent = BytedeskConsts.EMPTY_STRING;

    // 关键词回复类型所需要字段
    // 大模型回复类型所需要字段
    private String kbUid;

    // 是否启用大模型接管会话
    @Builder.Default
    @Column(name = "is_takeover_enabled")
    private Boolean takeoverEnabled = false;

    // 接管会话的机器人 uid（RobotEntity.uid）
    @Builder.Default
    @Column(name = "robot_uid")
    private String robotUid = BytedeskConsts.EMPTY_STRING;

    /**
     * 从 AutoReplySettingsRequest 创建 AutoReplySettings 实体
     * 如果 request 为 null，返回默认构建的实体
     * 
     * @param request AutoReplySettingsRequest 对象，可以为 null
     * @param modelMapper ModelMapper 实例用于对象映射
     * @return AutoReplySettings 实体，永远不为 null
     */
    public static AutoReplySettingsEntity fromRequest(AutoReplySettingsRequest request, ModelMapper modelMapper) {
        if (request == null) {
            return AutoReplySettingsEntity.builder().build();
        }
        
        return modelMapper.map(request, AutoReplySettingsEntity.class);
    }
}
