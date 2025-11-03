/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23 14:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-23 14:40:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_settings;

import com.bytedesk.kbase.settings.BaseSettingsRequest;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RobotSettingsRequest extends BaseSettingsRequest {

    private static final long serialVersionUID = 1L;

    // private Boolean kbSourceEnabled;

    // // 从 RobotEntity 迁移
    // private Boolean kbEnabled;
    // private String kbUid;

    // LLM 配置（实体引用通过 uid 关联或内联创建）
    // private RobotLlmRequest llm;

    /**
     * Rating down settings (Robot-specific)
     */
    private RatedownSettingsRequest rateDownSettings;
}
