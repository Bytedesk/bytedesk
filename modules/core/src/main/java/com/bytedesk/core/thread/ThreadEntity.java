/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-18 15:08:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.utils.ConvertUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * every visitor <-> agent thread should only be one,
 * history records are stored in thread_log table
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ ThreadEntityListener.class })
@Table(name = "core_thread", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"topic", "owner_id"})
})
public class ThreadEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * @{TopicUtils}
     */
    @NotBlank
    private String topic;

    @Builder.Default
    private String content = BdConstants.EMPTY_STRING;

    /**
     * @{ThreadTypeConsts}
     */
    @Builder.Default
    @Column(name = "thread_type", nullable = false)
    private String type = ThreadTypeEnum.WORKGROUP.name();

    @Builder.Default
    private String state = ThreadStateEnum.INITIAL.name();

    // 计数器编号，客服咨询首先需要取号，类似银行/医院排队系统
    @Builder.Default
    private int serialNumber = 1;

    // 客户端需要此字段，暂时保留，TODO: 需要与真实未读消息数同步
    @Builder.Default
    private int unreadCount = 1;

    // 星标
    @Builder.Default
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

    // 已解决
    @Builder.Default
    @Column(name = "is_solved")
    private boolean solved = false;

    // 已评价
    @Builder.Default
    @Column(name = "is_rated")
    private boolean rated = false;

    // 自动关闭
    @Builder.Default
    @Column(name = "is_auto_close")
    private boolean autoClose = false;

    // 机器人
    @Builder.Default
    @Column(name = "is_robot")
    private boolean robot = false;

    @Builder.Default
    private String client = ClientEnum.WEB.name();

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // 用于兼容postgreSQL，否则会报错，[ERROR: column "extra" is of type json but expression is
    // of type character varying
    @JdbcTypeCode(SqlTypes.JSON)
    private String extra = BdConstants.EMPTY_JSON_STRING;

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
    @Column(name = "thread_user", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String user = BdConstants.EMPTY_JSON_STRING;

    /**
     * 一对一客服对话中，存储客服信息
     * 技能组客服对话中，存储技能组信息
     * 机器人对话中，存储机器人信息
     * 用户私聊、群聊、同事会话中，无需存储，使用owner字段信息
     * FIXME: 头像、昵称和机器人大模型中参数修改之后，不能及时同步更新
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String agent = BdConstants.EMPTY_JSON_STRING;

    // 机器人和agent可以同时存在，人工接待的时候，机器人可以同时给出答案，客服可以选用
    // 存储机器人信息
    // @Builder.Default
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // @JdbcTypeCode(SqlTypes.JSON)
    // private String robot = BdConstants.EMPTY_JSON_STRING;

    // belongs to user
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    public void reInit() {
        this.state = ThreadStateEnum.INITIAL.name();
        this.unread = false;
        this.unreadCount = 1;
        this.mute = false;
        this.hide = false;
        this.star = 0;
        this.folded = false;
        this.solved = false;
        this.rated = false;
        this.autoClose = false;
        this.robot = false;
    }

    //
    public Boolean isClosed() {
        return this.state.equals(ThreadStateEnum.CLOSED.name());
    }

    public Boolean isCustomerService() {
        return this.type.equals(ThreadTypeEnum.AGENT.name())
                || this.type.equals(ThreadTypeEnum.WORKGROUP.name());
    }

    public Boolean isRobotType() {
        return this.type.equals(ThreadTypeEnum.KB.name());
    }

    public Boolean isWorkgroupType() {
        return this.type.equals(ThreadTypeEnum.WORKGROUP.name());
    }

    public Boolean isAgentType() {
        return this.type.equals(ThreadTypeEnum.AGENT.name());
    }

    public ThreadProtobuf toProtobuf() {
        return ConvertUtils.convertToThreadProtobuf(this);
    }

    // public UserProtobuf getAgentProtobuf() {
    // return JSON.parseObject(this.agent, UserProtobuf.class);
    // }

    public UserProtobuf getUserProtobuf() {
        return JSON.parseObject(this.user, UserProtobuf.class);
    }

}
