/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-12 07:17:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 18:19:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.kbase.faq.event.FaqCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RobotEventListener {

    
    // private final Optional<ZhipuaiChatService> zhipuaiChatService;
    // private final Optional<OllamaChatService> ollamaChatService;
    // private final Optional<SpringAIDeepseekService> springAIDeepseekService;
    // private final Optional<SpringAIZhipuaiService> springAIZhipuaiService;
    // private final Optional<SpringAIDashscopeService> springAIDashscopeService;
    // private final Optional<SpringAIOllamaService> springAIOllamaService;
    // private final UidUtils uidUtils;
    // private final ThreadRestService threadRestService;
    // private final IMessageSendService messageSendService;
    private final RobotRestService robotRestService;
    // private final RobotFaqProcessor robotFaqProcessor;
    private final RobotService robotService;

    @Order(5)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("robot - organization created: {}", organization.getName());
        String robotUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_ROBOT_UID);
        robotRestService.initDefaultRobot(orgUid, robotUid);
    }

    @EventListener
    public void onFaqCreateEvent(FaqCreateEvent event) {
        // robotFaqProcessor.addFaqToQueue(event.getFaq());
    }

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        robotService.processWebsocketMessage(event.getJson());
    }


    // private void processLlmThreadMessage(String query, String threadTopic, ThreadProtobuf threadProtobuf, MessageProtobuf messageProtobuf) {
    //     log.info("robot llm threadTopic {}, thread.type {}", threadTopic, threadProtobuf.getType());
    //         String[] splits = threadTopic.split("/");
    //         if (splits.length < 4) {
    //             throw new RuntimeException("robot topic format error");
    //         }
    //         String robotUid = splits[2];
    //         if (!StringUtils.hasText(robotUid)) {
    //             throw new RuntimeException("robotUid is null");
    //         }
    //         Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);
    //         if (robotOptional.isPresent()) {
    //             RobotEntity robot = robotOptional.get();
    //             // 
    //             if (robot.getLlm().isEnabled()) {
    //                 if (robot.isKbEnabled()) {
    //                 }
    //             } else if (robot.isKbEnabled()) {
    //             } else {
    //             }
    //             Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(threadTopic);
    //             if (!threadOptional.isPresent()) {
    //                 throw new RuntimeException("thread with topic " + threadTopic + " not found");
    //             }
    //             ThreadEntity thread = threadOptional.get();
    //             // MessageExtra extraObject = JSONObject.parseObject(messageProtobuf.getExtra(), MessageExtra.class);
    //             // String agent = thread.getAgent();
    //             // RobotProtobuf agent = JSON.parseObject(thread.getAgent(), RobotProtobuf.class);
    //             // RobotEntity robot = robotRestService.findByUid(agent.getUid())
    //             //     .orElseThrow(() -> new RuntimeException("robot " + agent.getUid() + " not found"));
    //             // UserProtobuf user = UserProtobuf.builder()
    //             //         .nickname(robotProtobuf.getNickname())
    //             //         .avatar(robotProtobuf.getAvatar())
    //             //         .type(UserTypeEnum.ROBOT.name())
    //             //         .build();
    //             // user.setUid(robotProtobuf.getUid());
    //             // String messageUid = uidUtils.getUid();
    //             // MessageProtobuf message = MessageProtobuf.builder()
    //             //         .uid(messageUid)
    //             //         .status(MessageStatusEnum.SUCCESS)
    //             //         .thread(threadProtobuf)
    //             //         .user(user)
    //             //         .client(ClientEnum.ROBOT)
    //             //         .extra(JSONObject.toJSONString(extraObject))
    //             //         .createdAt(LocalDateTime.now())
    //             //         .build();
    //             MessageProtobuf message = RobotMessageUtils.createRobotMessage(thread, threadProtobuf, robot, messageProtobuf);
    //             // 
    //             MessageProtobuf clonedMessage = SerializationUtils.clone(message);
    //             clonedMessage.setUid(uidUtils.getUid());
    //             clonedMessage.setType(MessageTypeEnum.PROCESSING);
    //             messageSendService.sendProtobufMessage(clonedMessage);
    //             //
    //             if (robot.getLlm().getProvider().equals(LlmProviderConsts.OLLAMA)) {
    //                 springAIOllamaService
    //                         .ifPresent(service -> service.sendWebsocketMessage(query, robot, message));
    //             } else if (robot.getLlm().getProvider().equals(LlmProviderConsts.DEEPSEEK)) {
    //                 springAIDeepseekService
    //                         .ifPresent(service -> service.sendWebsocketMessage(query, robot, message));
    //             } else if (robot.getLlm().getProvider().equals(LlmProviderConsts.DASHSCOPE)) {
    //                 springAIDashscopeService
    //                         .ifPresent(service -> service.sendWebsocketMessage(query, robot, message));
    //             } else if (robot.getLlm().getProvider().equals(LlmProviderConsts.ZHIPU)) {
    //                 springAIZhipuaiService
    //                         .ifPresent(service -> service.sendWebsocketMessage(query, robot, message));
    //             } else {
    //                 // 默认使用智谱AI
    //                 springAIZhipuaiService
    //                         .ifPresent(service -> service.sendWebsocketMessage(query, robot, message));
    //             }
    //         } else {
    //             log.error("robot not found");
    //         }
    // }


}
