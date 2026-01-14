/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 10:16:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-12 14:58:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class AgentRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String nickname;

    /**
     * 客服工号（对外展示用）
     */
    private String agentNo;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultAgentAvatarUrl();

    @NotBlank
    private String mobile;

    @NotBlank
    @Email(message = "email format error")
    private String email;

    @Builder.Default
    private String description = I18Consts.I18N_AGENT_DESCRIPTION;

    @Builder.Default
    private String status = AgentStatusEnum.OFFLINE.name();

    @Builder.Default
    private Boolean connected = false;

    @Builder.Default
    private Boolean enabled = true;

    /**
     * Agent settings reference UID
     * If not provided, will use the default settings
     */
    private String settingsUid;

    //
    @NotBlank
    private String memberUid;
    
    // used for client query
    private String componentType;
}
