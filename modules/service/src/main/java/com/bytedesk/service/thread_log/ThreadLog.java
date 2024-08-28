/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-09 16:34:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-26 06:32:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.thread_log;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.thread.ThreadStatusEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 记录thread的创建、更新、删除等操作
 * thread主表对于一个访客和客服之间的对话仅存放一条，当存在访客多次请求会话的情况时，在主表中仅修改主表thread状态status，
 * 在此表记录多次会话情况，便于统计，减少thread表数据量，提高查询效率。
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "service_thread_log")
public class ThreadLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // @NotBlank
    // private String title;
    // @NotBlank
    // private String avatar;

    /**
     * @{TopicConsts}
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
    // @Enumerated(EnumType.STRING)
    @Column(name = "thread_type", nullable = false)
    // private String type = ThreadTypeConsts.WORKGROUP;
    // private ThreadTypeEnum type = ThreadTypeEnum.WORKGROUP;
    private String type = ThreadTypeEnum.WORKGROUP.name();

    // closed/open
    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private String status = StatusConsts.THREAD_STATUS_OPEN;
    // private ThreadStatusEnum status = ThreadStatusEnum.NORMAL;
    private String status = ThreadStatusEnum.NORMAL.name();

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private ClientEnum client;
    private String client = ClientEnum.WEB.name();

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // 用于兼容postgreSQL，否则会报错，[ERROR: column "extra" is of type json but expression is
    // of type character varying
    @JdbcTypeCode(SqlTypes.JSON)
    private String extra = BdConstants.EMPTY_JSON_STRING;

    //
    // h2 db 不能使用 user, 所以重定义为 _user
    @Builder.Default
    @Column(name = "thread_user", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String user = BdConstants.EMPTY_JSON_STRING;

    /** belongs to user */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    /** belong to org */
    // private String orgUid;

}
