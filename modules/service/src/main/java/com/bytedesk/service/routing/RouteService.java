/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 18:59:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-26 11:24:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing;

import java.time.LocalDateTime;

// import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.queue_member.QueueMemberAcceptTypeEnum;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.queue_member.QueueMemberStatusEnum;
// import com.bytedesk.service.queue.QueueServiceMy;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.service.strategy.ThreadMessageUtil;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.workgroup.WorkgroupEntity;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Primary
@Service
@AllArgsConstructor
public class RouteService {

    private final ThreadRestService threadService;

    private final IMessageSendService messageSendService;

    private final AgentRestService agentRestService;

    private final QueueService queueService;

    private final QueueMemberRestService queueMemberRestService; ;

    public MessageProtobuf routeToRobot(VisitorRequest request, @Nonnull ThreadEntity thread,
            @Nonnull RobotEntity robot) {
        //
        thread.setContent(robot.getServiceSettings().getWelcomeTip());
        // 使用agent的serviceSettings配置
        UserProtobuf user = ConvertAiUtils.convertToUserProtobuf(robot);
        thread.setAgent(JSON.toJSONString(user));
        //
        thread.setRobot(true);
        thread.setUnreadCount(0);
        threadService.save(thread);
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadRobotWelcomeMessage(user, thread);
        return messageProtobuf;
    }

    public MessageProtobuf routeToAgent(VisitorRequest visitorRequest, @Nonnull ThreadEntity thread,
            @Nonnull AgentEntity agent) {
        // log.info("RouteService routeAgent: {}", agent.getUid());
        // 排队计数
        QueueMemberEntity queueMemberEntity = queueService.enqueue(thread, agent, visitorRequest);
        log.info("routeAgent Enqueued to queue {}", queueMemberEntity.toString());
        MessageProtobuf messageProtobuf = null;
        // 判断客服是否在线且接待状态
        if (agent.isConnectedAndAvailable()) {
            // 客服在线 且 接待状态
            // 判断是否达到最大接待人数，如果达到则进入排队
            if (agent.canAcceptMore()) {
                // 未满则接待
                thread.setStarted();
                thread.setUnreadCount(1);
                thread.setContent(agent.getServiceSettings().getWelcomeTip());
                thread.setQueueNumber(queueMemberEntity.getQueueNumber());
                // 增加接待数量，待优化
                agent.increaseThreadCount();
                agentRestService.save(agent);
                // 更新排队状态，待优化
                queueMemberEntity.setStatus(QueueMemberStatusEnum.SERVING.name());
                queueMemberEntity.setAcceptTime(LocalDateTime.now());
                queueMemberEntity.setAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
                queueMemberRestService.save(queueMemberEntity);
                // 
                messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(agent, thread);
                messageSendService.sendProtobufMessage(messageProtobuf);
            } else {
                // 进入排队队列
                thread.setQueuing();
                thread.setUnreadCount(0);
                thread.setContent(agent.getQueueSettings().getQueueTip());
                thread.setQueueNumber(queueMemberEntity.getQueueNumber());
                // 
                messageProtobuf = ThreadMessageUtil.getThreadQueueMessage(agent, thread);
                messageSendService.sendProtobufMessage(messageProtobuf);
            }
            threadService.save(thread);
            //
            return messageProtobuf;
        } else {
            // 客服离线或小休不接待状态，则进入留言
            thread.setOffline();
            thread.setUnreadCount(0);
            thread.setContent(agent.getLeaveMsgSettings().getLeaveMsgTip());
            thread.setQueueNumber(queueMemberEntity.getQueueNumber());
            threadService.save(thread);
            //
            messageProtobuf = ThreadMessageUtil.getThreadOfflineMessage(agent, thread);
            // 客服离线时，不发送消息通知
            return messageProtobuf;
        }
    }

    public MessageProtobuf routeToWorkgroup(VisitorRequest visitorRequest, ThreadEntity thread,
            WorkgroupEntity workgroup) {
        log.info("RouteServiceImplVip routeWorkgroup: {}", workgroup.getUid());
        
        if (workgroup.getAgents().isEmpty()) {
            throw new RuntimeException("No agents found in workgroup with uid " + workgroup.getUid());
        }
        // 下面人工接待
        AgentEntity agent = workgroup.nextAgent();
        if (agent == null) {
            throw new RuntimeException("No available agent found in workgroup with uid " + workgroup.getUid());
        }
        //
        if (agent.isConnectedAndAvailable()) {
            // 客服在线 且 接待状态
            thread.setStarted();
            thread.setUnreadCount(1);
            thread.setContent(workgroup.getServiceSettings().getWelcomeTip());
            //
            thread.setOwner(agent.getMember().getUser());
            UserProtobuf agentProtobuf = ConvertServiceUtils.convertToUserProtobuf(agent);
            thread.setAgent(JSON.toJSONString(agentProtobuf));
            threadService.save(thread);
            //
            MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(agent, thread);
            // 广播消息，由消息通道统一处理
            messageSendService.sendProtobufMessage(messageProtobuf);

            return messageProtobuf;
        } else {
            // 离线状态永远显示离线提示语，不显示“继续会话”
            // 客服离线 或 非接待状态
            thread.setOffline();
            thread.setContent(workgroup.getLeaveMsgSettings().getLeaveMsgTip());
            thread.setOwner(agent.getMember().getUser());
            UserProtobuf agentProtobuf = ConvertServiceUtils.convertToUserProtobuf(agent);
            thread.setAgent(JSON.toJSONString(agentProtobuf));
            threadService.save(thread);
            //
            MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadOfflineMessage(agent, thread);
            // 广播消息，由消息通道统一处理
            messageSendService.sendProtobufMessage(messageProtobuf);
            return messageProtobuf;
        }

    }


}
