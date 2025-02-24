/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 16:17:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 12:40:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AutoReplyEventListener {

    // private final UidUtils uidUtils;

    // private final AgentRestService agentService;

    // private final MessageService messageService;

    // private final KeywordVipService keywordVipService;

    // private final BytedeskEventPublisher bytedeskEventPublisher;

    // private final ZhipuaiChatService zhipuaiChatService;

    // private final IMessageSendService messageSendService;

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        // log.info("MessageJsonEvent {}", event.getJson());
        String messageJson = event.getJson();

        processMessage(messageJson);
    }

    private void processMessage(String messageJson) {
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        if (messageProtobuf.getType().equals(MessageTypeEnum.STREAM)) {
            // ai回答暂不处理
            return;
        }
        ThreadProtobuf thread = messageProtobuf.getThread();
        if (thread == null) {
            throw new RuntimeException("thread is null");
        }
        if (thread.getType() != ThreadTypeEnum.AGENT 
            && thread.getType() != ThreadTypeEnum.WORKGROUP) {
            return;
        }
        log.info("auto_reply processMessage {}", messageProtobuf.getContent());
        //
        MessageTypeEnum messageType = messageProtobuf.getType();
        String messageContent = messageProtobuf.getContent();
        // 自动回复访客消息
        String threadTopic = thread.getTopic();
        String senderUid = messageProtobuf.getUser().getUid();
        if (!StringUtils.hasText(senderUid)) {
            throw new RuntimeException("senderUid is null");
        }
        if (TopicUtils.isOrgAgentTopic(threadTopic)) {
            // 一对一客服消息 org/agent/df_ag_uid/1420995827073219
            String[] splits = threadTopic.split("/");
            if (splits.length < 4) {
                throw new RuntimeException("auto_reply appointed topic format error");
            }
            String agentUid = splits[2];
            String visitorUid = splits[3];
            log.info("auto_reply agentUid {}, visitorUid {}", agentUid, visitorUid);
            if (senderUid.equals(visitorUid)) {
                autoReply(agentUid, messageType, messageContent, thread);
            }
        } else if (TopicUtils.isOrgWorkgroupTopic(threadTopic)) {
            // 技能组客服 topic格式：org/workgroup/{workgroup_uid}/{visitor_uid}
            String[] splits = threadTopic.split("/");
            if (splits.length < 4) {
                throw new RuntimeException("auto_reply workgroup topic format error");
            }
            String workgroupUid = splits[2];
            String visitorUid = splits[3];
            log.info("auto_reply workgroupUid {}, visitorUid {}", workgroupUid, visitorUid);
            // if (senderUid.equals(visitorUid)) {
            // autoReply(agentUid, messageType, messageContent, thread);
            // }
        }
    }

    // 自动回复访客消息
    private void autoReply(String agentUid, MessageTypeEnum messageType, String query,
            ThreadProtobuf thread) {
        log.info("auto_reply agentUid {}", agentUid);
        if (!StringUtils.hasText(agentUid)) {
            throw new RuntimeException("agentUid is null");
        }
        // 仅针对文本类型自动回复
        if (!messageType.equals(MessageTypeEnum.TEXT)) {
            return;
        }
        //
        // Optional<AgentEntity> agentOptional = agentService.findByUid(agentUid);
        // if (agentOptional.isPresent()) {
        //     AgentEntity agent = agentOptional.get();
        //     AutoReplySettings autoReplySettings = agent.getAutoReplySettings();
        //     // 自动回复访客消息
        //     if (autoReplySettings.isAutoReplyEnabled()) {
        //         UserProtobuf user = UserProtobuf.builder().build();
        //         user.setUid(agentUid);
        //         user.setNickname(agent.getNickname());
        //         user.setAvatar(agent.getAvatar());
        //         //
        //         MessageExtra extra = MessageExtra.builder()
        //                 .isAutoReply(true)
        //                 .autoReplyType(autoReplySettings.getAutoReplyType())
        //                 .orgUid(agent.getOrgUid())
        //                 .build();
        //         //
        //         MessageProtobuf message = MessageProtobuf.builder()
        //                 .uid(uidUtils.getUid())
        //                 .type(MessageTypeEnum.TEXT)
        //                 .status(MessageStatusEnum.SUCCESS)
        //                 .thread(thread)
        //                 .user(user)
        //                 .client(ClientEnum.AUTO_REPLY)
        //                 .extra(JSONObject.toJSONString(extra))
        //                 .createdAt(LocalDateTime.now())
        //                 .build();
        //         if (autoReplySettings.getAutoReplyType().equals(AutoReplyTypeEnum.FIXED.name())) {
        //             // 固定自动回复语
        //             MessageTypeEnum type = MessageTypeEnum.fromValue(autoReplySettings.getAutoReplyContentType());
        //             String content = autoReplySettings.getAutoReplyContent();
        //             log.info("auto_reply fixed reply: {}", content);
        //             message.setType(type);
        //             message.setContent(content);

        //             messageSendService.sendProtobufMessage(message);
        //         } else if (autoReplySettings.getAutoReplyType().equals(AutoReplyTypeEnum.KEYWORD.name())) {
        //             // 关键词自动回复语，匹配知识库
        //             String reply = keywordVipService.getKeywordReply(query, autoReplySettings.getKbUid(),
        //                     agent.getOrgUid());
        //             log.info("auto_reply keyword reply: {}, kbUid {}", reply, autoReplySettings.getKbUid());
        //             if (reply != null) {
        //                 message.setType(MessageTypeEnum.TEXT);
        //                 message.setContent(reply);
        //             } else {
        //                 message.setType(MessageTypeEnum.TEXT);
        //                 message.setContent(I18Consts.I18N_ROBOT_NO_REPLY);
        //             }
        //             messageSendService.sendProtobufMessage(message);
        //         } else if (autoReplySettings.getAutoReplyType().equals(AutoReplyTypeEnum.LLM.name())) {
        //             // TODO: 大模型自动回复语，默认先使用智谱ai，后续可修改配置接入其他大模型
        //             log.info("auto_reply llm reply: {}", "todo");
        //             // 返回一个输入中消息，让访客端显示输入中
        //             MessageProtobuf clonedMessage = SerializationUtils.clone(message);
        //             clonedMessage.setUid(uidUtils.getCacheSerialUid());
        //             clonedMessage.setType(MessageTypeEnum.PROCESSING);
        //             // MessageUtils.notifyUser(clonedMessage);
        //             messageSendService.sendProtobufMessage(clonedMessage);
        //             //
        //             zhipuaiChatService.sendWsKbAutoReply(query, autoReplySettings.getKbUid(), message);
        //             return;
        //         } else {
        //             log.info("auto_reply type not support: {}", autoReplySettings.getAutoReplyType());
        //         }
        //     } else {
        //         log.info("auto_reply disabled");
        //     }
        // }
    
    }
}
