/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-04 11:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 15:37:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
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
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.kbase.settings.ServiceSettingsResponseVisitor;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentResponse;
import com.bytedesk.service.visitor.VisitorEntity;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor.VisitorResponse;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupResponse;

public class ServiceConvertUtils {

    private static final ModelMapper modelMapper = new ModelMapper(); // 添加静态ModelMapper实例
    
    private ServiceConvertUtils() {}

    //
    public static VisitorResponse convertToVisitorResponse(VisitorEntity visitor) {
        return modelMapper.map(visitor, VisitorResponse.class);
    }

    public static UserProtobuf convertToUserProtobuf(VisitorEntity visitor) {
        return modelMapper.map(visitor, UserProtobuf.class);
    }

    public static UserProtobuf convertToUserProtobuf(VisitorResponse visitor) {
        return modelMapper.map(visitor, UserProtobuf.class);
    }

    public static UserProtobuf convertToUserProtobuf(VisitorRequest visitorRequest) {
        UserProtobuf userProtobuf = modelMapper.map(visitorRequest, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.VISITOR.name());
        return userProtobuf;
    }

    public static String convertToUserProtobufJSONString(VisitorRequest visitorRequest) {
        UserProtobuf userProtobuf = convertToUserProtobuf(visitorRequest);
        return JSON.toJSONString(userProtobuf);
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
        // try {
        //     // 确保agent对象已经完全初始化（非代理状态）
        //     Hibernate.initialize(agent);
            
        //     // 如果有成员关系，确保成员也已经初始化
        //     if (agent.getMember() != null) {
        //         Hibernate.initialize(agent.getMember());
        //     }
            
        //     // 直接创建新的AgentResponse对象，手动设置必要的属性
        //     AgentResponse response = new AgentResponse();
        //     response.setUid(agent.getUid());
        //     response.setOrgUid(agent.getOrgUid());
        //     response.setNickname(agent.getNickname());
        //     response.setAvatar(agent.getAvatar());
        //     response.setMobile(agent.getMobile());
        //     response.setEmail(agent.getEmail());
        //     response.setDescription(agent.getDescription());
        //     response.setStatus(agent.getStatus());
        //     response.setConnected(agent.isConnected());
        //     response.setMaxThreadCount(agent.getMaxThreadCount());
        //     response.setCurrentThreadCount(agent.getCurrentThreadCount());
            
        //     // 复制其他必要的属性
        //     if (agent.getServiceSettings() != null) {
        //         ServiceSettingsResponse serviceSettings = modelMapper.map(agent.getServiceSettings(), ServiceSettingsResponse.class);
        //         response.setServiceSettings(serviceSettings);
        //     }
        //     if (agent.getAutoReplySettings() != null) {
        //         response.setAutoReplySettings(agent.getAutoReplySettings());
        //     }
        //     if (agent.getQueueSettings() != null) {
        //         // response.setQueueSettings(agent.getQueueSettings());
        //         QueueSettingsResponse queueSettings = modelMapper.map(agent.getQueueSettings(), QueueSettingsResponse.class);
        //         response.setQueueSettings(queueSettings);
        //     }
        //     if (agent.getMessageLeaveSettings() != null) {
        //         // response.setMessageLeaveSettings(agent.getMessageLeaveSettings());
        //         MessageLeaveSettingsResponse messageLeaveSettings = modelMapper.map(agent.getMessageLeaveSettings(), MessageLeaveSettingsResponse.class);
        //         response.setMessageLeaveSettings(messageLeaveSettings);
        //     }
        //     if (agent.getInviteSettings() != null) {
        //         response.setInviteSettings(agent.getInviteSettings());
        //     }
            
        //     // 设置userUid
        //     if (agent.getUserUid() != null) {
        //         response.setUserUid(agent.getUserUid());
        //     }
            
        //     // 添加其他所需属性的设置
        //     MemberProtobuf member = modelMapper.map(agent.getMember(), MemberProtobuf.class);
        //     response.setMember(member);
            
        //     return response;
        // } catch (Exception e) {
        //     // 记录异常，但返回一个基本的响应对象
        //     // 这样可以确保即使有映射错误，也能返回部分数据
        //     AgentResponse response = new AgentResponse();
        //     response.setUid(agent.getUid());
        //     response.setOrgUid(agent.getOrgUid());
        //     response.setNickname(agent.getNickname());
        //     // 设置其他不依赖懒加载的基本属性
        //     return response;
        // }
    }

    public static UserProtobuf convertToUserProtobuf(AgentEntity agent) {
        UserProtobuf userProtobuf = modelMapper.map(agent, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.AGENT.name());
        return userProtobuf;
    }

    public static UserProtobuf convertToUserProtobuf(WorkgroupEntity workgroup) {
        UserProtobuf userProtobuf = modelMapper.map(workgroup, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.WORKGROUP.name());
        return userProtobuf;
    }

    public static String convertToUserProtobufJSONString(AgentEntity agent) {
        UserProtobuf userProtobuf = convertToUserProtobuf(agent);
        return JSON.toJSONString(userProtobuf);
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
