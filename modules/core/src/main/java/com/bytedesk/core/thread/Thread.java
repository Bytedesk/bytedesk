/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-14 13:19:38
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

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.user.User;
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
@EntityListeners({ ThreadListener.class })
@Table(name = "core_thread")
public class Thread extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // @NotBlank
    // private String title;

    // @NotBlank
    // private String avatar;

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
    private String content = BdConstants.EMPTY_STRING;

    @Builder.Default
    private Integer unreadCount = 0;

    /**
     * @{ThreadTypeConsts}
     */
    @Builder.Default
    @Column(name = TypeConsts.COLUMN_NAME_TYPE)
    // private String type = ThreadTypeConsts.WORKGROUP;
    private ThreadTypeEnum type = ThreadTypeEnum.WORKGROUP;

    /** closed/open, agent closed/auto closed */
    @Builder.Default
    // private String status = StatusConsts.THREAD_STATUS_OPEN;
    private ThreadStatusEnum status = ThreadStatusEnum.OPEN;

    // private String client;
    private ClientEnum client;

    /**
     * 用途：
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // 用于兼容postgreSQL，否则会报错，[ERROR: column "extra" is of type json but expression is
    // of type character varying
    @JdbcTypeCode(SqlTypes.JSON)
    private String extra = BdConstants.EMPTY_JSON_STRING;

    /**
     * 在客服会话中，存储访客信息
     * 在同事会话中，存储同事信息
     * FIXME: 同事对话中，对方更新头像之后，不能及时同步更新
     * 注意：h2 db 不能使用 user, 所以重定义为 by_user
     */
    @Builder.Default
    @Column(name = "by_user", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String user = BdConstants.EMPTY_JSON_STRING;
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // private BaseUser user;

    /**
     * 一对一客服对话中，存储客服信息
     * 技能组对话中，存储技能组信息
     * 机器人对话中，存储机器人信息
     * FIXME: 头像、昵称和机器人大模型中参数修改之后，不能及时同步更新
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String agent = BdConstants.EMPTY_JSON_STRING;

    /**
     * belongs to user
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    /** belong to org */
    private String orgUid;

    //
    public Boolean isClosed() {
        return this.status != ThreadStatusEnum.OPEN;
    }

}
