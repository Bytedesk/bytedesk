/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-04 11:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-07 10:24:56
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
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.service.agent.Agent;
import com.bytedesk.service.agent.AgentResponse;
import com.bytedesk.kbase.service_settings.ServiceSettingsResponseVisitor;
import com.bytedesk.service.settings.ServiceSettings;
import com.bytedesk.service.visitor.Visitor;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor.VisitorResponse;
import com.bytedesk.service.workgroup.Workgroup;
import com.bytedesk.service.workgroup.WorkgroupResponse;

public class ConvertServiceUtils {
    private ConvertServiceUtils() {
    }

    //
    public static VisitorResponse convertToVisitorResponse(Visitor visitor) {
        return new ModelMapper().map(visitor, VisitorResponse.class);
    }

    public static UserProtobuf convertToUserProtobuf(Visitor visitor) {
        return new ModelMapper().map(visitor, UserProtobuf.class);
    }

    public static UserProtobuf convertToUserProtobuf(VisitorRequest visitorRequest) {
        UserProtobuf userProtobuf = new ModelMapper().map(visitorRequest, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.VISITOR.name());
        return userProtobuf;
    }

    // public static UserProtobuf convertToUserResponseSimple(AgentResponseSimple
    // agentResponseSimple) {
    // return new ModelMapper().map(agentResponseSimple, UserProtobuf.class);
    // }

    public static UserProtobuf convertToUserResponseSimple(UserProtobuf visitorResponseSimple) {
        return new ModelMapper().map(visitorResponseSimple, UserProtobuf.class);
    }

    public static MessageProtobuf convertToMessageProtobuf(Message lastMessage, Thread thread) {
        //
        MessageProtobuf messageProtobuf = new ModelMapper().map(lastMessage, MessageProtobuf.class);
        messageProtobuf.setThread(ConvertUtils.convertToThreadProtobuf(thread));
        //
        UserProtobuf user = JSON.parseObject(lastMessage.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BdConstants.EMPTY_JSON_STRING);
        }
        messageProtobuf.setUser(user);

        return messageProtobuf;
    }

    public static MessageResponse convertToMessageResponse(Message lastMessage, Thread thread) {
        //
        MessageResponse messageResponse = new ModelMapper().map(lastMessage, MessageResponse.class);
        // messageResponse.setThread(ConvertUtils.convertToThreadProtobuf(thread));
        //
        UserProtobuf user = JSON.parseObject(lastMessage.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BdConstants.EMPTY_JSON_STRING);
        }
        messageResponse.setUser(user);

        return messageResponse;
    }

    //
    public static AgentResponse convertToAgentResponse(Agent agent) {
        return new ModelMapper().map(agent, AgentResponse.class);
    }

    public static UserProtobuf convertToUserProtobuf(Agent agent) {
        UserProtobuf userProtobuf = new ModelMapper().map(agent, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.AGENT.name());
        return userProtobuf;
    }

    //
    public static WorkgroupResponse convertToWorkgroupResponse(Workgroup workgroup) {
        return new ModelMapper().map(workgroup, WorkgroupResponse.class);
    }

    // public static WorkgroupResponseSimple
    // convertToWorkgroupResponseSimple(Workgroup workgroup) {
    // return new ModelMapper().map(workgroup, WorkgroupResponseSimple.class);
    // }

    //
    public static ServiceSettingsResponseVisitor convertToServiceSettingsResponseVisitor(
            ServiceSettings serviceSettings) {
        return new ModelMapper().map(serviceSettings, ServiceSettingsResponseVisitor.class);
    }

}
