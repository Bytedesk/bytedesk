/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-08 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.strategy.impl;

import org.springframework.stereotype.Component;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.EmailExistsException;
import com.bytedesk.core.exception.MobileExistsException;
import com.bytedesk.core.push.strategy.AuthValidationStrategy;
import com.bytedesk.core.rbac.auth.AuthRequest;
import com.bytedesk.core.rbac.auth.AuthTypeEnum;
import com.bytedesk.core.rbac.user.UserService;
import lombok.RequiredArgsConstructor;

/**
 * 注册验证策略
 */
@Component
@RequiredArgsConstructor
public class RegisterValidationStrategy implements AuthValidationStrategy {
    
    private final UserService userService;

    @Override
    public void validateUserStatus(AuthRequest authRequest, String receiver, String platform) {
        // 注册验证码，如果账号已经存在，则直接抛出异常
        if (authRequest.isMobile() && userService.existsByMobileAndPlatform(receiver, platform)) {
            throw new MobileExistsException(I18Consts.I18N_MOBILE_ALREADY_EXISTS);
        }
        if (authRequest.isEmail() && userService.existsByEmailAndPlatform(receiver, platform)) {
            throw new EmailExistsException(I18Consts.I18N_EMAIL_ALREADY_EXISTS);
        }
    }

    @Override
    public String getSupportedAuthType() {
        return AuthTypeEnum.MOBILE_REGISTER.name();
    }
}
