/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-23
 * @Description: Summary settings response
 */
package com.bytedesk.kbase.settings_summary;

import com.bytedesk.core.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SummarySettingsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    /**
     * 绑定用于会话小结的机器人（RobotEntity.uid）
     */
    private String robotUid;

    /**
     * 是否启用会话小结
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
