/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 14:28:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.unified;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.workgroup.WorkgroupEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_unified")
public class UnifiedEntity extends BaseEntity {

    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultWorkGroupAvatarUrl();

    @Builder.Default
    private String description = I18Consts.I18N_WORKGROUP_DESCRIPTION;
    
    @Builder.Default
    @Column(name = "unified_type", nullable = false)
    private String type = ThreadTypeEnum.WORKGROUP.name();

    @Builder.Default
    private String status = UnifiedStateEnum.AVAILABLE.name();

    @NotBlank
    @Builder.Default
    private String language = LanguageEnum.ZH_CN.name();

    @NotBlank
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String welcomeTip = I18Consts.I18N_WELCOME_TIP;

    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();

    // 统一入口技能组
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<WorkgroupEntity> workgroups = new ArrayList<>();
    
    // 统一入口一对一客服
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<AgentEntity> agents = new ArrayList<>();

    // 统一入口机器人
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<RobotEntity> robots = new ArrayList<>();

    private String userUid;
}
