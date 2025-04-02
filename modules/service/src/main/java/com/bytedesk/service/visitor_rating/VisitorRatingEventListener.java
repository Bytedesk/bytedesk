/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-16 13:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 17:21:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_rating;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.message.event.MessageUpdateEvent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.thread.event.ThreadCloseEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class VisitorRatingEventListener {

    // private final VisitorRatingRestService rateService;

    private final WorkgroupRestService workgroupRestService;

    private final AgentRestService agentRestService;

    private final RobotRestService robotRestService;

    private final IMessageSendService messageSendService;

    @EventListener
    public void onThreadCloseEvent(ThreadCloseEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("ThreadRating onThreadCloseEvent: {}", thread.getUid());
        String topic = thread.getTopic();
        // 会话关闭，判断是否需要自动发送满意度邀请
        if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
            String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(topic);
            Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
            if (workgroupOptional.isPresent()) {
                WorkgroupEntity workgroup = workgroupOptional.get();
                if (workgroup.getServiceSettings().isAutoInviteRate()) {
                    String content = workgroup.getServiceSettings().getInviteRateTip(); //"请对本次服务进行评价";
                    // 发送满意度邀请
                    MessageProtobuf message = MessageUtils.createRateInviteMessage(thread, content);
                    messageSendService.sendProtobufMessage(message);
                }
            }
        } else if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
            String agentUid = TopicUtils.getAgentUidFromThreadTopic(topic);
            Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);
            if (agentOptional.isPresent()) {
                AgentEntity agent = agentOptional.get();
                if (agent.getServiceSettings().isAutoInviteRate()) {
                    String content = agent.getServiceSettings().getInviteRateTip(); //"请对本次服务进行评价";
                    // 发送满意度邀请
                    MessageProtobuf message = MessageUtils.createRateInviteMessage(thread, content);
                    messageSendService.sendProtobufMessage(message);
                }
            }
        } else if (thread.getType().equals(ThreadTypeEnum.ROBOT.name())) {
            String robotUid = TopicUtils.getRobotUidFromThreadTopic(topic);
            Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);
            if (robotOptional.isPresent()) {
                RobotEntity robot = robotOptional.get();
                if (robot.getServiceSettings().isAutoInviteRate()) {
                    String content = robot.getServiceSettings().getInviteRateTip(); //"请对本次服务进行评价";
                    // 发送满意度邀请
                    MessageProtobuf message = MessageUtils.createRateInviteMessage(thread, content);
                    messageSendService.sendProtobufMessage(message);
                }
            }
        }
    }

    @EventListener
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        MessageEntity message = event.getMessage();
        // 判断消息状态，生成评价记录
        if (message.getStatus().equals(MessageStatusEnum.RATE_SUBMIT.name())) {
            log.info("message rate update event: {}", message);
            //
            // ThreadRatingMessageExtra extra = JSON.parseObject(message.getContent(), ThreadRatingMessageExtra.class);
            // //
            // ThreadRatingRequest request = ThreadRatingRequest.builder()
            //         .score(extra.getScore())
            //         .comment(extra.getContent())
            //         .topic(message.getTopic())
            //         .threadUid(message.getThreadUid())
            //         .user(message.getUser())
            //         .orgUid(extra.getOrgUid())
            //         .build();
            // rateService.create(request);
        }
    }

}
