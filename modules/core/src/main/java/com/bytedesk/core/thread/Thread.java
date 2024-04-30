/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-23 09:55:39
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

import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.StatusConsts;
import com.bytedesk.core.constant.ThreadTypeConsts;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.utils.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
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
public class Thread extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tid;

    /**
     * used to push message
     * topic format:
     * workgroup_wid + '/' + visitor_vid
     * agent_aid + '/' + visitor_vid
     * such as: wid/vid or aid/vid
     */
    private String topic;

    @Builder.Default
    private String content = BdConstants.EMPTY_STRING;

    @Builder.Default
    private Integer unreadCount = 0;

    /**
     * @{ThreadTypeConsts}
     */
    @Builder.Default
    @Column(name = "by_type")
    private String type = ThreadTypeConsts.WORKGROUP;

    /** closed/open, agent closed/auto closed */
    @Builder.Default
    private String status = StatusConsts.THREAD_STATUS_OPEN;

    private String client;

    // @Lob
    @Builder.Default
    @Column(columnDefinition = "json")
    // 用于兼容postgreSQL，否则会报错，[ERROR: column "extra" is of type json but expression is of type character varying
    @JdbcTypeCode(SqlTypes.JSON)
    private String extra = BdConstants.EMPTY_JSON_STRING;

    // 
    // h2 db 不能使用 user, 所以重定义为 by_user
    @Builder.Default
    @Column(name = "by_user", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String user = BdConstants.EMPTY_JSON_STRING;

    /**
     * belongs to user
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    // TODO: 
    /** belong to org */
    private String orgOid;

}
