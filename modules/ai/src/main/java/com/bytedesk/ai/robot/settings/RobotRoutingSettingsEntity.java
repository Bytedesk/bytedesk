/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 13:57:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-24 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot.settings;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.ai.robot.RobotEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Robot routing and transfer settings for workgroups
 * 
 * Purpose:
 * - Configure when to route customers to robots
 * - Define robot engagement strategies
 * - Manage human-robot transfer rules
 * 
 * Note: This is different from ai.robot_settings.RobotSettingsEntity
 * - RobotRoutingSettings: Standalone entity for workgroup robot routing strategy
 * - RobotSettingsEntity: Standalone entity for robot's own configuration
 */
@Data
@SuperBuilder
@Entity
@Table(name = "bytedesk_ai_robot_routing_settings")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
public class RobotRoutingSettingsEntity extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    // 开启机器人之后，robot字段为必填
    @lombok.Builder.Default
    private Boolean defaultRobot = false;
    
    @lombok.Builder.Default
    private Boolean offlineRobot = false;

    @lombok.Builder.Default
    private Boolean nonWorktimeRobot = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RobotEntity robot;

    /**
     * 从请求对象创建 RobotRoutingSettingsEntity。
     * create 场景下不解析 robotUid，不做实体关联，仅拷贝基础布尔字段，保持与其他 *SettingsEntity.fromRequest 风格一致。
     * 
     * @param request RobotRoutingSettingsRequest，可为 null
     * @return 非空的 RobotRoutingSettingsEntity 实例
     */
    public static RobotRoutingSettingsEntity fromRequest(RobotRoutingSettingsRequest request) {
        if (request == null ) {
            return RobotRoutingSettingsEntity.builder().build();
        }
        return RobotRoutingSettingsEntity.builder()
                .defaultRobot(Boolean.TRUE.equals(request.getDefaultRobot()))
                .offlineRobot(Boolean.TRUE.equals(request.getOfflineRobot()))
                .nonWorktimeRobot(Boolean.TRUE.equals(request.getNonWorktimeRobot()))
                // create 阶段不设置 robot 关联
                .build();
    }

    // 是否应该转接到机器人
    public Boolean shouldTransferToRobot(Boolean isOffline, Boolean isInServiceTime) {

        if (defaultRobot) {
            // 默认机器人优先接待
            return true;
        } else if (offlineRobot && isOffline) {
            // 所有客服离线,且设置机器人离线接待
            return true;
        } else if (nonWorktimeRobot && !isInServiceTime) {
            // 非工作时间,且设置机器人非工作时间接待
            return true;
        }
        // 
        return false;
    }

    

}
