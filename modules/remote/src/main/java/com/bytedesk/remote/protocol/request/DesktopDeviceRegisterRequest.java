/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Device Register Request
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.protocol.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Device Registration Request
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DesktopDeviceRegisterRequest {

    private String deviceName;
    private String osType;
    private String osVersion;
    private String capabilities; // JSON string
    private String userUid;
    private String ipAddress;
}
