/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Session Create Request
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.protocol.request;

import com.bytedesk.remote.protocol.DesktopModeEnum;
import com.bytedesk.remote.protocol.QualityLevelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Session Creation Request
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DesktopSessionCreateRequest {

    private String hostDeviceUid;
    private String viewerUserUid;
    private DesktopModeEnum mode;
    private QualityLevelEnum quality;
}
