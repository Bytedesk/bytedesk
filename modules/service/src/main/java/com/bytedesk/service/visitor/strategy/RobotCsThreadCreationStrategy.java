/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-07 22:42:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor.strategy;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.robot.Robot;
import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.thread.ThreadStatusEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.core.thread.Thread;

import lombok.AllArgsConstructor;

// 机器人对话
@Component("robotCsThreadStrategy")
@AllArgsConstructor
public class RobotCsThreadCreationStrategy implements CsThreadCreationStrategy {

    private final RobotService robotService;

    private final ThreadService threadService;

    private final UidUtils uidUtils;

    @Override
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        return createRobotCsThread(visitorRequest);
    }

    public MessageProtobuf createRobotCsThread(VisitorRequest visitorRequest) {
        //
        String robotUid = visitorRequest.getSid();
        Robot robot = robotService.findByUid(robotUid)
                .orElseThrow(() -> new RuntimeException("Robot uid " + robotUid + " not found"));
        //
        Thread thread = getRobotThread(visitorRequest, robot);
        //
        return getRobotMessage(visitorRequest, thread, robot);
    }

    private Thread getRobotThread(VisitorRequest visitorRequest, Robot robot) {
        //
        String topic = TopicUtils.formatOrgRobotThreadTopic(robot.getUid(), visitorRequest.getUid());
        // TODO: 到visitor thread表中拉取
        Thread thread = Thread.builder().build();
        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            thread = threadOptional.get();
        } else {
            //
            thread = Thread.builder().build();
            thread.setUid(uidUtils.getCacheSerialUid());
            thread.setTopic(topic);
            thread.setType(ThreadTypeEnum.ROBOT.name());
            thread.setUnreadCount(0);
            thread.setClient(ClientEnum.fromValue(visitorRequest.getClient()).name());
            thread.setOrgUid(robot.getOrgUid());
            //
            UserProtobuf visitor = ConvertServiceUtils.convertToUserProtobuf(visitorRequest);
            thread.setUser(JSON.toJSONString(visitor));
        }
        // 
        thread.setExtra(JSON.toJSONString(ConvertAiUtils.convertToServiceSettingsResponseVisitor(
                robot.getServiceSettings())));
        //
        UserProtobuf agenProtobuf = ConvertAiUtils.convertToUserProtobuf(robot);
        thread.setAgent(JSON.toJSONString(agenProtobuf));
        //
        return thread;
    }

    private MessageProtobuf getRobotMessage(VisitorRequest visitorRequest, Thread thread, Robot robot) {
        // 
        thread.setContent(robot.getServiceSettings().getWelcomeTip());
        //
        boolean isReenter = true;
        if (thread.getStatus() == ThreadStatusEnum.NORMAL.name()) {
            isReenter = false;
        }
        // if thread is closed, reopen it and then create a new message
        if (thread.isClosed()) {
            isReenter = false;
            thread.setStatus(ThreadStatusEnum.REOPEN.name());
        } else {
            thread.setStatus(isReenter ? ThreadStatusEnum.CONTINUE.name() : ThreadStatusEnum.NORMAL.name());
        }
        threadService.save(thread);
        //
        // UserProtobuf user = modelMapper.map(robot, UserProtobuf.class);
        UserProtobuf user = ConvertAiUtils.convertToUserProtobuf(robot);
        //
        JSONObject userExtra = new JSONObject();
        userExtra.put("llm", robot.getLlm().isEnabled());
        userExtra.put("defaultReply", robot.getDefaultReply());
        user.setExtra(JSON.toJSONString(userExtra));
        //
        return ThreadMessageUtil.getThreadMessage(user, thread, isReenter);
    }

}