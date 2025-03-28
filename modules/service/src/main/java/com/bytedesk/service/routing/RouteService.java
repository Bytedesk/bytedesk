/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 18:59:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-19 15:47:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing;

// import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Primary
@Service
@AllArgsConstructor
public class RouteService {

    // private final ThreadRestService threadService;

    // private final IMessageSendService messageSendService;

    // private final AgentRestService agentRestService;

    // private final QueueService queueService;

    // private final QueueMemberRestService queueMemberRestService;;

    // private final MessageRestService messageRestService;

    // private final WorkgroupRoutingService workgroupRoutingService;

    // private final RobotRestService robotRestService;

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

    // @Transactional
    // @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 200))
    // public MessageProtobuf routeToAgent(VisitorRequest visitorRequest, @Nonnull ThreadEntity threadFromRequest,
    //         @Nonnull AgentEntity agent) {
    //     Assert.notNull(visitorRequest, "VisitorRequest must not be null");
    //     Assert.notNull(threadFromRequest, "ThreadEntity must not be null");
    //     Assert.notNull(agent, "AgentEntity must not be null");
    //     Assert.hasText(threadFromRequest.getUid(), "Thread UID must not be empty");

    //     // 直接使用threadFromRequest，修改保存报错，所以重新查询，待完善
    //     Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
    //     Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");
    //     ThreadEntity thread = threadOptional.get();
    //     //
    //     try {
    //         // 排队计数
    //         QueueMemberEntity queueMemberEntity = queueService.enqueueAgent(thread, agent, visitorRequest);
    //         log.info("routeAgent Enqueued to queue {}", queueMemberEntity.getQueueNickname());
    //         // 判断客服是否在线且接待状态
    //         if (agent.isConnectedAndAvailable()) {
    //             // 客服在线 且 接待状态
    //             // 判断是否达到最大接待人数，如果达到则进入排队
    //             if (agent.canAcceptMore()) {
    //                 // 未满则接待
    //                 return handleAvailableAgent(thread, agent, queueMemberEntity);
    //             } else {
    //                 return handleQueuedAgent(thread, agent, queueMemberEntity);
    //             }
    //         } else {
    //             return handleOfflineAgent(thread, agent, queueMemberEntity);
    //         }
    //     } catch (ObjectOptimisticLockingFailureException e) {
    //         log.warn("Optimistic locking failure while routing to agent, retrying...", e);
    //         throw e;
    //     } catch (Exception e) {
    //         log.error("Error while routing to agent: {}", e.getMessage(), e);
    //         throw new RuntimeException("Failed to route to agent", e);
    //     }
    // }

    // private MessageProtobuf handleAvailableAgent(ThreadEntity thread, AgentEntity agent,
    //         QueueMemberEntity queueMemberEntity) {
    //     Assert.notNull(thread, "ThreadEntity must not be null");
    //     Assert.notNull(agent, "AgentEntity must not be null");
    //     Assert.notNull(queueMemberEntity, "QueueMemberEntity must not be null");
        
    //     // 未满则接待
    //     thread.setStarted();
    //     thread.setUnreadCount(1);
    //     thread.setContent(agent.getServiceSettings().getWelcomeTip());
    //     thread.setQueueNumber(queueMemberEntity.getQueueNumber());
    //     // 增加接待数量，待优化
    //     agent.increaseThreadCount();
    //     agentRestService.save(agent);
    //     // 更新排队状态，待优化
    //     queueMemberEntity.setStatus(QueueMemberStatusEnum.SERVING.name());
    //     queueMemberEntity.setAcceptTime(LocalDateTime.now());
    //     queueMemberEntity.setAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
    //     queueMemberRestService.save(queueMemberEntity);
    //     //
    //     thread.setRobot(false);
    //     threadService.save(thread);
    //     //
    //     MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(agent, thread);
    //     messageSendService.sendProtobufMessage(messageProtobuf);
    //     return messageProtobuf;
    // }

    // private MessageProtobuf handleQueuedAgent(ThreadEntity thread, AgentEntity agent,
    //         QueueMemberEntity queueMemberEntity) {
    //     // 已满则排队
    //     // String queueTip = agent.getQueueSettings().getQueueTip();
    //     String content = "";
    //     if (queueMemberEntity.getBeforeNumber() == 0) {
    //         // 客服接待刚满员，下一个就是他，
    //         content = "请稍后，下一个就是您";
    //         // String content = String.format(queueTip, queueMemberEntity.getQueueNumber(),
    //         // queueMemberEntity.getWaitTime());
    //     } else {
    //         // 前面有排队人数
    //         content = " 当前排队人数：" + queueMemberEntity.getBeforeNumber() + " 大约等待时间："
    //                 + queueMemberEntity.getBeforeNumber() * 2 + "  分钟";
    //     }
    //     // 进入排队队列
    //     thread.setQueuing();
    //     thread.setUnreadCount(0);
    //     thread.setContent(content);
    //     thread.setQueueNumber(queueMemberEntity.getQueueNumber());
    //     thread.setRobot(false);
    //     threadService.save(thread);
    //     //
    //     MessageProtobuf messageProtobuf = ThreadMessageUtil.getAgentThreadQueueMessage(agent, thread);
    //     messageSendService.sendProtobufMessage(messageProtobuf);
    //     return messageProtobuf;
    // }

    // private MessageProtobuf handleOfflineAgent(ThreadEntity thread, AgentEntity agent,
    //         QueueMemberEntity queueMemberEntity) {
    //     // 客服离线或小休不接待状态，则进入留言
    //     thread.setOffline();
    //     thread.setUnreadCount(0);
    //     thread.setContent(agent.getMessageLeaveSettings().getMessageLeaveTip());
    //     thread.setQueueNumber(queueMemberEntity.getQueueNumber());
    //     threadService.save(thread);
    //     //
    //     MessageEntity message = ThreadMessageUtil.getAgentThreadOfflineMessage(agent, thread);
    //     // 保存留言消息
    //     messageRestService.save(message);
    //     // 返回留言消息
    //     // 部分用户测试的，离线状态收不到消息，以为是bug，其实不是，是离线状态不发送消息。防止此种情况，所以还是推送一下
    //     MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    //     messageSendService.sendProtobufMessage(messageProtobuf);
    //     return messageProtobuf;
    // }

    // public MessageProtobuf routeToWorkgroup(VisitorRequest visitorRequest, String threadTopic,
    //         WorkgroupEntity workgroup) {
    //     Assert.notNull(visitorRequest, "VisitorRequest must not be null");
    //     Assert.hasText(threadTopic, "Thread topic must not be empty");
    //     Assert.notNull(workgroup, "WorkgroupEntity must not be null");
    //     Assert.notEmpty(workgroup.getAgents(), "No agents found in workgroup with uid " + workgroup.getUid());

    //     log.info("routeService routeWorkgroup: {}", workgroup.getUid());
    //     // 直接使用threadFromRequest，修改保存报错，所以重新查询，待完善
    //     Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(threadTopic);
    //     Assert.isTrue(threadOptional.isPresent(), "Thread with topic " + threadTopic + " not found");
        
    //     ThreadEntity thread = threadOptional.get();
    //     // 下面人工接待
    //     AgentEntity agent = workgroupRoutingService.selectAgent(workgroup, thread, workgroup.getAvailableAgents());
    //     if (agent == null) {
    //         return getOfflineMessage(visitorRequest, thread, workgroup);
    //     }
    //     // 排队计数
    //     QueueMemberEntity queueMemberEntity = queueService.enqueueWorkgroup(thread, agent, workgroup, visitorRequest);
    //     log.info("routeAgent Enqueued to queue {}", queueMemberEntity.getQueueNickname());
    //     //
    //     if (agent.isConnectedAndAvailable()) {
    //         // 客服在线 且 接待状态
    //         if (agent.canAcceptMore()) {
    //             // 未满则接待
    //             return handleAvailableWorkgroup(thread, agent, queueMemberEntity);
    //         } else {
    //             // 排队，已满则排队
    //             return handleQueuedWorkgroup(thread, agent, queueMemberEntity);
    //         }
    //     } else {
    //         // 离线状态永远显示离线提示语，不显示"继续会话"
    //         // 客服离线 或 非接待状态
    //         return getOfflineMessage(visitorRequest, thread, workgroup);
    //     }
    // }

    // private MessageProtobuf handleAvailableWorkgroup(ThreadEntity thread, AgentEntity agent,
    //         QueueMemberEntity queueMemberEntity) {
    //     // 未满则接待
    //     thread.setStarted();
    //     thread.setUnreadCount(1);
    //     thread.setContent(agent.getServiceSettings().getWelcomeTip());
    //     thread.setQueueNumber(queueMemberEntity.getQueueNumber());
    //     // 增加接待数量，待优化
    //     agent.increaseThreadCount();
    //     agentRestService.save(agent);
    //     // 更新排队状态，待优化
    //     queueMemberEntity.setStatus(QueueMemberStatusEnum.SERVING.name());
    //     queueMemberEntity.setAcceptTime(LocalDateTime.now());
    //     queueMemberEntity.setAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
    //     queueMemberRestService.save(queueMemberEntity);
    //     //
    //     thread.setOwner(agent.getMember().getUser());
    //     //
    //     UserProtobuf agentProtobuf = ServiceConvertUtils.convertToUserProtobuf(agent);
    //     thread.setAgent(JSON.toJSONString(agentProtobuf));
    //     thread.setRobot(false);
    //     //
    //     threadService.save(thread);
    //     log.info("routeWorkgroup WelcomeMessage: {}", thread.toString());
    //     //
    //     MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(agent, thread);
    //     messageSendService.sendProtobufMessage(messageProtobuf);
    //     return messageProtobuf;
    // }

    // private MessageProtobuf handleQueuedWorkgroup(ThreadEntity thread, AgentEntity agent,
    //         QueueMemberEntity queueMemberEntity) {
    //     // 排队，已满则排队
    //     // String queueTip = agent.getQueueSettings().getQueueTip();
    //     String content = "";
    //     if (queueMemberEntity.getBeforeNumber() == 0) {
    //         // 客服接待刚满员，下一个就是他，
    //         content = "请稍后，下一个就是您";
    //         // String content = String.format(queueTip, queueMemberEntity.getQueueNumber(),
    //         // queueMemberEntity.getWaitTime());
    //     } else {
    //         // 前面有排队人数
    //         content = " 当前排队人数：" + queueMemberEntity.getBeforeNumber() + " 大约等待时间："
    //                 + queueMemberEntity.getBeforeNumber() * 2 + "  分钟";
    //     }

    //     // 进入排队队列
    //     thread.setQueuing();
    //     thread.setUnreadCount(0);
    //     thread.setContent(content);
    //     thread.setQueueNumber(queueMemberEntity.getQueueNumber());
    //     thread.setRobot(false);
    //     //
    //     threadService.save(thread);
    //     log.info("routeWorkgroup QueueMessage: {}", thread.toString());
    //     //
    //     MessageProtobuf messageProtobuf = ThreadMessageUtil.getAgentThreadQueueMessage(agent, thread);
    //     messageSendService.sendProtobufMessage(messageProtobuf);
    //     return messageProtobuf;
    // }

    // public MessageProtobuf getOfflineMessage(VisitorRequest visitorRequest, ThreadEntity thread,
    //         WorkgroupEntity workgroup) {
    //     thread.setOffline();
    //     thread.setContent(workgroup.getMessageLeaveSettings().getMessageLeaveTip());
    //     threadService.save(thread);
    //     //
    //     MessageEntity message = ThreadMessageUtil.getThreadOfflineMessage(workgroup, thread);
    //     // 保存留言消息
    //     messageRestService.save(message);
    //     // 返回留言消息
    //     // 部分用户测试的，离线状态收不到消息，以为是bug，其实不是，是离线状态不发送消息。防止此种情况，所以还是推送一下
    //     MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    //     messageSendService.sendProtobufMessage(messageProtobuf);
    //     return messageProtobuf;
    // }

}
