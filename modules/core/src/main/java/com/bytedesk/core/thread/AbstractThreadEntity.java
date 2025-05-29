/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:00:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 13:26:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.converter.JsonListConverter;
import com.bytedesk.core.converter.StringListConverter;

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
import lombok.ToString;
import jakarta.persistence.Convert;

@MappedSuperclass
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
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

    // process status
    @Builder.Default
    @Column(name = "thread_status", nullable = false)
    private String status = ThreadProcessStatusEnum.NEW.name();

    // 
    @Builder.Default
    private Integer unreadCount = 1;

    // 星标
    @Builder.Default
    @Column(name = "thread_star")
    private Integer star = 0;

    // 置顶
    @Builder.Default
    @Column(name = "is_top")
    private Boolean top = false;

    // 未读
    @Builder.Default
    @Column(name = "is_unread")
    private Boolean unread = false;

    // 免打扰
    @Builder.Default
    @Column(name = "is_mute")
    private Boolean mute = false;

    // 不在会话列表显示
    @Builder.Default
    @Column(name = "is_hide")
    private Boolean hide = false;
    
    // 类似微信折叠会话
    @Builder.Default
    @Column(name = "is_folded")
    private Boolean folded = false;

    // 自动关闭
    @Builder.Default
    @Column(name = "is_auto_close")
    private Boolean autoClose = false;

    // 备注
    @Column(name = "thread_note")
    private String note;

    // 标签
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

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
     * @{UserProtobuf}
     */
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String agent = BytedeskConsts.EMPTY_JSON_STRING;

    // 机器人对话中，存储机器人信息
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String robot = BytedeskConsts.EMPTY_JSON_STRING;

    // 技能组客服对话中，存储技能组信息
    // workgroup
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String workgroup = BytedeskConsts.EMPTY_JSON_STRING;

    // 邀请多个客服参与会话，存放多个 UserProtobuf 实体转换成的 JSON
    // 每个字符串元素都是一个 UserProtobuf 实体序列化成的 JSON
    @Builder.Default
    @Convert(converter = JsonListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> invites = new ArrayList<>();

    // 多个管理员监听会话
    @Builder.Default
    @Convert(converter = JsonListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> monitors = new ArrayList<>();

    // assistants: monitoring agent、quality check agent、robot agent
    @Builder.Default
    @Convert(converter = JsonListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> assistants = new ArrayList<>();

    // ticketors: ticket observers
    @Builder.Default
    @Convert(converter = JsonListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> ticketors = new ArrayList<>();

    // 流程实例ID
    private String processInstanceId;

    // 流程定义实体UID
    private String processEntityUid;

    // belongs to user
    @ManyToOne(fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private UserEntity owner;

}
