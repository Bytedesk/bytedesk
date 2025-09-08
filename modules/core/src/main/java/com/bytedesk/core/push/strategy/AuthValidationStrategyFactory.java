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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.bytedesk.core.rbac.auth.AuthTypeEnum;
import lombok.RequiredArgsConstructor;

/**
 * 认证验证策略工厂
 */
@Component
@RequiredArgsConstructor
public class AuthValidationStrategyFactory {
    
    private final List<AuthValidationStrategy> strategies;
    private Map<String, AuthValidationStrategy> strategyMap;

    public AuthValidationStrategy getStrategy(String authType) {
        if (strategyMap == null) {
            initStrategyMap();
        }
        return strategyMap.get(authType.trim().toUpperCase());
    }

    private void initStrategyMap() {
        strategyMap = strategies.stream()
            .collect(Collectors.toMap(
                strategy -> strategy.getSupportedAuthType().toUpperCase(),
                Function.identity()
            ));
        
        // 手动注册多种类型支持的策略
        AuthValidationStrategy resetStrategy = getResetStrategy();
        AuthValidationStrategy verifyStrategy = getVerifyStrategy();
        
        if (resetStrategy != null) {
            strategyMap.put(AuthTypeEnum.EMAIL_RESET.name(), resetStrategy);
            strategyMap.put(AuthTypeEnum.MOBILE_RESET.name(), resetStrategy);
        }
        
        if (verifyStrategy != null) {
            strategyMap.put(AuthTypeEnum.EMAIL_VERIFY.name(), verifyStrategy);
            strategyMap.put(AuthTypeEnum.MOBILE_VERIFY.name(), verifyStrategy);
        }
    }

    private AuthValidationStrategy getResetStrategy() {
        return strategies.stream()
            .filter(s -> s.getClass().getSimpleName().contains("Reset"))
            .findFirst()
            .orElse(null);
    }

    private AuthValidationStrategy getVerifyStrategy() {
        return strategies.stream()
            .filter(s -> s.getClass().getSimpleName().contains("Verify"))
            .findFirst()
            .orElse(null);
    }
}
