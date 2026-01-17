/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Session Response
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.protocol.response;

import java.time.ZonedDateTime;

import com.bytedesk.remote.protocol.DesktopModeEnum;
import com.bytedesk.remote.protocol.DesktopSessionStatusEnum;
import com.bytedesk.remote.protocol.QualityLevelEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Session Response DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DesktopSessionResponse {

    private String uid;
    private String hostDeviceUid;
    private String hostDeviceName;
    private String viewerUserUid;
    private String viewerUserName;
    private DesktopModeEnum mode;
    private DesktopSessionStatusEnum status;
    private Boolean controlGranted;
    private QualityLevelEnum qualityLevel;
    private String resolution;
    private Integer frameRate;
    private ZonedDateTime startedAt;
    private ZonedDateTime endedAt;
    private Long durationSeconds;
    private Integer averageLatencyMs;
}
