/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-10 23:50:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-11 00:04:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.oauth;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserResponse;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class OAuthResponse extends BaseResponse {
    
    private String openId;

    private String unionId;

    private String accessToken;

    private String refreshToken;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OAuthProviderEnum provider = OAuthProviderEnum.GITHUB;

    private UserResponse user;
}
