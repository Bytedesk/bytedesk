/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:45:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 12:53:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.kbase.settings.InviteSettings;
import com.bytedesk.kbase.settings.ServiceSettingsRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RobotRequest extends BaseRequest {

    @Builder.Default
    private String name = I18Consts.I18N_ROBOT_NAME;
    
    @Builder.Default
    private String nickname = I18Consts.I18N_ROBOT_NICKNAME;
    
    @Builder.Default
    private String avatar = AvatarConsts.getDefaultRobotAvatar();

    @Builder.Default
    private String description = I18Consts.I18N_ROBOT_DESCRIPTION;

    @Builder.Default
    private ServiceSettingsRequest serviceSettings = new ServiceSettingsRequest();

    @Builder.Default
    private InviteSettings inviteSettings = new InviteSettings();

    @Builder.Default
    private RobotLlm llm = new RobotLlm();

    @Builder.Default
    private Boolean isFlowEnabled = false;

    // @Builder.Default
    // private RobotFlow flow = new RobotFlow();

    @Builder.Default
    private String defaultReply = I18Consts.I18N_ROBOT_REPLY;

    // 机器人分类
    private String categoryUid;

    // @Builder.Default
    // private String level = LevelEnum.ORGANIZATION.name();
    
    @Builder.Default
    private Boolean published = false;

    @Builder.Default
    private Boolean isKbEnabled = false;

    private String kbUid;

}
