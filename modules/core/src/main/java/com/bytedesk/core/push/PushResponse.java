/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:42:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 09:56:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import com.bytedesk.core.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PushResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String sender;

    private String content;

    private String country;
    private String receiver; // email or mobile

    private String type;

    private String ip;

    private String ipLocation;

    private String deviceUid; // 设备唯一标识

    private PushStatusEnum status;

    private String channel;

    // private PlatformEnum platform;

    // private ZonedDateTime createdAt;

    // private ZonedDateTime updatedAt;
}
