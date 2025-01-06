/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-06 11:28:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-27 14:34:26
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

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotResponse;
import com.bytedesk.ai.robot.RobotProtobuf;
// import com.bytedesk.ai.settings.RobotServiceSettings;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.kbase.service_settings.ServiceSettings;
import com.bytedesk.kbase.service_settings.ServiceSettingsResponseVisitor;

public class ConvertAiUtils {

    public static final ModelMapper modelMapper = new ModelMapper();

    private ConvertAiUtils() {}

    public static RobotResponse convertToRobotResponse(RobotEntity entity) {
        return modelMapper.map(entity, RobotResponse.class);
    }

    public static RobotProtobuf convertToRobotProtobuf(RobotEntity entity) {
        return modelMapper.map(entity, RobotProtobuf.class);
    }

    public static String convertToRobotProtobufString(RobotEntity entity) {
        RobotProtobuf robotProtobuf = convertToRobotProtobuf(entity);
        return JSON.toJSONString(robotProtobuf);
    }

    public static UserProtobuf convertToUserProtobuf(RobotEntity entity) {
        UserProtobuf userProtobuf = modelMapper.map(entity, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.ROBOT.name());
        return userProtobuf;
    }

    public static String convertToUserProtobufString(RobotEntity entity) {
        UserProtobuf userProtobuf = convertToUserProtobuf(entity);
        return JSON.toJSONString(userProtobuf);
    }

    public static ServiceSettingsResponseVisitor convertToServiceSettingsResponseVisitor(
            ServiceSettings serviceSettings) {
        return modelMapper.map(serviceSettings, ServiceSettingsResponseVisitor.class);
    }

}
