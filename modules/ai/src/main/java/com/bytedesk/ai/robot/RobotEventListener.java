/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-12 07:17:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-29 22:32:13
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

import java.util.Date;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.provider.ollama.OllamaService;
import com.bytedesk.ai.provider.zhipuai.ZhipuaiService;
import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageCache;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageJsonEvent;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.redis.pubsub.RedisPubsubService;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RobotEventListener {

    private final RobotService robotService;

    private final ZhipuaiService zhipuaiService;

    private final OllamaService ollamaService;

    private final UidUtils uidUtils;

    private final ThreadService threadService;

    private final RedisPubsubService redisPubsubService;

    private final BytedeskProperties bytedeskProperties;

    private final MessageCache messageCache;

    private final IMessageSendService messageSendService;

    @Order(5)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("robot - organization created: {}", organization.getName());
        //
        robotService.createDefaultRobot(orgUid, uidUtils.getUid());
        robotService.createDefaultAgentAssistantRobot(orgUid, uidUtils.getUid());
    }

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        // log.info("MessageJsonEvent {}", event.getJson());
        String messageJson = event.getJson();
        //
        processMessage(messageJson);
    }

    // @EventListener
    // public void onMessageProtoEvent(MessageProtoEvent event) {
    //     // log.info("MessageProtoEvent");
    //     try {
    //         MessageProto.Message messageProto = MessageProto.Message.parseFrom(event.getMessageBytes());
    //         //
    //         try {
    //             String messageJson = MessageConvertUtils.toJson(messageProto);
    //             //
    //             processMessage(messageJson);

    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //     } catch (InvalidProtocolBufferException e) {
    //         e.printStackTrace();
    //     }
    // }

    private void processMessage(String messageJson) {
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        MessageTypeEnum messageType = messageProtobuf.getType();
        if (messageType.equals(MessageTypeEnum.STREAM)) {
            // ai回答暂不处理
            return;
        }
        String query = messageProtobuf.getContent();
        log.info("robot processMessage {}", query);
        //
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        if (threadProtobuf == null) {
            throw new RuntimeException("thread is null");
        }
        // 仅针对文本类型自动回复
        if (!messageType.equals(MessageTypeEnum.TEXT)) {
            return;
        }
        //
        String threadTopic = threadProtobuf.getTopic();
        if (threadProtobuf.getType().equals(ThreadTypeEnum.KB)) {
            // 知识库对话
            log.info("robot kb threadTopic {}, thread.type {}", threadTopic, threadProtobuf.getType());
            // 机器人客服消息 org/robot/df_robot_uid/1420995827073219
            String[] splits = threadTopic.split("/");
            if (splits.length < 4) {
                throw new RuntimeException("robot topic format error");
            }
            String robotUid = splits[2];
            if (!StringUtils.hasText(robotUid)) {
                throw new RuntimeException("robotUid is null");
            }
            Optional<RobotEntity> robotOptional = robotService.findByUid(robotUid);
            if (robotOptional.isPresent()) {
                RobotEntity robot = robotOptional.get();
                //
                UserProtobuf user = UserProtobuf.builder().build();
                user.setUid(robotUid);
                user.setNickname(robot.getNickname());
                user.setAvatar(robot.getAvatar());
                //
                sendRobotReply(threadProtobuf, user, query, robot);
            } else {
                log.error("robot not found");
            }
        } else if (threadProtobuf.getType().equals(ThreadTypeEnum.LLM)) {
            log.info("robot llm threadTopic {}, thread.type {}", threadTopic, threadProtobuf.getType());
            // 大模型对话，无知识库
            Optional<ThreadEntity> threadOptional = threadService.findByTopic(threadTopic);
            if (!threadOptional.isPresent()) {
                throw new RuntimeException("thread with topic " + threadTopic + " not found");
            }
            ThreadEntity thread = threadOptional.get();
            MessageExtra extraObject = JSONObject.parseObject(messageProtobuf.getExtra(), MessageExtra.class);
            // 
            String agent = thread.getAgent();
            RobotProtobuf robotProtobuf = JSON.parseObject(agent, RobotProtobuf.class);
            UserProtobuf user = UserProtobuf.builder().build();
            user.setUid(robotProtobuf.getUid());
            user.setNickname(robotProtobuf.getNickname());
            user.setAvatar(robotProtobuf.getAvatar());
            //
            String messageUid = uidUtils.getCacheSerialUid();
            MessageProtobuf message = MessageProtobuf.builder()
                    .uid(messageUid)
                    .status(MessageStatusEnum.SUCCESS)
                    .thread(threadProtobuf)
                    .user(user)
                    .client(ClientEnum.SYSTEM_AUTO)
                    .extra(JSONObject.toJSONString(extraObject))
                    .createdAt(new Date())
                    .build();
            // 返回一个输入中消息，让访客端显示输入中
            MessageProtobuf clonedMessage = SerializationUtils.clone(message);
            clonedMessage.setUid(uidUtils.getCacheSerialUid());
            clonedMessage.setType(MessageTypeEnum.PROCESSING);
            // MessageUtils.notifyUser(clonedMessage);
            messageSendService.sendProtobufMessage(clonedMessage);
            //
            // TODO: 获取大模型配置
            // robotProtobuf.getLlm().getProvider()
            // robotProtobuf.getLlm().getModel()
            // 
            if (robotProtobuf.getLlm().getProvider().equals("ollama")) {
                ollamaService.sendWsMessage(query, robotProtobuf.getLlm(), message);
            } else {
                // 目前所有的模型都使用zhipu
                zhipuaiService.sendWsMessage(query, robotProtobuf.getLlm(), message);
            }

        } else if (threadProtobuf.getType().equals(ThreadTypeEnum.AGENT)
                || threadProtobuf.getType().equals(ThreadTypeEnum.WORKGROUP)
                || threadProtobuf.getType().equals(ThreadTypeEnum.KB)
                || threadProtobuf.getType().equals(ThreadTypeEnum.KBDOC)) {
            log.info("robot agent/workgroup threadTopic {}, thread.type {}", threadTopic, threadProtobuf.getType());
            // TODO: 取消查库
            ThreadEntity thread = threadService.findByTopic(threadTopic)
                    .orElseThrow(() -> new RuntimeException("thread with topic " + threadTopic + " not found"));
            UserProtobuf agent = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
            // 当前会话为机器人接待，而且是访客发送的消息
            if (agent.getType().equals(UserTypeEnum.ROBOT.name())
                    && messageProtobuf.getUser().getType().equals(UserTypeEnum.VISITOR.name())) {
                // 机器人回复
                log.info("robot thread reply");
                RobotEntity robot = robotService.findByUid(agent.getUid())
                        .orElseThrow(() -> new RuntimeException("robot " + agent.getUid() + " not found"));
                //
                sendRobotReply(threadProtobuf, agent, query, robot);
            }
        }
    }

    private void sendRobotReply(ThreadProtobuf threadProtobuf, UserProtobuf user, String query, RobotEntity robot) {
        //
        String threadTopic = threadProtobuf.getTopic();
        MessageExtra extra = MessageUtils.getMessageExtra(robot.getOrgUid());
        //
        String messageUid = uidUtils.getCacheSerialUid();
        MessageProtobuf message = MessageProtobuf.builder()
                .uid(messageUid)
                .status(MessageStatusEnum.SUCCESS)
                .thread(threadProtobuf)
                .user(user)
                .client(ClientEnum.SYSTEM_AUTO)
                .extra(JSONObject.toJSONString(extra))
                .createdAt(new Date())
                .build();

        // 返回一个输入中消息，让访客端显示输入中
        MessageProtobuf clonedMessage = SerializationUtils.clone(message);
        clonedMessage.setUid(uidUtils.getCacheSerialUid());
        clonedMessage.setType(MessageTypeEnum.PROCESSING);
        // MessageUtils.notifyUser(clonedMessage);
        messageSendService.sendProtobufMessage(clonedMessage);
        // 知识库
        if (bytedeskProperties.getJavaAi()) {
            zhipuaiService.sendWsKbMessage(query, robot.getKbUid(), robot, message);
        }
        // 通知python ai模块处理回答
        if (bytedeskProperties.getPythonAi()) {
            messageCache.put(messageUid, message);
            redisPubsubService.sendQuestionMessage(messageUid, threadTopic, robot.getKbUid(),
                    query);
        }
    }

}
