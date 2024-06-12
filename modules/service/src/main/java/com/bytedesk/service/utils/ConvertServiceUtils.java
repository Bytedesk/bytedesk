/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-04 11:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-07 14:23:45
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
import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.rbac.user.UserResponseSimple;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.service.agent.Agent;
import com.bytedesk.service.agent.AgentResponse;
import com.bytedesk.service.agent.AgentResponseSimple;
import com.bytedesk.service.visitor.Visitor;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor.VisitorResponse;
import com.bytedesk.service.visitor.VisitorResponseSimple;
import com.bytedesk.service.workgroup.Workgroup;
import com.bytedesk.service.workgroup.WorkgroupResponse;
import com.bytedesk.service.workgroup.WorkgroupResponseSimple;

public class ConvertServiceUtils {
    private ConvertServiceUtils() {
    }

    //
    public static VisitorResponse convertToVisitorResponse(Visitor visitor) {
        return new ModelMapper().map(visitor, VisitorResponse.class);
    }

    public static VisitorResponseSimple convertToVisitorResponseSimple(Visitor visitor) {
        return new ModelMapper().map(visitor, VisitorResponseSimple.class);
    }

    public static VisitorResponseSimple convertToVisitorResponseSimple(VisitorRequest visitorRequest) {
        return new ModelMapper().map(visitorRequest, VisitorResponseSimple.class);
    }

    public static UserResponseSimple convertToUserResponseSimple(AgentResponseSimple agentResponseSimple) {
        return new ModelMapper().map(agentResponseSimple, UserResponseSimple.class);
    }

    public static UserResponseSimple convertToUserResponseSimple(VisitorResponseSimple visitorResponseSimple) {
        return new ModelMapper().map(visitorResponseSimple, UserResponseSimple.class);
    }

    public static MessageResponse convertToMessageResponse(Message lastMessage, Thread thread) {
        //
        MessageResponse messageResponse = new ModelMapper().map(lastMessage, MessageResponse.class);
        messageResponse.setThread(ConvertUtils.convertToThreadResponseSimple(thread));
        //
        UserResponseSimple user = JSON.parseObject(lastMessage.getUser(), UserResponseSimple.class);
        messageResponse.setUser(user);

        return messageResponse;
    }

    //
    public static AgentResponse convertToAgentResponse(Agent agent) {
        return new ModelMapper().map(agent, AgentResponse.class);
    }

    public static AgentResponseSimple convertToAgentResponseSimple(Agent agent) {
        return new ModelMapper().map(agent, AgentResponseSimple.class);
    }

    // 
    public static WorkgroupResponse convertToWorkgroupResponse(Workgroup workgroup) {
        return new ModelMapper().map(workgroup, WorkgroupResponse.class);
    }

    public static WorkgroupResponseSimple convertToWorkgroupResponseSimple(Workgroup workgroup) {
        return new ModelMapper().map(workgroup, WorkgroupResponseSimple.class);
    }

    // 

}
