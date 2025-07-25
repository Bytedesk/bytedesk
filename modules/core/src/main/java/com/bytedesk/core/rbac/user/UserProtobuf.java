/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-05 09:54:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 所有字段跟user.proto中字段一一对应
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserProtobuf implements Serializable {

    private static final long serialVersionUID = 1L;

	private String uid;

    private String nickname;

    private String avatar;

    @Builder.Default
    private String type = UserTypeEnum.USER.name();

    private String extra;

    public static UserProtobuf fromJson(String user) {
        if (user == null || user.trim().isEmpty()) {
            return null;
        }
        return JSON.parseObject(user, UserProtobuf.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public static UserProtobuf getSystemUser() {
        return UserProtobuf.builder()
                .uid(BytedeskConsts.DEFAULT_SYSTEM_UID)
                .nickname(I18Consts.I18N_SYSTEM_NOTIFICATION_NAME)
                .avatar(AvatarConsts.getDefaultSystemNotificationAvatarUrl())
                .type(UserTypeEnum.SYSTEM.name())
                .build();
    }

    public static UserProtobuf getFileAssistantUser() {
        return UserProtobuf.builder()
                .uid(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID)
                .nickname(I18Consts.I18N_FILE_ASSISTANT_NAME)
                .avatar(AvatarConsts.getDefaultFileAssistantAvatarUrl())
                .type(UserTypeEnum.SYSTEM.name())
                .build();
    }

    public static UserProtobuf fromEntity(UserEntity user) {
        return user.toProtobuf();
    }

    // 通过解析user字段中的type字段来判断 type=robot则为机器人，否则为访客
    public Boolean isRobot() {
        return UserTypeEnum.ROBOT.name().equalsIgnoreCase(getType());
    }

    // 通过解析user字段中的type字段来判断 type=visitor则为访客，否则为客服
    public Boolean isVisitor() {
        return UserTypeEnum.VISITOR.name().equalsIgnoreCase(getType());
    }

    public Boolean isUser() {
        return UserTypeEnum.USER.name().equalsIgnoreCase(getType());
    }

    public Boolean isMember() {
        return UserTypeEnum.MEMBER.name().equalsIgnoreCase(getType());
    }

    // 是否系统消息
    public Boolean isSystem() {
        return UserTypeEnum.SYSTEM.name().equalsIgnoreCase(getType());
    }

    // 是否客服消息
    public Boolean isAgent() {
        return UserTypeEnum.AGENT.name().equalsIgnoreCase(getType());
    }

}
