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
package com.bytedesk.core.push.strategy;

import com.bytedesk.core.rbac.auth.AuthRequest;

/**
 * 认证验证策略接口
 */
public interface AuthValidationStrategy {
    
    /**
     * 验证用户状态（注册、重置、验证等场景的业务逻辑）
     * @param authRequest 认证请求
     * @param receiver 接收者
     * @param platform 平台
     */
    void validateUserStatus(AuthRequest authRequest, String receiver, String platform);
    
    /**
     * 获取策略支持的认证类型
     * @return 认证类型名称
     */
    String getSupportedAuthType();
}
