/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-04 11:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 15:07:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.utils;

import org.modelmapper.ModelMapper;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentResponse;
import com.bytedesk.kbase.service_settings.ServiceSettings;
import com.bytedesk.kbase.service_settings.ServiceSettingsResponseVisitor;
import com.bytedesk.service.visitor.VisitorEntity;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor.VisitorResponse;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupResponse;

public class ConvertServiceUtils {

    private static final ModelMapper modelMapper = new ModelMapper(); // 添加静态ModelMapper实例
    
    private ConvertServiceUtils() {}

    //
    public static VisitorResponse convertToVisitorResponse(VisitorEntity visitor) {
        return modelMapper.map(visitor, VisitorResponse.class);
    }

    public static UserProtobuf convertToUserProtobuf(VisitorEntity visitor) {
        return modelMapper.map(visitor, UserProtobuf.class);
    }

    public static UserProtobuf convertToUserProtobuf(VisitorRequest visitorRequest) {
        UserProtobuf userProtobuf = modelMapper.map(visitorRequest, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.VISITOR.name());
        return userProtobuf;
    }

    public static String convertToUserProtobufJSONString(VisitorRequest visitorRequest) {
        return JSON.toJSONString(convertToUserProtobuf(visitorRequest));
    }

    public static UserProtobuf convertToUserResponseSimple(UserProtobuf visitorResponseSimple) {
        return modelMapper.map(visitorResponseSimple, UserProtobuf.class);
    }

    public static MessageProtobuf convertToMessageProtobuf(MessageEntity lastMessage, ThreadEntity thread) {
        //
        MessageProtobuf messageProtobuf = modelMapper.map(lastMessage, MessageProtobuf.class);
        messageProtobuf.setThread(ConvertUtils.convertToThreadProtobuf(thread));
        //
        UserProtobuf user = JSON.parseObject(lastMessage.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }
        messageProtobuf.setUser(user);

        return messageProtobuf;
    }

    public static MessageResponse convertToMessageResponse(MessageEntity lastMessage, ThreadEntity thread) {
        //
        MessageResponse messageResponse = modelMapper.map(lastMessage, MessageResponse.class);
        // messageResponse.setThread(ConvertUtils.convertToThreadProtobuf(thread));
        //
        UserProtobuf user = JSON.parseObject(lastMessage.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }
        messageResponse.setUser(user);

        return messageResponse;
    }

    //
    public static AgentResponse convertToAgentResponse(AgentEntity agent) {
        return modelMapper.map(agent, AgentResponse.class);
    }

    public static UserProtobuf convertToUserProtobuf(AgentEntity agent) {
        UserProtobuf userProtobuf = modelMapper.map(agent, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.AGENT.name());
        return userProtobuf;
    }

    public static String convertToUserProtobufJSONString(AgentEntity agent) {
        return JSON.toJSONString(convertToUserProtobuf(agent));
    }

    //
    public static WorkgroupResponse convertToWorkgroupResponse(WorkgroupEntity workgroup) {
        return modelMapper.map(workgroup, WorkgroupResponse.class);
    }
    
    //
    public static ServiceSettingsResponseVisitor convertToServiceSettingsResponseVisitor(
            ServiceSettings serviceSettings) {
        return modelMapper.map(serviceSettings, ServiceSettingsResponseVisitor.class);
    }

    public static String convertToServiceSettingsResponseVisitorJSONString(
        ServiceSettings serviceSettings) {
        return JSON.toJSONString(convertToServiceSettingsResponseVisitor(serviceSettings));
    }


}
