/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-23
 * @Description: Summary settings request
 */
package com.bytedesk.kbase.settings_summary;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.kbase.settings_trigger.ExecutionTimingConsts;
import com.bytedesk.kbase.settings_trigger.TriggerScopeConsts;

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
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class SummarySettingsRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String name;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * 绑定用于会话小结的机器人（RobotEntity.uid）
     */
    private String robotUid;

    /**
     * 是否启用会话小结
     */
    @Builder.Default
    private Boolean enabled = true;

    /**
     * 执行时机
     * - ON_MESSAGE: 收到新消息时
     * - THREAD_END: 会话结束时
     */
    @Builder.Default
    private String executionTiming = ExecutionTimingConsts.THREAD_END;

    /**
     * 触发范围（当 executionTiming=ON_MESSAGE 时生效）
     */
    @Builder.Default
    private String triggerScope = TriggerScopeConsts.VISITOR_ONLY;

    /**
     * 触发频率（当 executionTiming=ON_MESSAGE 时生效）：每隔 N 条消息触发一次
     */
    @Builder.Default
    private Integer triggerEveryNMessages = 1;

    /**
     * 触发冷却时间（秒）（当 executionTiming=ON_MESSAGE 时生效）
     */
    @Builder.Default
    private Integer triggerCooldownSeconds = 0;

    /**
     * 是否仅按时间冷却触发（忽略 triggerEveryNMessages）
     */
    @Builder.Default
    private Boolean triggerCooldownOnly = false;
}
