/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:16:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-29 15:33:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.kbase.settings_intention.IntentionSettingsEntity;
import com.bytedesk.kbase.settings_invite.InviteSettingsEntity;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

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
import lombok.experimental.SuperBuilder;

/**
 * AI robot entity for automated customer service
 * Supports different service types: agent (human-only), robot (AI-only), workgroup (hybrid)
 * 
 * Database Table: bytedesk_ai_robot
 * Purpose: Stores AI robot configurations, LLM settings, and service parameters
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ RobotEntityListener.class })
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
// 限制name + orgUid唯一索引，防止重复创建机器人
@Table(name = "bytedesk_ai_robot"
// , uniqueConstraints = {
    // UniqueConstraint(columnNames = {"name", "orgUid"})
// }
)
public class RobotEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Internal name of the robot
     */
    @Builder.Default
    private String name = I18Consts.I18N_ROBOT_NAME;

    /**
     * Display name shown to customers
     */
    @Builder.Default
    private String nickname = I18Consts.I18N_ROBOT_NICKNAME;

    /**
     * Avatar image URL for the robot
     */
    @Builder.Default
    private String avatar = AvatarConsts.getDefaultRobotAvatar();

    /**
     * Description of the robot's capabilities
     */
    @Builder.Default
    private String description = I18Consts.I18N_ROBOT_DESCRIPTION;
    
    /**
     * LLM configuration settings for the robot
     */
    @Embedded
    @Builder.Default
    private RobotLlm llm = new RobotLlm();

    /**
     * Service settings and parameters for the robot
     */
    @Embedded
    @Builder.Default
    private ServiceSettings serviceSettings = new ServiceSettings();

    // 如果未匹配到关键词，默认回复内容
    // @Builder.Default
    // private String defaultReply = I18Consts.I18N_ROBOT_DEFAULT_REPLY;

    /**
     * Type of robot service (SERVICE, ASK, CHAT)
     */
    @Builder.Default
    @Column(name = "robot_type")
    private String type = RobotTypeEnum.SERVICE.name();

    /**
     * Whether to use streaming responses for real-time interaction
     */
    @Builder.Default
    @Column(name = "is_stream")
    private Boolean stream = true;

    // @Builder.Default
    // private Boolean published = false;

    /**
     * Whether knowledge base integration is enabled
     */
    @Builder.Default
    @Column(name = "is_kb_enabled")
    private Boolean kbEnabled = false;

    /**
     * Associated knowledge base UID for AI responses
     */
    private String kbUid; // 对应知识库

    /**
     * Whether workflow/flow integration is enabled
     */
    @Builder.Default
    @Column(name = "is_flow_enabled")
    private Boolean flowEnabled = false;

    /**
     * Associated workflow/flow UID for automated processes
     */
    private String flowUid;

    /**
     * Associated category UID for robot classification
     */
    private String categoryUid; // 机器人分类

    /**
     * Whether this is a system-provided robot
     */
    @Builder.Default
    @Column(name = "is_system")
    private Boolean system = false;

    /**
     * Invitation settings for the robot
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private InviteSettingsEntity inviteSettings;

    /**
     * Rating down settings for feedback collection
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private RatedownSettingsEntity rateDownSettings;

    /**
     * Intention recognition settings for the robot
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private IntentionSettingsEntity intentionSetting;

    public UserProtobuf toUserProtobuf() {
        return UserProtobuf.builder()
            .uid(this.getUid())
            .nickname(this.getNickname())
            .avatar(this.getAvatar())
            .type(UserTypeEnum.ROBOT.name())
            .build();
    }
}
