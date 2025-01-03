/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-03 12:05:13
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

import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.routing.RouteService;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor_thread.VisitorThreadService;

import jakarta.annotation.Nonnull;

import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// 机器人对话策略器人
@Slf4j
@Component("robotCsThreadStrategy")
@AllArgsConstructor
public class RobotCsThreadCreationStrategy implements CsThreadCreationStrategy {

    private final RobotRestService robotService;

    private final ThreadRestService threadService;

    private final VisitorThreadService visitorThreadService;

    private final RouteService routeService;

    @Override
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        return createRobotCsThread(visitorRequest);
    }

    // 机器人对话，不支持转人工
    public MessageProtobuf createRobotCsThread(VisitorRequest visitorRequest) {
        String robotUid = visitorRequest.getSid();
        RobotEntity robot = robotService.findByUid(robotUid)
                .orElseThrow(() -> new RuntimeException("Robot uid " + robotUid + " not found"));
        //  
        String topic = TopicUtils.formatOrgRobotThreadTopic(robot.getUid(), visitorRequest.getUid());
        // 
        ThreadEntity thread = null;
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            thread = threadOptional.get();
            // 
            if (thread.isStarted()) {
                thread = visitorThreadService.reInitRobotThreadExtra(thread, robot); // 方便测试
                // 返回未关闭，或 非留言状态的会话
                log.info("Already have a processing robot thread {}", topic);
                return getRobotContinueMessage(visitorRequest, thread);
            } else {
                // 重新初始化
                thread = threadOptional.get().reInitRobot();
            }
        } else {
            thread = visitorThreadService.createRobotThread(visitorRequest, robot, topic);
        }
        thread = visitorThreadService.reInitRobotThreadExtra(thread, robot);

        return routeService.routeToRobot(visitorRequest, thread, robot);
    }

    private MessageProtobuf getRobotContinueMessage(VisitorRequest visitorRequest, @Nonnull ThreadEntity thread) {
        //
        // UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // log.info("getRobotContinueMessage user: {}, agent {}", user.toString(), thread.getAgent());
        // 
        return ThreadMessageUtil.getThreadRobotWelcomeMessage(thread);
    }

}
