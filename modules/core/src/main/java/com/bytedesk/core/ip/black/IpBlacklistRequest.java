/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 11:09:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 16:34:46
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

import com.bytedesk.core.base.BaseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpBlacklistRequest extends BaseRequest {
    // 
    private String ip;
    private String ipLocation;
    // 
    // 开始时间
    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime = LocalDateTime.now();

    // 结束时间
    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime = LocalDateTime.now().plusHours(24);
    // 
    private String reason;
    // 
    private String blackUid;
    private String blackNickname;

    private String userUid;
    private String userNickname;
}
