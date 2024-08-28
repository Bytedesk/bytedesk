/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-12 07:17:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-19 16:51:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.ai.zhipuai.ZhipuaiService;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageJsonEvent;
import com.bytedesk.core.message.MessageProtoEvent;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.organization.Organization;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.socket.protobuf.model.MessageProto;
import com.bytedesk.core.thread.ThreadCreateEvent;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.MessageConvertUtils;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RobotEventListener {

    private final RobotService robotService;

    private final ZhipuaiService zhipuaiService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    private final UidUtils uidUtils;

    private final ThreadService threadService;

    @Order(5)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        Organization organization = (Organization) event.getSource();
        // User user = organization.getUser();
        String orgUid = organization.getUid();
        log.info("robot - organization created: {}", organization.getName());
        //
        List<String> faqUids = Arrays.asList(
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_1,
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_2);
        //
        // List<String> quickButtonUids = Arrays.asList(
        // orgUid + I18Consts.I18N_QUICK_BUTTON_DEMO_TITLE_1,
        // orgUid + I18Consts.I18N_QUICK_BUTTON_DEMO_TITLE_2);
        //
        RobotRequest robotRequest = RobotRequest.builder()
                .nickname(I18Consts.I18N_ROBOT_NICKNAME)
                .description(I18Consts.I18N_ROBOT_DESCRIPTION)
                // .kb(kb)
                // .llm(llm)
                .build();
        robotRequest.setType(RobotTypeEnum.SERVICE.name());
        robotRequest.setOrgUid(orgUid);
        //
        robotRequest.getServiceSettings().setFaqUids(faqUids);
        robotRequest.getServiceSettings().setQuickFaqUids(faqUids);
        //
        robotService.create(robotRequest);
    }

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        // log.info("MessageJsonEvent {}", event.getJson());
        String messageJson = event.getJson();
        //
        processMessage(messageJson);
    }

    @EventListener
    public void onMessageProtoEvent(MessageProtoEvent event) {
        // log.info("MessageProtoEvent");
        try {
            MessageProto.Message messageProto = MessageProto.Message.parseFrom(event.getMessageBytes());
            //
            try {
                String messageJson = MessageConvertUtils.toJson(messageProto);
                //
                processMessage(messageJson);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(String messageJson) {
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        MessageTypeEnum messageType = messageProtobuf.getType();
        String query = messageProtobuf.getContent();
        log.info("robot processMessage {}", query);
        //
        ThreadProtobuf thread = messageProtobuf.getThread();
        if (thread == null) {
            throw new RuntimeException("thread is null");
        }
        // 仅针对文本类型自动回复
        if (!messageType.equals(MessageTypeEnum.TEXT)) {
            return;
        }
        //
        String threadTopic = thread.getTopic();
        log.info("robot threadTopic {}, thread.type {}", threadTopic, thread.getType());
        if (thread.getType().equals(ThreadTypeEnum.ROBOT)) {
            // 机器人回复
            log.info("robot thread reply");
            // 机器人客服消息 org/robot/default_robot_uid/1420995827073219
            String[] splits = threadTopic.split("/");
            if (splits.length < 4) {
                throw new RuntimeException("robot topic format error");
            }
            String robotUid = splits[2];
            if (!StringUtils.hasText(robotUid)) {
                throw new RuntimeException("robotUid is null");
            }
            Optional<Robot> robotOptional = robotService.findByUid(robotUid);
            if (robotOptional.isPresent()) {
                Robot robot = robotOptional.get();
                //
                UserProtobuf user = UserProtobuf.builder().build();
                user.setUid(robotUid);
                user.setNickname(robot.getNickname());
                user.setAvatar(robot.getAvatar());
                //
                MessageExtra extra = MessageExtra.builder()
                        // .isAutoReply(true)
                        // .autoReplyType(autoReplySettings.getAutoReplyType().name())
                        .orgUid(robot.getOrgUid())
                        .build();
                //
                MessageProtobuf message = MessageProtobuf.builder()
                        .uid(uidUtils.getCacheSerialUid())
                        .status(MessageStatusEnum.SUCCESS)
                        .thread(thread)
                        .user(user)
                        .client(ClientEnum.SYSTEM_AUTO)
                        .extra(JSONObject.toJSONString(extra))
                        .createdAt(new Date())
                        .build();
                // 返回一个输入中消息，让访客端显示输入中
                MessageProtobuf clonedMessage = SerializationUtils.clone(message);
                clonedMessage.setUid(uidUtils.getCacheSerialUid());
                clonedMessage.setType(MessageTypeEnum.PROCESSING);
                String json = JSON.toJSONString(clonedMessage);
                bytedeskEventPublisher.publishMessageJsonEvent(json);
                // 绑定知识库
                zhipuaiService.sendWsRobotMessage(query, robot.getKbUid(), robot, message);
            } else {
                log.error("robot not found");
            }
        }

    }

    // 
    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        Thread thread = event.getThread();
        log.info("robot ThreadCreateEvent: {}", thread.getUid());
        //
        if (thread.getType().equals(ThreadTypeEnum.ROBOT)
            && thread.getAgent().equals(BdConstants.EMPTY_JSON_STRING)) {
            // 机器人会话：org/robot/{robot_uid}/{visitor_uid}
            String topic = thread.getTopic();
            //
            String[] splits = topic.split("/");
            if (splits.length < 4) {
                throw new RuntimeException("robot topic format error");
            }
            String robotUid = splits[2];
            Optional<Robot> robotOptional = robotService.findByUid(robotUid);
            if (robotOptional.isPresent()) {
                Robot robot = robotOptional.get();
                // 更新机器人配置+大模型相关信息
                thread.setExtra(JSON.toJSONString(ConvertAiUtils.convertToServiceSettingsResponseVisitor(
                        robot.getServiceSettings())));
                thread.setAgent(JSON.toJSONString(ConvertAiUtils.convertToRobotProtobuf(robot)));
                // 
                threadService.save(thread);
            }
        }

    }

}
