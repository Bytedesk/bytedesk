/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 11:09:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 11:10:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.black;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseResponse;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class IpBlacklistResponse extends BaseResponse {
    // 
    private String ip;
    private String ipLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
}
