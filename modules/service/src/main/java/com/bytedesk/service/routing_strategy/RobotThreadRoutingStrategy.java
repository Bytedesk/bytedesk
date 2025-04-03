/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-03 13:50:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing_strategy;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadStatusEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.queue_member.QueueMemberAcceptTypeEnum;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.queue_member.QueueMemberStatusEnum;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor_thread.VisitorThreadService;
import jakarta.annotation.Nonnull;

import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 机器人对话策略器人
@Slf4j
@Component("robotThreadStrategy")
@AllArgsConstructor
public class RobotThreadRoutingStrategy implements ThreadRoutingStrategy {

    private final RobotRestService robotService;

    private final ThreadRestService threadService;

    private final VisitorThreadService visitorThreadService;

    private final QueueService queueService;

    private final QueueMemberRestService queueMemberRestService;;

    private final RobotRestService robotRestService;

    @Override
    public MessageProtobuf createThread(VisitorRequest visitorRequest) {
        return createRobotThread(visitorRequest);
    }

    // 机器人对话，不支持转人工
    public MessageProtobuf createRobotThread(VisitorRequest request) {
        String robotUid = request.getSid();
        RobotEntity robot = robotService.findByUid(robotUid)
                .orElseThrow(() -> new RuntimeException("Robot uid " + robotUid + " not found"));
        //
        String topic = TopicUtils.formatOrgRobotThreadTopic(robot.getUid(), request.getUid());
        //
        ThreadEntity thread = null;
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            // 
            if (threadOptional.get().isNew()) {
                thread = threadOptional.get();
            } else if (threadOptional.get().isRoboting()) {
                thread = threadOptional.get();
                // 
                thread = visitorThreadService.reInitRobotThreadExtra(thread, robot); // 方便测试
                // 返回未关闭，或 非留言状态的会话
                log.info("Already have a processing robot thread {}", topic);
                return getRobotContinueMessage(robot, thread);
            }
        }

        // 如果会话不存在，或者会话已经关闭，则创建新的会话
        if (thread == null) {
            thread = visitorThreadService.createRobotThread(request, robot, topic);
        }

        // 排队计数
        QueueMemberEntity queueMemberEntity = queueService.enqueueRobot(thread, robot, request);
        log.info("routeRobot Enqueued to queue {}", queueMemberEntity.getUid());

        // 更新线程状态
        thread.setStatus(ThreadStatusEnum.ROBOTING.name());
        thread.setAgent(ConvertAiUtils.convertToRobotProtobufString(robot));
        thread.setContent(robot.getServiceSettings().getWelcomeTip());
        thread.setUnreadCount(0);
        threadService.save(thread);

        // 增加接待数量
        robot.increaseThreadCount();
        robotRestService.save(robot);

        // 更新排队状态
        queueMemberEntity.setStatus(QueueMemberStatusEnum.SERVING.name());
        queueMemberEntity.setAcceptTime(LocalDateTime.now());
        queueMemberEntity.setAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
        queueMemberRestService.save(queueMemberEntity);

        return ThreadMessageUtil.getThreadRobotWelcomeMessage(robot, thread);
    }

    private MessageProtobuf getRobotContinueMessage(RobotEntity robot, @Nonnull ThreadEntity thread) {
        //
        // UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // log.info("getRobotContinueMessage user: {}, agent {}", user.toString(),
        // thread.getAgent());
        //
        return ThreadMessageUtil.getThreadRobotWelcomeMessage(robot, thread);
    }

}
