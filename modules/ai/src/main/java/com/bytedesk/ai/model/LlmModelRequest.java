/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 12:20:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-12 12:06:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class LlmModelRequest extends BaseRequest {

    // 模型名称，用于调用
    private String name;
    
    // 便于记忆
    private String nickname;

    @Builder.Default
    private String description = BytedeskConsts.EMPTY_STRING;

    private String providerUid;

    @Builder.Default
    private String level = LevelEnum.PLATFORM.name();
}
