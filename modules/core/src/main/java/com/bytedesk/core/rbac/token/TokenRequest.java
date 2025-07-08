/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-08 11:29:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-22 15:47:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.token;

import java.time.ZonedDateTime;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest extends BaseRequest {

    private String name;

    private String description;

    private String accessToken;

    private String refreshToken;

    // private String type;

    private List<String> scope;

    private ZonedDateTime expiresAt;

    private Boolean revoked;

    private String revokeReason;

    private String client;

    // 设备信息，比如安卓手机信息、苹果手机信息等
    private String device;


}
