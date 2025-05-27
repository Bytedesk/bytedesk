/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-06 11:28:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-27 10:19:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RobotProtobuf implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

    private String nickname;

    private String avatar;

    // ROBOT/AGENT/SYSTEM/USER/VISITOR/WORKGROUP
    @Builder.Default
    private String type = UserTypeEnum.ROBOT.name();

    private String extra;

    private Boolean kbEnabled;

    private String kbUid; // 对应知识库

    private RobotLlm llm;

    public static RobotProtobuf fromJson(String user) {
        return JSON.parseObject(user, RobotProtobuf.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public static RobotProtobuf convertFromRobotEntity(RobotEntity robotEntity) {
        return RobotProtobuf.builder()
                .uid(robotEntity.getUid())
                .nickname(robotEntity.getNickname())
                .avatar(robotEntity.getAvatar())
                .type(UserTypeEnum.ROBOT.name())
                .kbEnabled(robotEntity.getKbEnabled()) // 确保正确设置
                .kbUid(robotEntity.getKbUid())
                .llm(robotEntity.getLlm())
                .build();
    }

    public UserProtobuf toUserProtobuf() {
        return UserProtobuf.builder()
                .uid(this.uid)
                .nickname(this.nickname)
                .avatar(this.avatar)
                .type(UserTypeEnum.ROBOT.name())
                .build();
    }

}
