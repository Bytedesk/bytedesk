/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 09:43:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.uid.UidUtils;

// import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SmartInitializingSingleton：适用于需要确保在 ApplicationContext 初始化之后再执行的初始化逻辑。
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserInitializer {

    private final UserService userService;

    private final BytedeskProperties bytedeskProperties;

    private final UidUtils uidUtils;

    // 初始化超级管理员账号, 在 organizationInitializer 中初始化后执行
    public void init() {
        // 系统只能存在一个超级管理员账号
        if (userService.existsBySuperUser()) {
            // 仍需确保系统内置用户存在
            initBuiltinSystemUsers();
            return;
        }
        log.info("init super administrator account...");
        // 创建超级管理员账号
        UserEntity superAdmin = UserEntity.builder()
                .uid(uidUtils.getUid())
                .email(bytedeskProperties.getEmail())
                .username(bytedeskProperties.getEmail())
                .password(new BCryptPasswordEncoder().encode(bytedeskProperties.getPassword()))
                .nickname(bytedeskProperties.getNickname())
                .avatar(AvatarConsts.getDefaultAvatarUrl())
                .mobile(bytedeskProperties.getMobile())
                .num(bytedeskProperties.getMobile())
                .superUser(true)
                .emailVerified(true)
                .mobileVerified(true)
                .build();
        userService.save(superAdmin);

        // 初始化系统内置用户（如系统通知、文件助手等）
        initBuiltinSystemUsers();
    }

    // 确保系统内置用户存在（可后续在后台修改昵称/头像）
    private void initBuiltinSystemUsers() {
        try {
            // 系统通知用户
            userService.findByUid(BytedeskConsts.DEFAULT_SYSTEM_UID).orElseGet(() -> {
                log.info("init builtin user: system notification");
                UserEntity systemUser = UserEntity.builder()
                        .uid(BytedeskConsts.DEFAULT_SYSTEM_UID)
                        .username(BytedeskConsts.DEFAULT_SYSTEM_UID) // username 必填，使用uid占位
                        .nickname(I18Consts.I18N_SYSTEM_NOTIFICATION_NAME)
                        .avatar(AvatarConsts.getDefaultSystemNotificationAvatarUrl())
                        .enabled(true)
                        .emailVerified(true)
                        .mobileVerified(true)
                        .build();
                return userService.save(systemUser);
            });

            // 文件助手用户
            userService.findByUid(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID).orElseGet(() -> {
                log.info("init builtin user: file assistant");
                UserEntity fileAssistant = UserEntity.builder()
                        .uid(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID)
                        .username(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID)
                        .nickname(I18Consts.I18N_FILE_ASSISTANT_NAME)
                        .avatar(AvatarConsts.getDefaultFileAssistantAvatarUrl())
                        .enabled(true)
                        .emailVerified(true)
                        .mobileVerified(true)
                        .build();
                return userService.save(fileAssistant);
            });
        } catch (Exception e) {
            log.warn("init builtin system users failed: {}", e.getMessage());
        }
    }
    
}
