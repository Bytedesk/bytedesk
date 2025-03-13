/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:00:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-13 22:19:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractThreadEntity extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /**
     * @{TopicUtils}
     */
    @NotBlank
    @Column(name = "thread_topic", nullable = false)
    private String topic;

    @Builder.Default
    private String content = BytedeskConsts.EMPTY_STRING;

    /**
     * @{ThreadTypeConsts}
     */
    @Builder.Default
    @Column(name = "thread_type", nullable = false)
    private String type = ThreadTypeEnum.WORKGROUP.name();

    // 意图类型
    @Builder.Default
    private String intentionType = ThreadIntentionTypeEnum.OTHER.name();

    // 情绪类型
    @Builder.Default
    private String emotionType = ThreadEmotionTypeEnum.OTHER.name();

    // 质检结果
    @Builder.Default
    private String qualityCheckResult = ThreadQualityCheckResultEnum.OTHER.name();

    // 是否被评价
    @Builder.Default
    @Column(name = "is_rated")
    private boolean rated = false;

    @Builder.Default
    @Column(name = "thread_state", nullable = false)
    private String state = ThreadStateEnum.QUEUING.name();

    // 计数器编号，客服咨询首先需要取号，类似银行/医院排队系统
    @Builder.Default
    private int queueNumber = 0;

    @Builder.Default
    private int unreadCount = 1;

    // 星标
    @Builder.Default
    @Column(name = "thread_star")
    private int star = 0;

    // 置顶
    @Builder.Default
    @Column(name = "is_top")
    private boolean top = false;

    // 未读
    @Builder.Default
    @Column(name = "is_unread")
    private boolean unread = false;

    // 免打扰
    @Builder.Default
    @Column(name = "is_mute")
    private boolean mute = false;

    // 不在会话列表显示
    @Builder.Default
    @Column(name = "is_hide")
    private boolean hide = false;
    
    // 类似微信折叠会话
    @Builder.Default
    @Column(name = "is_folded")
    private boolean folded = false;

    // 自动关闭
    @Builder.Default
    @Column(name = "is_auto_close")
    private boolean autoClose = false;

    // 机器人
    @Builder.Default
    @Column(name = "is_robot")
    private boolean robot = false;

    // 备注
    @Column(name = "thread_note")
    private String note;

    // 标签
    @Builder.Default
    private String tags = BytedeskConsts.EMPTY_ARRAY_STRING;

    @Builder.Default
    private String client = ClientEnum.WEB.name();

    @Builder.Default
    @Column(name = "thread_extra", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)   
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 在客服会话中，存储访客信息
     * 在同事会话中，存储同事信息
     * 在用户私聊中，存储对方用户信息
     * 机器人会话中，存储访客信息
     * 群组会话中，存储群组信息
     * 注意：h2 db 不能使用 user, 所以重定义为 thread_user
     * @{UserProtobuf}
     */
    @Builder.Default
    @Column(name = "thread_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 一对一客服对话中，存储客服信息
     * 技能组客服对话中，存储技能组信息
     * 机器人对话中，存储机器人信息
     * 用户私聊、群聊、同事会话中，无需存储，使用owner字段信息
     * @{UserProtobuf}
     */
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String agent = BytedeskConsts.EMPTY_JSON_STRING;

    // multi agent assistants: monitoring agent、quality check agent、robot agent
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String multiAgents = BytedeskConsts.EMPTY_JSON_STRING;

    // belongs to user
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity owner;

}
