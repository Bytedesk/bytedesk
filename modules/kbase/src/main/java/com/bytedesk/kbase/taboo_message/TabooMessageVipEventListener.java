/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-30 17:06:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-15 18:08:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo_message;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 在此实现敏感词过滤逻辑
// 在此实现关键词回复逻辑
@Slf4j
@Component
@AllArgsConstructor
public class TabooMessageVipEventListener {

    // private final UidUtils uidUtils;

    // private final AgentService agentService;

    // private final BytedeskEventPublisher bytedeskEventPublisher;

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        // log.info("MessageJsonEvent {}", event.getJson());
        String messageJson = event.getJson();

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
    //         //
    //     } catch (InvalidProtocolBufferException e) {
    //         e.printStackTrace();
    //     }
    // }

    private void processMessage(String messageJson) {
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        if (messageProtobuf.getType().equals(MessageTypeEnum.STREAM)) {
            // ai回答暂不处理
            return;
        }
        // log.info("taboo_message processMessage {}", messageProtobuf.getContent());
        //
        // ThreadProtobuf thread = messageProtobuf.getThread();
        // if (thread == null) {
        //     throw new RuntimeException("thread is null");
        // }
        // // 自动回复访客消息
        // String threadTopic = thread.getTopic();
        // String senderUid = messageProtobuf.getUser().getUid();
        // if (TopicUtils.isOrgAgentTopic(threadTopic)) {
        //     // 一对一客服消息 org/agent/default_agent_uid/1420995827073219
        //     String[] splits = threadTopic.split("/");
        //     if (splits.length < 4) {
        //         throw new RuntimeException("taboo_message appointed topic format error");
        //     }
        //     String agentUid = splits[2];
        //     String visitorUid = splits[3];
        //     log.info("taboo_message agentUid {}, visitorUid {}", agentUid, visitorUid);
        //     if (senderUid.equals(visitorUid)) {
        //         filterTabooMessage(agentUid, visitorUid, thread);
        //     }
        // } else if (TopicUtils.isOrgWorkgroupTopic(threadTopic)) {
        //     // 技能组客服 topic格式：org/workgroup/{workgroup_uid}/{agent_uid}/{visitor_uid}
        //     String[] splits = threadTopic.split("/");
        //     if (splits.length < 5) {
        //         throw new RuntimeException("taboo_message workgroup topic format error");
        //     }
        //     String workgroupUid = splits[2];
        //     String agentUid = splits[3];
        //     String visitorUid = splits[4];
        //     log.info("taboo_message workgroupUid {}, agentUid {}, visitorUid {}", workgroupUid, agentUid, visitorUid);
        //     if (senderUid.equals(visitorUid)) {
        //         filterTabooMessage(agentUid, visitorUid, thread);
        //     }
        // }
    }

    // TODO: 过滤敏感词，并使用全局变量标记已处理敏感词
    // private void filterTabooMessage(String agentUid, String visitorUid, ThreadProtobuf thread) {

    // }

}
