/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:45:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-11 12:27:10
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

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class RobotRequest extends BaseRequest {

    private String nickname;
    
    private String avatar;

    private String description;

    // @Builder.Default
    // private RobotTypeEnum type = RobotTypeEnum.SERVICE;
    
    @Builder.Default
    private Boolean published = false;

    @Builder.Default
    private RobotServiceSettingsRequest serviceSettings = new RobotServiceSettingsRequest();

    @Builder.Default
    private RobotLlm llm = new RobotLlm();
    
    // @NotBlank
    // private String orgUid;
}
