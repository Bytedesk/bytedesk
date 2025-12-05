/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-04
 * @Description: Robot-to-agent settings response DTO
 */
package com.bytedesk.service.robot_to_agent_settings;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RobotToAgentSettingsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private Boolean enabled = Boolean.TRUE;

    @Builder.Default
    private Boolean keywordTriggerEnabled = Boolean.TRUE;

    @Builder.Default
    private Integer minConfidence = 65;

    @Builder.Default
    private Integer maxRobotRepliesBeforeTransfer = 3;

    @Builder.Default
    private Integer autoTransferDelaySeconds = 0;

    @Builder.Default
    private Integer cooldownSeconds = 90;

    @Builder.Default
    private Boolean allowVisitorManualTransfer = Boolean.TRUE;

    @Builder.Default
    private String manualTransferLabel = "转人工客服";

    @Builder.Default
    private List<String> triggerKeywords = new ArrayList<>();
}
