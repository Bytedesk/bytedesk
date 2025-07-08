/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-04 11:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-23 10:21:59
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
import com.bytedesk.service.message_leave.MessageLeaveEntity;
import com.bytedesk.service.message_leave.MessageLeaveResponse;
import com.bytedesk.service.message_unread.MessageUnreadEntity;
import com.bytedesk.service.queue.QueueEntity;
import com.bytedesk.service.queue.QueueResponse;
import com.bytedesk.service.visitor.VisitorEntity;
import com.bytedesk.service.visitor.VisitorProtobuf;
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

    public static VisitorProtobuf convertToVisitorProtobuf(VisitorEntity visitor) {
        return modelMapper.map(visitor, VisitorProtobuf.class);
    }

    public static VisitorProtobuf convertToVisitorProtobuf(VisitorResponse visitor) {
        VisitorProtobuf userProtobuf = modelMapper.map(visitor, VisitorProtobuf.class);
        userProtobuf.setType(UserTypeEnum.VISITOR.name());
        return userProtobuf;
    }

    public static VisitorProtobuf convertToVisitorProtobuf(VisitorRequest visitorRequest) {
        VisitorProtobuf userProtobuf = modelMapper.map(visitorRequest, VisitorProtobuf.class);
        userProtobuf.setType(UserTypeEnum.VISITOR.name());
        return userProtobuf;
    }

    public static String convertToVisitorProtobufJSONString(VisitorRequest visitorRequest) {
        VisitorProtobuf userProtobuf = convertToVisitorProtobuf(visitorRequest);
        return userProtobuf.toJson();
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

    public static MessageResponse convertToMessageResponse(MessageEntity lastMessage) {
        //
        MessageResponse messageResponse = modelMapper.map(lastMessage, MessageResponse.class);
        //
        UserProtobuf user = JSON.parseObject(lastMessage.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }
        messageResponse.setUser(user);

        return messageResponse;
    }

    public static MessageResponse convertToMessageResponse(MessageUnreadEntity message) {
        MessageResponse messageResponse = modelMapper.map(message, MessageResponse.class);

        UserProtobuf user = JSON.parseObject(message.getUser(), UserProtobuf.class);
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

    public static UserProtobuf convertToUserProtobuf(WorkgroupEntity workgroup) {
        UserProtobuf userProtobuf = modelMapper.map(workgroup, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.WORKGROUP.name());
        return userProtobuf;
    }

    public static String convertToUserProtobufJSONString(WorkgroupEntity workgroup) {
        UserProtobuf userProtobuf = convertToUserProtobuf(workgroup);
        return userProtobuf.toJson();
    }

    public static String convertToUserProtobufJSONString(AgentEntity agent) {
        UserProtobuf userProtobuf = convertToUserProtobuf(agent);
        return userProtobuf.toJson();
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

    public static String convertToServiceSettingsResponseVisitorJSONString( ServiceSettings serviceSettings) {
        return JSON.toJSONString(convertToServiceSettingsResponseVisitor(serviceSettings));
    }

    public static QueueResponse convertToQueueResponse(QueueEntity entity) {
        return modelMapper.map(entity, QueueResponse.class);
    }
    
    public static MessageLeaveResponse convertToMessageLeaveResponse(MessageLeaveEntity entity) {
        MessageLeaveResponse messageLeaveResponse = modelMapper.map(entity, MessageLeaveResponse.class);
        
        // 设置用户信息
        if (entity.getUser() != null) {
            messageLeaveResponse.setUser(UserProtobuf.fromJson(entity.getUser()));
        }
        
        // 设置回复用户信息
        if (entity.getReplyUser() != null) {
            messageLeaveResponse.setReplyUser(UserProtobuf.fromJson(entity.getReplyUser()));
        }
        
        // 设置已读用户信息
        if (entity.getReadUser() != null) {
            messageLeaveResponse.setReadUser(UserProtobuf.fromJson(entity.getReadUser()));
        }
        
        // 设置转接用户信息
        if (entity.getTransferUser() != null) {
            messageLeaveResponse.setTransferUser(UserProtobuf.fromJson(entity.getTransferUser()));
        }
        
        // 设置关闭用户信息
        if (entity.getCloseUser() != null) {
            messageLeaveResponse.setCloseUser(UserProtobuf.fromJson(entity.getCloseUser()));
        }
        
        // 设置标记垃圾留言用户信息
        if (entity.getSpamUser() != null) {
            messageLeaveResponse.setSpamUser(UserProtobuf.fromJson(entity.getSpamUser()));
        }
        
        return messageLeaveResponse;
    }
    

}
