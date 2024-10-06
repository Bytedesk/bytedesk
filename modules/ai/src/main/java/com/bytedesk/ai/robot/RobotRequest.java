/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:45:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-24 16:03:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import com.bytedesk.ai.settings.RobotServiceSettingsRequest;
import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class RobotRequest extends BaseRequest {

    @Builder.Default
    private String nickname = I18Consts.I18N_ROBOT_NICKNAME;
    
    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_AVATAR_URL;

    @Builder.Default
    private String description = I18Consts.I18N_ROBOT_DESCRIPTION;

    @Builder.Default
    private RobotServiceSettingsRequest serviceSettings = new RobotServiceSettingsRequest();

    @Builder.Default
    private RobotLlm llm = new RobotLlm();

    @Builder.Default
    private String defaultReply = BdConstants.EMPTY_STRING;

    @Builder.Default
    private String category = RobotCategoryEnum.DEFAULT.name();

    @Builder.Default
    private String level = LevelEnum.ORGNIZATION.name();
    
    @Builder.Default
    private Boolean published = false;

    // @Builder.Default
    // private Boolean isPrivate = false;

    private String kbUid; // 对应知识库

}
