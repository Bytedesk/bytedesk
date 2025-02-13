/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:00:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-13 09:19:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.thread.ThreadStateEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;

/**
 * TODO: 同步thread中客服会话，包括uid。用于在访客端显示会话列表，减少thread表压力
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_visitor_thread")
public class VisitorThreadEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * used to push message
     * topic format:
     * workgroup_wid + '/' + visitor_vid
     * agent_aid + '/' + visitor_vid
     * such as: wid/vid or aid/vid
     */
    @NotBlank
    private String topic;

    @Builder.Default
    private String content = BytedeskConsts.EMPTY_STRING;

    /**
     * @{ThreadTypeConsts}
     */
    @Builder.Default
    @Column(name = "thread_type", nullable = false)
    private String type = ThreadTypeEnum.WORKGROUP.name();

    /** closed/open, agent closed/auto closed */
    @Builder.Default
    private String state = ThreadStateEnum.QUEUING.name();

    // 置顶
    @Builder.Default
    @Column(name = "is_top")
    private boolean top = false;

    // 未读
    @Builder.Default
    @Column(name = "is_unread")
    private boolean unread = false;

    // 客户端需要此字段，暂时保留，TODO: 需要与真实未读消息数同步
    @Builder.Default
    private int unreadCount = 1;

    // 免打扰
    @Builder.Default
    @Column(name = "is_mute")
    private boolean mute = false;

    // 不在会话列表显示
    @Builder.Default
    @Column(name = "is_hide")
    private boolean hide = false;

    // 星标
    @Builder.Default
    private int star = 0;

    // 类似微信折叠会话
    @Builder.Default
    @Column(name = "is_folded")
    private boolean folded = false;

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private ClientEnum client;
    private String client = ClientEnum.WEB.name();

    @Builder.Default
    // json字段格式，搜索时，对数据库有依赖，不方便迁移
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // 用于兼容postgreSQL，否则会报错，[ERROR: column "extra" is of type json but expression is
    // of type character varying
    // @JdbcTypeCode(SqlTypes.JSON)
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 在客服会话中，存储访客信息
     * 在同事会话中，存储同事信息
     * 在用户私聊中，存储对方用户信息
     * 机器人会话中，存储访客信息
     * 群组会话中，存储群组信息
     * FIXME: 同事对话中，对方更新头像之后，不能及时同步更新
     * 注意：h2 db 不能使用 user, 所以重定义为 thread_user
     */
    @Builder.Default
    // json字段格式，搜索时，对数据库有依赖，不方便迁移
    // @Column(name = "thread_user", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "thread_user")
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 一对一客服对话中，存储客服信息
     * 技能组客服对话中，存储技能组信息
     * 机器人对话中，存储机器人信息
     * 用户私聊、群聊、同事会话中，无需存储，使用owner字段信息
     * FIXME: 头像、昵称和机器人大模型中参数修改之后，不能及时同步更新
     */
    @Builder.Default
    // json字段格式，搜索时，对数据库有依赖，不方便迁移
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "thread_agent")
    private String agent = BytedeskConsts.EMPTY_JSON_STRING;

    // 机器人和agent可以同时存在，人工接待的时候，机器人可以同时给出答案，客服可以选用
    // 存储机器人信息
    // @Builder.Default
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSONB)
    // @JdbcTypeCode(SqlTypes.JSON)
    // private String robot = BytedeskConsts.EMPTY_JSON_STRING;

    private String visitorUid;
}
