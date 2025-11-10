/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-17 09:05:29
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

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.service.agent_settings.AgentSettingsEntity;
import com.bytedesk.core.member.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
// import lombok.extern.slf4j.Slf4j;
import lombok.experimental.SuperBuilder;

/**
 * human agent, not ai agent
 * - agent：一对一人工客服，不支持机器人接待
 * - robot：机器人客服，不支持转人工
 * - workgroup：工作组，支持机器人接待，支持转人工
 */
// @Slf4j
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ AgentEntityListener.class })
@Table(name = "bytedesk_service_agent")
public class AgentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultAgentAvatarUrl();

    @Builder.Default
    private String description = I18Consts.I18N_USER_DESCRIPTION;

    private String mobile;

    @Email(message = I18Consts.I18N_EMAIL_FORMAT_ERROR)
    private String email;

    @Builder.Default
    @Column(name = "agent_status")
    private String status = AgentStatusEnum.OFFLINE.name();


    /**
     * Configuration settings reference
     * All settings are managed through the settings entity
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    // @NotFound(action = NotFoundAction.IGNORE)
    private AgentSettingsEntity settings;

    // 以下设置项已迁移至 AgentSettingsEntity
    // 为保持兼容性，保留委托型 getter，以 settings 中的值为准

    /** 存储当前接待数量等 */
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // 是否启用，状态：启用/禁用
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    // org member
    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    public Boolean isAvailable() {
        return this.status.equals(AgentStatusEnum.AVAILABLE.name());
    }

    public Boolean isOffline() {
        return this.status.equals(AgentStatusEnum.OFFLINE.name());
    }

    public Boolean isBusy() {
        return this.status.equals(AgentStatusEnum.BUSY.name());
    }

    public Boolean isAway() {
        return this.status.equals(AgentStatusEnum.AWAY.name());
    }

    // 是否可以接待
    @Builder.Default
    @Column(name = "is_connected")
    private Boolean connected = false; // Deprecated: 将逐步由 ConnectionEntity 统计替代

    // 兼容旧代码：仍保留布尔连通判断
    public Boolean isConnectedAndAvailable() {
        return this.getConnected() && this.isAvailable();
    }

    public Boolean isConnected() {
        return this.connected != null && this.connected;
    }

    public UserProtobuf toUserProtobuf() {
        return UserProtobuf.builder()
            .uid(this.getUid())
            .nickname(this.getNickname())
            .avatar(this.getAvatar())
            .type(UserTypeEnum.AGENT.name())
            .build();
    }

    /**
     * 兼容旧代码：获取最大同时接待数量
     * 优先从 settings 读取；若无 settings 或为空，使用安全默认值 10
     */
    public Integer getMaxThreadCount() {
        if (this.settings != null && this.settings.getMaxThreadCount() != null) {
            return this.settings.getMaxThreadCount();
        }
        return 10;
    }

    /** 是否开启超时提醒（委托 settings） */
    public Boolean getTimeoutRemindEnabled() {
        if (this.settings != null && this.settings.getTimeoutRemindEnabled() != null) {
            return this.settings.getTimeoutRemindEnabled();
        }
        return false;
    }

    /** 超时提醒时间分钟数（委托 settings） */
    public Integer getTimeoutRemindTime() {
        if (this.settings != null && this.settings.getTimeoutRemindTime() != null) {
            return this.settings.getTimeoutRemindTime();
        }
        return 5;
    }

    /** 超时提醒提示（委托 settings） */
    public String getTimeoutRemindTip() {
        if (this.settings != null && this.settings.getTimeoutRemindTip() != null) {
            return this.settings.getTimeoutRemindTip();
        }
        return I18Consts.I18N_AGENT_TIMEOUT_TIP;
    }

}

