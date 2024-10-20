/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-18 17:13:52
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
import com.bytedesk.kbase.auto_reply.AutoReplySettings;
import com.bytedesk.service.settings.ServiceSettings;
import com.bytedesk.team.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
// import lombok.extern.slf4j.Slf4j;

/**
 * human agent, not ai agent
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
        @UniqueConstraint(columnNames = { "userUid", "orgUid" })
})
public class Agent extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // show to the visitors
    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_AVATAR_URL;

    @Builder.Default
    private String description = I18Consts.I18N_USER_DESCRIPTION;

    // show on agent card
    private String mobile;

    private String email;

    @Builder.Default
    private String status = AgentStateEnum.AVAILABLE.name();

    @Builder.Default
    @Column(name = "is_connected")
    private boolean connected = false;

    @Embedded
    @Builder.Default
    private ServiceSettings serviceSettings = new ServiceSettings();

    @Embedded
    @Builder.Default
    private AutoReplySettings autoReplySettings = new AutoReplySettings();

    // current thread count
    @Builder.Default
    private int currentThreadCount = 0;

    // max concurrent chatting thread count
    @Builder.Default
    private int maxThreadCount = 10;

    /** 存储当前接待数量等 */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // 用于兼容postgreSQL，否则会报错，[ERROR: column "extra" is of type json but expression is
    // of type character varying
    @JdbcTypeCode(SqlTypes.JSON)
    private String extra = BdConstants.EMPTY_JSON_STRING;

    // org member
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    // for quick query, space exchange for speed
    private String userUid;

    public Boolean isAvailable() {
        return this.status.equals(AgentStateEnum.AVAILABLE.name());
    }

    public Boolean isOffline() {
        return this.status.equals(AgentStateEnum.OFFLINE.name());
    }

    public Boolean isBusy() {
        return this.status.equals(AgentStateEnum.BUSY.name());
    }

    public Boolean isAway() {
        return this.status.equals(AgentStateEnum.AWAY.name());
    }

    // 是否可以接待
    public Boolean isConnectedAndAvailable() {
        return this.isConnected() && this.isAvailable();
    }

    
}
