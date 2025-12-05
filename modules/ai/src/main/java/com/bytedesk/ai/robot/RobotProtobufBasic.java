/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-05
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
import com.bytedesk.core.rbac.user.UserTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 机器人基础信息协议类 - 用于 thread.robot 字段存储
 * 
 * <p>只包含显示所需的基础信息，不包含 LLM 配置等大字段，
 * 避免 prompt 过长导致 thread.robot 字段超出长度限制。
 * 
 * <p>完整的 LLM 配置应通过 robotUid 从数据库/缓存中获取。
 * 
 * @see RobotProtobuf 完整版机器人协议
 * @see RobotRestService#findByUid 获取完整机器人信息
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RobotProtobufBasic implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 机器人唯一标识，用于后续从数据库获取完整配置
     */
    private String uid;

    /**
     * 机器人昵称，用于显示
     */
    private String nickname;

    /**
     * 机器人头像，用于显示
     */
    private String avatar;

    /**
     * 类型：ROBOT
     */
    @Builder.Default
    private String type = UserTypeEnum.ROBOT.name();

    /**
     * 组织UID
     */
    private String orgUid;

    /**
     * 从 JSON 字符串解析
     */
    public static RobotProtobufBasic fromJson(String json) {
        return JSON.parseObject(json, RobotProtobufBasic.class);
    }

    /**
     * 转换为 JSON 字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }

    /**
     * 从 RobotEntity 创建基础协议对象
     */
    public static RobotProtobufBasic fromEntity(RobotEntity entity) {
        if (entity == null) {
            return null;
        }
        return RobotProtobufBasic.builder()
                .uid(entity.getUid())
                .nickname(entity.getNickname())
                .avatar(entity.getAvatar())
                .type(UserTypeEnum.ROBOT.name())
                .orgUid(entity.getOrgUid())
                .build();
    }

    /**
     * 从 RobotProtobuf 创建基础协议对象
     */
    public static RobotProtobufBasic fromProtobuf(RobotProtobuf protobuf) {
        if (protobuf == null) {
            return null;
        }
        return RobotProtobufBasic.builder()
                .uid(protobuf.getUid())
                .nickname(protobuf.getNickname())
                .avatar(protobuf.getAvatar())
                .type(protobuf.getType())
                .orgUid(protobuf.getOrgUid())
                .build();
    }
}
