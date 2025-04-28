/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-28 11:45:04
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
import com.bytedesk.kbase.auto_reply.settings.AutoReplySettings;
import com.bytedesk.kbase.settings.InviteSettings;
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettings;
import com.bytedesk.service.queue.settings.QueueSettings;
import com.bytedesk.team.member.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
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
@Table(name = "bytedesk_service_agent", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "userUid", "orgUid" })
})
public class AgentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultAgentAvatarUrl();

    @Builder.Default
    private String description = I18Consts.I18N_USER_DESCRIPTION;

    // only support chinese mobile number, 
    // TODO: support other country mobile number using libphonenumber library
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = I18Consts.I18N_MOBILE_FORMAT_ERROR)
    private String mobile;

    @Email(message = I18Consts.I18N_EMAIL_FORMAT_ERROR)
    private String email;

    @Builder.Default
    @Column(name = "agent_status")
    private String status = AgentStatusEnum.OFFLINE.name();

    @Builder.Default
    @Column(name = "is_connected")
    private boolean connected = false;

    @Builder.Default
    @Column(name = "is_enabled")
    private boolean enabled = true;

    // 留言设置
    @Embedded
    @Builder.Default
    private MessageLeaveSettings messageLeaveSettings = new MessageLeaveSettings();

    // 一对一人工客服，不支持机器人接待。通过自动回复设置，可以自动回复访客消息
    // @Embedded
    // @Builder.Default
    // private RobotSettings robotSettings = new RobotSettings();

    @Embedded
    @Builder.Default
    private ServiceSettings serviceSettings = new ServiceSettings();

    @Embedded
    @Builder.Default
    private AutoReplySettings autoReplySettings = new AutoReplySettings();

    @Embedded
    @Builder.Default
    private QueueSettings queueSettings = new QueueSettings();

    @Embedded
    @Builder.Default
    private InviteSettings inviteSettings = new InviteSettings();

    /**
     * 迁移到 @{QueueEntity}
     * 容易导致不一致，使用实时计算chatting thread数量
     */
    // @Builder.Default
    // private int currentThreadCount = 0;

    // 最大同时接待数量
    // max concurrent chatting thread count
    @Builder.Default
    private int maxThreadCount = 10;

    // 超时提醒时间：分钟
    // 当客服超过这个时间没有接待新的会话时，会提醒客服
    @Builder.Default
    private int timeoutRemindTime = 5;

    /** 存储当前接待数量等 */
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // org member
    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    // for quick query, space exchange for speed
    // private String userUid;

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
    public Boolean isConnectedAndAvailable() {
        return this.isConnected() && this.isAvailable();
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
     主要用途：
        精准分配：将会话分配给最合适的客服
        专业服务：确保客服具备处理特定问题的能力
        分层支持：实现初级/高级支持分流
        多语言支持：按语言技能分配
        产品线划分：不同产品对应不同技能组
     */
    // @Column(length = 1000)
    // private String skills;  // 技能标签,逗号分隔,如: "java,python,database"
    
    // // 将skills字符串转换为List
    // public List<String> getSkillList() {
    //     if (skills == null || skills.isEmpty()) {
    //         return new ArrayList<>();
    //     }
    //     return Arrays.asList(skills.split(","));
    // }
    
    // // 检查是否具备某个技能
    // public boolean hasSkill(String skill) {
    //     return getSkillList().contains(skill);
    // }
    
    // // 检查是否具备所有必需技能
    // public boolean hasRequiredSkills(List<String> requiredSkills) {
    //     return getSkillList().containsAll(requiredSkills);
    // }


}

