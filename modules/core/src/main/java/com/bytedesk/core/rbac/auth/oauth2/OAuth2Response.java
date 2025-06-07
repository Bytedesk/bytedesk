/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-10 23:50:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-07 10:38:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth.oauth2;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2Response extends BaseResponse {
    
    private String openId;

    private String unionId;

    private String accessToken;

    private String refreshToken;

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private OAuth2ProviderEnum provider = OAuth2ProviderEnum.GITHUB;
    private String provider = OAuth2ProviderEnum.GITHUB.name();

    private UserResponse user;
}
