/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-31 16:20:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-15 13:34:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserUtils {
    // 通过Spring注入服务到静态上下文，便于静态方法读取数据库
    private static UserService userService;

    public UserUtils(UserService userService) {
        UserUtils.userService = userService;
    }
    
    public static UserProtobuf getFileAssistantUser() {
        try {
            if (userService != null) {
                Optional<UserEntity> optional = userService.findByUid(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID);
                if (optional.isPresent()) {
                    UserProtobuf u = UserProtobuf.fromEntity(optional.get());
                    u.setType(UserTypeEnum.SYSTEM.name());
                    return u;
                }
            }
        } catch (Exception ignored) {
            log.debug("Failed to load file assistant user from DB, fallback to default uid={}",
                    BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID, ignored);
        }
        // 兜底：未找到数据库记录时返回默认内置信息
        return UserProtobuf.builder()
                .uid(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID)
                .nickname(I18Consts.I18N_FILE_ASSISTANT_NAME)
                .avatar(AvatarConsts.getDefaultFileAssistantAvatarUrl())
                .type(UserTypeEnum.SYSTEM.name())
                .build();
    }

    public static UserProtobuf getQueueAssistantUser() {
        try {
            if (userService != null) {
                Optional<UserEntity> optional = userService.findByUid(BytedeskConsts.DEFAULT_QUEUE_ASSISTANT_UID);
                if (optional.isPresent()) {
                    UserProtobuf u = UserProtobuf.fromEntity(optional.get());
                    u.setType(UserTypeEnum.SYSTEM.name());
                    return u;
                }
            }
        } catch (Exception ignored) {
            log.debug("Failed to load queue assistant user from DB, fallback to default uid={}",
                    BytedeskConsts.DEFAULT_QUEUE_ASSISTANT_UID, ignored);
        }
        // 兜底：未找到数据库记录时返回默认内置信息
        return UserProtobuf.builder()
                .uid(BytedeskConsts.DEFAULT_QUEUE_ASSISTANT_UID)
                .nickname(I18Consts.I18N_QUEUE_ASSISTANT_NAME)
                .avatar(AvatarConsts.getDefaultQueueAssistantAvatarUrl())
                .type(UserTypeEnum.SYSTEM.name())
                .build();
    }

    public static UserProtobuf getSystemUser() {
        try {
            if (userService != null) {
                Optional<UserEntity> optional = userService.findByUid(BytedeskConsts.DEFAULT_SYSTEM_UID);
                if (optional.isPresent()) {
                    UserProtobuf u = UserProtobuf.fromEntity(optional.get());
                    u.setType(UserTypeEnum.SYSTEM.name());
                    return u;
                }
            }
        } catch (Exception ignored) {
            log.debug("Failed to load system user from DB, fallback to default uid={}",
                    BytedeskConsts.DEFAULT_SYSTEM_UID, ignored);
        }
        // 兜底：未找到数据库记录时返回默认内置信息
        return UserProtobuf.builder()
                .uid(BytedeskConsts.DEFAULT_SYSTEM_UID)
                .nickname(I18Consts.I18N_SYSTEM_NOTIFICATION_NAME)
                .avatar(AvatarConsts.getDefaultSystemNotificationAvatarUrl())
                .type(UserTypeEnum.SYSTEM.name())
                .build();
    }

}
