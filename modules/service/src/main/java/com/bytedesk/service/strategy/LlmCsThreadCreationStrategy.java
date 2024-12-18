/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-26 14:02:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.strategy;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;

// 机器人-对话大模型
@Component("llmCsThreadStrategy")
@AllArgsConstructor
public class LlmCsThreadCreationStrategy implements CsThreadCreationStrategy {

    private final RobotRestService robotService;

    private final ThreadRestService threadService;

    private final UidUtils uidUtils;

    @Override
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        return createLlmCsThread(visitorRequest);
    }

    public MessageProtobuf createLlmCsThread(VisitorRequest visitorRequest) {
        //
        String robotUid = visitorRequest.getSid();
        RobotEntity robot = robotService.findByUid(robotUid)
                .orElseThrow(() -> new RuntimeException("Robot uid " + robotUid + " not found"));
        //
        ThreadEntity thread = getLlmThread(visitorRequest, robot);
        //
        return getLlmMessage(visitorRequest, thread, robot);
    }

    private ThreadEntity getLlmThread(VisitorRequest visitorRequest, RobotEntity robot) {
        //
        String topic = TopicUtils.formatOrgRobotThreadTopic(robot.getUid(), visitorRequest.getUid());
        // TODO: 到visitor thread表中拉取
        ThreadEntity thread = ThreadEntity.builder().build();
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            thread = threadOptional.get();
        } else {
            //
            thread = ThreadEntity.builder().build();
            thread.setUid(uidUtils.getCacheSerialUid());
            thread.setTopic(topic);
            thread.setType(ThreadTypeEnum.KB.name());
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

    private MessageProtobuf getLlmMessage(VisitorRequest visitorRequest, ThreadEntity thread, RobotEntity robot) {
        //
        thread.setContent(robot.getServiceSettings().getWelcomeTip());
        //
        // boolean isReenter = true;
        // if (thread.getState() == ThreadStateEnum.STARTED.name()) {
        //     isReenter = false;
        // }
        // if thread is closed, reopen it and then create a new message
        // if (thread.isClosed()) {
        //     isReenter = false;
        //     thread.setStatus(ThreadStateEnum.RESTART.name());
        // } else {
        //     thread.setStatus(isReenter ? ThreadStateEnum.CONTINUE.name() : ThreadStateEnum.STARTED.name());
        // }
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
        return ThreadMessageUtil.getThreadWelcomeMessage(user, thread);
    }

}