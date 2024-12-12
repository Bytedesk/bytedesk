/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-06 11:28:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-05 13:10:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
import com.bytedesk.ai.settings.RobotServiceSettings;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
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

    public static UserProtobuf convertToUserProtobuf(RobotEntity entity) {
        UserProtobuf userProtobuf = modelMapper.map(entity, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.ROBOT.name());
        return userProtobuf;
    }

    public static String convertToUserProtobufString(RobotEntity entity) {
        return JSON.toJSONString(convertToUserProtobuf(entity));
    }

    public static ServiceSettingsResponseVisitor convertToServiceSettingsResponseVisitor(
            RobotServiceSettings serviceSettings) {
        return modelMapper.map(serviceSettings, ServiceSettingsResponseVisitor.class);
    }

}
