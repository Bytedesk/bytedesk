/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-11 12:00:04
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

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.service.common.ServiceSettings;
// import com.bytedesk.core.constant.StatusConsts;
// import com.bytedesk.core.rbac.user.User;
import com.bytedesk.team.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
 * human agent, not ai agent
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
@EntityListeners({ AgentEntityListener.class })
// @DiscriminatorValue("Agent")
@Table(name = "service_agent", uniqueConstraints = {
// @UniqueConstraint(columnNames = { "email", "orgUid" }),
// @UniqueConstraint(columnNames = { "mobile", "orgUid" })
})
public class Agent extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * visible to visitors
     */
    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_AVATAR_URL;

    @Builder.Default
    private String description = I18Consts.I18N_USER_DESCRIPTION;

    // show on agent card
    private String mobile;

    private String email;

    /**
     * @{AgentConsts}
     */
    // @Builder.Default
    // private String acceptStatus = AgentConsts.ACCEPT_STATUS_ACCEPTING;
    @Builder.Default
    private AgentStatus status = AgentStatus.AVAILABLE;

    /**
     * @{AgentConsts}
     */
    // @Builder.Default
    // private String status = StatusConsts.AGENT_STATUS_PENDING;
    // @Builder.Default
    // private String status = StatusConsts.AGENT_STATUS_PENDING;
    // @Builder.Default
    // @Column(name = "is_enabled")
    // private boolean enabled = true;

    // TODO:是否需要跟内存中mqttsession同步
    @Builder.Default
    @Column(name = "is_connected")
    private boolean connected = false;

    @Embedded
    @Builder.Default
    private ServiceSettings serviceSettings = new ServiceSettings();

    // max concurrent chatting thread count
    @Builder.Default
    private int maxThreadCount = 10;

    // TODO: 是否需要跟进行中thread同步
    @Builder.Default
    private int currentThreadCount = 0;

    /** 存储当前接待数量等 */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // 用于兼容postgreSQL，否则会报错，[ERROR: column "extra" is of type json but expression is
    // of type character varying
    @JdbcTypeCode(SqlTypes.JSON)
    private String extra = BdConstants.EMPTY_JSON_STRING;

    /**
     * login user info
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    // private User user;
    private Member member;

    // for quick query, space exchange for speed
    private String userUid;

    /** belong to org */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // private Organization organization;
    private String orgUid;

    public void incrementThreadCount() {
        this.currentThreadCount++;
    }

    public void decrementThreadCount() {
        this.currentThreadCount--;
    }

    public Boolean isAvailable() {
        return this.status == AgentStatus.AVAILABLE;
    }

    public Boolean canAcceptMore() {
        return this.currentThreadCount < this.maxThreadCount;
    }
}
