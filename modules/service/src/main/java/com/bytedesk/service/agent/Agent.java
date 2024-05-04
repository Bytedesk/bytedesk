/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:29:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.utils.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
// import lombok.extern.slf4j.Slf4j;

/**
 * 客服账号-关联信息
 */
// @Slf4j
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ AgentListener.class })
@Table(name = "service_agent")
public class Agent extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    // @Column(name = "uuid", unique = true, nullable = false)
	// private String uid;

    /**
     * visible to visitors
     */
    private String nickname;

    private String avatar;

    private String mobile;

    private String email;

    /** agent description */
    private String description;

    /**
     * @{AgentConsts}
     */
    @Builder.Default
    private String acceptStatus = AgentConsts.ACCEPT_STATUS_ACCEPTING;

    @Builder.Default
    @Column(name = "is_connected")
    private boolean connected = false;

    // max concurrent chatting thread count
    @Builder.Default
    private Integer maxThreadCount = 10;

    /**
     * tips
     * TODO: set different tips for different lang
     */
    @Builder.Default
    private String welcomeTip = BdConstants.DEFAULT_WORK_GROUP_ACCEPT_TIP;

    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    @Builder.Default
    private Double autoCloseMin = Double.valueOf(25);

    /** 存储当前接待数量等 */
    @Builder.Default
    @Column(columnDefinition = "json")
    // 用于兼容postgreSQL，否则会报错，[ERROR: column "extra" is of type json but expression is of type character varying
    @JdbcTypeCode(SqlTypes.JSON)
    private String extra = BdConstants.EMPTY_JSON_STRING;

    /**
     * login user info
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    /** belong to org */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // private Organization organization;
    private String orgUid;
    
    /**
     * belongs to user
     */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // private User owner;


    // @PostPersist
    // public void onPostPersist() {
    //     // log.debug("onPostPersist: {}", this);
    //     // 这里可以记录日志、发送通知等
    //     // create agent topic
    //     TopicService topicService = ApplicationContextHolder.getBean(TopicService.class);
    //     // 
    //     topicService.create(this.getUid(), this.getUser().getUid());
    // }
    
}

