/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_emotion;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.kbase.settings_trigger.ExecutionTimingConsts;
import com.bytedesk.kbase.settings_trigger.TriggerScopeConsts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class EmotionSettingRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String name;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * 绑定用于情绪识别的机器人（RobotEntity.uid）
     */
    private String robotUid;

    /**
     * 是否启用情绪识别
     */
    @Builder.Default
    private Boolean enabled = true;

    /**
     * 执行时机
     * - ON_MESSAGE: 收到新消息时
     * - THREAD_END: 会话结束时
     */
    @Builder.Default
    private String executionTiming = ExecutionTimingConsts.ON_MESSAGE;

    /**
     * 触发范围
     * - VISITOR_ONLY: 仅访客消息
     * - AGENT_ONLY: 仅客服消息
     * - ALL: 所有非系统消息
     */
    @Builder.Default
    private String triggerScope = TriggerScopeConsts.VISITOR_ONLY;

    /**
     * 触发频率：每隔 N 条消息触发一次
     */
    @Builder.Default
    private Integer triggerEveryNMessages = 1;

    /**
     * 触发冷却时间（秒）
     */
    @Builder.Default
    private Integer triggerCooldownSeconds = 0;

    /**
     * 是否仅按时间冷却触发（忽略 triggerEveryNMessages）
     */
    @Builder.Default
    private Boolean triggerCooldownOnly = false;
}
