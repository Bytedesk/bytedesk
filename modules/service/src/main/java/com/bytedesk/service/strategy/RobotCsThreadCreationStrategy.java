/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-19 14:19:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.strategy;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadStateEnum;
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
@Component("robotCsThreadStrategy")
@AllArgsConstructor
public class RobotCsThreadCreationStrategy implements CsThreadCreationStrategy {

    private final RobotRestService robotService;

    private final ThreadRestService threadService;

    private final VisitorThreadService visitorThreadService;

    // private final RouteService routeService;
    private final QueueService queueService;

    private final QueueMemberRestService queueMemberRestService;;

    // private final MessageRestService messageRestService;

    // private final WorkgroupRoutingService workgroupRoutingService;

    private final RobotRestService robotRestService;

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
        if (threadOptional.isPresent() && threadOptional.get().isStarted()) {
            thread = threadOptional.get();
            // 
            thread = visitorThreadService.reInitRobotThreadExtra(thread, robot); // 方便测试
            // 返回未关闭，或 非留言状态的会话
            log.info("Already have a processing robot thread {}", topic);

            return getRobotContinueMessage(robot, thread);
        } else {
            thread = visitorThreadService.createRobotThread(visitorRequest, robot, topic);
            thread = visitorThreadService.reInitRobotThreadExtra(thread, robot);
        }

        // 排队计数
        QueueMemberEntity queueMemberEntity = queueService.enqueueRobot(thread, robot, visitorRequest);
        log.info("routeRobot Enqueued to queue {}", queueMemberEntity.getQueueNickname());

        // 更新线程状态
        thread.setState(ThreadStateEnum.ROBOT.name());
        thread.setAgent(ConvertAiUtils.convertToRobotProtobufString(robot));
        thread.setContent(robot.getServiceSettings().getWelcomeTip());
        thread.setRobot(true);
        thread.setUnreadCount(0);
        // ThreadEntity savedThread =
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

        // return routeService.routeToRobot(visitorRequest, thread, robot);
        // return routeToRobot(visitorRequest, thread, robot);
    }

    // @Transactional
    // @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 200))
    // public MessageProtobuf routeToRobot(VisitorRequest request, @Nonnull ThreadEntity threadFromRequest,
    //         @Nonnull RobotEntity robot) {
    //     try {
    //         Assert.notNull(request, "VisitorRequest must not be null");
    //         Assert.notNull(threadFromRequest, "ThreadEntity must not be null");
    //         Assert.notNull(robot, "RobotEntity must not be null");
    //         Assert.hasText(threadFromRequest.getUid(), "Thread UID must not be empty");

    //         // 直接使用threadFromRequest，修改保存报错，所以重新查询，待完善
    //         Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
    //         Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");

    //         ThreadEntity thread = threadOptional.get();
    //         // 排队计数
    //         QueueMemberEntity queueMemberEntity = queueService.enqueueRobot(thread, robot, request);
    //         log.info("routeRobot Enqueued to queue {}", queueMemberEntity.getQueueNickname());

    //         // 更新线程状态
    //         thread.setState(ThreadStateEnum.ROBOT.name());
    //         thread.setAgent(ConvertAiUtils.convertToRobotProtobufString(robot));
    //         thread.setContent(robot.getServiceSettings().getWelcomeTip());
    //         thread.setRobot(true);
    //         thread.setUnreadCount(0);
    //         // ThreadEntity savedThread =
    //         threadService.save(thread);

    //         // 增加接待数量
    //         robot.increaseThreadCount();
    //         robotRestService.save(robot);

    //         // 更新排队状态
    //         queueMemberEntity.setStatus(QueueMemberStatusEnum.SERVING.name());
    //         queueMemberEntity.setAcceptTime(LocalDateTime.now());
    //         queueMemberEntity.setAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
    //         queueMemberRestService.save(queueMemberEntity);

    //         return ThreadMessageUtil.getThreadRobotWelcomeMessage(robot, thread);
    //     } catch (ObjectOptimisticLockingFailureException e) {
    //         log.warn("Optimistic locking failure while routing to robot, retrying...", e);
    //         throw e;
    //     } catch (Exception e) {
    //         log.error("Error while routing to robot: {}", e.getMessage(), e);
    //         throw new RuntimeException("Failed to route to robot", e);
    //     }
    // }

    private MessageProtobuf getRobotContinueMessage(RobotEntity robot, @Nonnull ThreadEntity thread) {
        //
        // UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // log.info("getRobotContinueMessage user: {}, agent {}", user.toString(),
        // thread.getAgent());
        //
        return ThreadMessageUtil.getThreadRobotWelcomeMessage(robot, thread);
    }

}
