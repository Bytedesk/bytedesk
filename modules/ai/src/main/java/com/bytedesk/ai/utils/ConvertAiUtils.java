/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-06 11:28:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 15:00:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.utils;

import org.modelmapper.ModelMapper;
import lombok.experimental.UtilityClass;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotResponse;
import com.bytedesk.ai.robot_message.RobotMessageEntity;
import com.bytedesk.ai.robot_message.RobotMessageResponse;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.utils.ApplicationContextHolder;
// import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.kbase.settings.ServiceSettingsEntity;
import com.bytedesk.kbase.settings.ServiceSettingsResponseVisitor;

@UtilityClass
public class ConvertAiUtils {

    private static ModelMapper getModelMapper() {
        return ApplicationContextHolder.getBean(ModelMapper.class);
    }

    public static RobotResponse convertToRobotResponse(RobotEntity entity) {
        return getModelMapper().map(entity, RobotResponse.class);
    }

    public static RobotProtobuf convertToRobotProtobuf(RobotEntity entity) {
        RobotProtobuf robotProtobuf = getModelMapper().map(entity, RobotProtobuf.class);
        robotProtobuf.setType(UserTypeEnum.ROBOT.name());
        return robotProtobuf;
    }

    public static String convertToRobotProtobufString(RobotEntity entity) {
        RobotProtobuf robotProtobuf = convertToRobotProtobuf(entity);
        return robotProtobuf.toJson();
    }

    public static String convertToUserProtobufString(RobotEntity entity) {
        UserProtobuf robotProtobuf = getModelMapper().map(entity, UserProtobuf.class);
        robotProtobuf.setType(UserTypeEnum.ROBOT.name());
        return JSON.toJSONString(robotProtobuf);
    }

    public static ServiceSettingsResponseVisitor convertToServiceSettingsResponseVisitor(
            ServiceSettingsEntity serviceSettings) {
        return getModelMapper().map(serviceSettings, ServiceSettingsResponseVisitor.class);
    }

    public static RobotMessageResponse convertToRobotMessageResponse(RobotMessageEntity message) {
        RobotMessageResponse messageResponse = getModelMapper().map(message, RobotMessageResponse.class);
        // 
        if (message.getUser() != null) {
            UserProtobuf user = UserProtobuf.fromJson(message.getUser());
            if (user.getExtra() == null) {
                user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
            }
            messageResponse.setUser(user);
        }
        // robot
        if (message.getRobot()!= null) {
            UserProtobuf robot = UserProtobuf.fromJson(message.getRobot());
            if (robot.getExtra() == null) {
                robot.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
            }
            messageResponse.setRobot(robot);
        }
        // extra
        // if (message.getExtra() != null) {
        //     MessageExtra extra = MessageExtra.fromJson(message.getExtra());
        //     if (extra.getFeedback() == null) {
        //         extra.setFeedback(BytedeskConsts.EMPTY_JSON_STRING);
        //     }
        //     messageResponse.setExtra(extra);
        // }

        return messageResponse;
    }

}
