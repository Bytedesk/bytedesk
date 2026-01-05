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

import com.bytedesk.core.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class EmotionSettingResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    /**
     * 绑定用于情绪分析的机器人（RobotEntity.uid）
     */
    private String robotUid;

    /**
     * 是否启用情绪分析
     */
    private Boolean enabled;

    /**
     * 执行时机
     * - ON_MESSAGE: 收到新消息时
     * - THREAD_END: 会话结束时
     */
    private String executionTiming;

    /**
     * 触发范围
     */
    private String triggerScope;

    /**
     * 触发频率：每隔 N 条消息触发一次
     */
    private Integer triggerEveryNMessages;

    /**
     * 触发冷却时间（秒）
     */
    private Integer triggerCooldownSeconds;

    /**
     * 是否仅按时间冷却触发（忽略 triggerEveryNMessages）
     */
    private Boolean triggerCooldownOnly;

    private String type;
}
