/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-06 11:28:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-15 17:27:47
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
// import com.bytedesk.ai.robot.RobotLlm;
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

    private String orgUid;

    private String extra;

    private Boolean kbSourceEnabled;
    
    private Boolean kbEnabled;

    private String kbUid;

    private RobotLlm llm;

    public static RobotProtobuf fromJson(String user) {
        return JSON.parseObject(user, RobotProtobuf.class);
    }

    /**
     * 从 RobotEntity 创建完整的 RobotProtobuf
     * 
     * @param entity 机器人实体
     * @return 完整的机器人协议对象（包含 LLM 配置）
     */
    public static RobotProtobuf fromEntity(RobotEntity entity) {
        if (entity == null) {
            return null;
        }
        return RobotProtobuf.builder()
                .uid(entity.getUid())
                .nickname(entity.getNickname())
                .avatar(entity.getAvatar())
                .type(UserTypeEnum.ROBOT.name())
                .orgUid(entity.getOrgUid())
                .kbSourceEnabled(entity.getKbSourceEnabled())
                .kbEnabled(entity.getKbEnabled())
                .kbUid(entity.getKbUid())
                .llm(entity.getLlm())
                .build();
    }

    /**
     * 从 RobotEntity 创建完整的 RobotProtobuf，保留指定的 type
     * 
     * @param entity 机器人实体
     * @param type 指定的类型（用于兼容旧数据）
     * @return 完整的机器人协议对象（包含 LLM 配置）
     */
    public static RobotProtobuf fromEntity(RobotEntity entity, String type) {
        if (entity == null) {
            return null;
        }
        return RobotProtobuf.builder()
                .uid(entity.getUid())
                .nickname(entity.getNickname())
                .avatar(entity.getAvatar())
                .type(type != null ? type : UserTypeEnum.ROBOT.name())
                .orgUid(entity.getOrgUid())
                .kbSourceEnabled(entity.getKbSourceEnabled())
                .kbEnabled(entity.getKbEnabled())
                .kbUid(entity.getKbUid())
                .llm(entity.getLlm())
                .build();
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    // convertFromRobotEntity 已迁移至 ConvertAiUtils.convertToRobotProtobuf

    public UserProtobuf toUserProtobuf() {
        return UserProtobuf.builder()
                .uid(this.uid)
                .nickname(this.nickname)
                .avatar(this.avatar)
                .type(UserTypeEnum.ROBOT.name())
                .build();
    }

}
