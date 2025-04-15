/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-01 12:37:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-15 17:47:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unread;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageCreateEvent;
import com.bytedesk.core.message.event.MessageUpdateEvent;
import com.bytedesk.core.socket.mqtt.event.MqttConnectedEvent;
import com.bytedesk.core.socket.stomp.StompConnectedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO: 添加过期时间，过期之后，自动删除
@Slf4j
@Component
@AllArgsConstructor
public class MessageUnreadEventListener {

    // private final MessageUnreadService messageUnreadService;

    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        MessageEntity message = event.getMessage();
        if (MessageTypeEnum.STREAM.name().equalsIgnoreCase(message.getType()) || 
            MessageTypeEnum.NOTICE.name().equalsIgnoreCase(message.getType()) ||
            MessageTypeEnum.SYSTEM.name().equalsIgnoreCase(message.getType())) {
            return;
        }
        if (ClientEnum.SYSTEM.name().equalsIgnoreCase(message.getClient())) {
            return;
        }
        log.info("message unread create event: {} {} {}", message.getUid(), message.getType(), message.getContent());
        // 缓存未读消息
        // String threadTopic = message.getThread().getTopic();
        // String userString = message.getUser();
        // UserProtobuf user = UserProtobuf.fromJson(userString); //JSONObject.parseObject(userString, UserProtobuf.class);
        // String userUid = user.getUid();
        //
        // if (TopicUtils.isOrgAgentTopic(threadTopic)) {
        //     // 一对一客服消息 org/agent/default_agent_uid/1420995827073219
        //     String[] splits = threadTopic.split("/");
        //     if (splits.length < 4) {
        //         throw new RuntimeException("appointed topic format error");
        //     }
        //     String agentUid = splits[2];
        //     String visitorUid = splits[3];
        //     // log.info("onMessageCreateEvent agentUid {}, visitorUid {}", agentUid, visitorUid);
        //     // 仅缓存接受者未读消息
        //     if (userUid.equals(agentUid)) {
        //         messageUnreadService.create(message, visitorUid);
        //     }
        //     if (userUid.equals(visitorUid)) {
        //         messageUnreadService.create(message, agentUid);
        //     }
        //     //
        // } else if (TopicUtils.isOrgWorkgroupTopic(threadTopic)) {
        //     // 技能组客服 topic格式：org/workgroup/{workgroup_uid}/{visitor_uid}
        //     String[] splits = threadTopic.split("/");
        //     if (splits.length < 4) {
        //         throw new RuntimeException("workgroup topic format error");
        //     }
        //     String workgroupUid = splits[2];
        //     // String agentUid = splits[3];
        //     String visitorUid = splits[3];
        //     log.info("workgroupUid {}, visitorUid {}", workgroupUid, visitorUid);
        //     //
        //     // 仅缓存接受者未读消息
        //     // if (userUid.equals(agentUid)) {
        //     //     messageUnreadService.create(message, visitorUid);
        //     // }
        //     // if (userUid.equals(visitorUid)) {
        //     //     messageUnreadService.create(message, agentUid);
        //     // }

        // } else if (TopicUtils.isOrgGroupTopic(threadTopic)) {
        //     // 群组消息 topic格式：org/group/{group_uid}
        //     String[] splits = threadTopic.split("/");
        //     if (splits.length < 3) {
        //         throw new RuntimeException("group topic format error");
        //     }
        //     String groupUid = splits[2];
        //     log.info("groupUid {}", groupUid);
        //     // TODO: 缓存群聊未读消息
        //     // Set<Topic> topics = topicService.findFirstByTopic(threadTopic);
        //     // for (Topic topic : topics) {
        //     // Set<String> clientIds = topic.getClientIds();
        //     // for (String clientId : clientIds) {
        //     // // 用户clientId格式: uid/client/deviceUid
        //     // String[] clientIdSplits = clientId.split("/");
        //     // String userUid = clientIdSplits[0];
        //     // messageUnreadService.create(message, userUid);
        //     // }
        //     // }
        // } else if (TopicUtils.isOrgMemberTopic(threadTopic)) {
        //     // 同事私聊 topic格式：org/member/{self_member_uid}/{other_member_uid}
        //     String[] splits = threadTopic.split("/");
        //     if (splits.length < 3) {
        //         throw new RuntimeException("member topic format error");
        //     }
        //     String selfUid = splits[2];
        //     String otherUid = splits[3];
        //     log.info("selfUid {}, otherUid {}", selfUid, otherUid);
        //     // 同事私聊，仅给对方缓存消息即可
        //     messageUnreadService.create(message, otherUid);

        // }
    
    }

    @EventListener
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        MessageEntity message = event.getMessage();
        if (MessageTypeEnum.STREAM.name().equalsIgnoreCase(message.getType()) || 
            MessageTypeEnum.NOTICE.name().equalsIgnoreCase(message.getType()) || 
            MessageTypeEnum.SYSTEM.name().equalsIgnoreCase(message.getType())) {
            return;
        }
        if (ClientEnum.SYSTEM.name().equalsIgnoreCase(message.getClient())) {
            return;
        }
        log.info("message unread update event: {} {} {}", message.getUid(), message.getType(), message.getContent());
        //
        // String threadTopic = message.getThread().getTopic();
        // MessageStatusEnum messageState = MessageStatusEnum.fromValue(message.getStatus());
        // if (messageState.ordinal() < MessageStatusEnum.DELIVERED.ordinal()) {
        //     return;
        // }
        // 删除已读消息
        // if (TopicUtils.isOrgAgentTopic(threadTopic)) {
        //     // 一对一客服消息 org/agent/default_agent_uid/1420995827073219
        //     String[] splits = threadTopic.split("/");
        //     if (splits.length < 4) {
        //         throw new RuntimeException("appointed topic format error");
        //     }
        //     String agentUid = splits[2];
        //     String visitorUid = splits[3];
        //     log.info("onMessageUpdateEvent agentUid {}, visitorUid {}", agentUid, visitorUid);
        //     //
        //     messageUnreadService.delete(agentUid);
        //     messageUnreadService.delete(visitorUid);
        //     //
        // } else if (TopicUtils.isOrgWorkgroupTopic(threadTopic)) {
        //     // 技能组客服 topic格式：org/workgroup/{workgroup_uid}/{visitor_uid}
        //     String[] splits = threadTopic.split("/");
        //     if (splits.length < 4) {
        //         throw new RuntimeException("workgroup topic format error");
        //     }
        //     String workgroupUid = splits[2];
        //     // String agentUid = splits[3];
        //     String visitorUid = splits[3];
        //     log.info("workgroupUid {}, visitorUid {}", workgroupUid, visitorUid);
        //     //
        //     // messageUnreadService.delete(agentUid);
        //     messageUnreadService.delete(visitorUid);

        // } else if (TopicUtils.isOrgGroupTopic(threadTopic)) {
        //     // 群组消息 topic格式：org/group/{group_uid}
        //     String[] splits = threadTopic.split("/");
        //     if (splits.length < 3) {
        //         throw new RuntimeException("group topic format error");
        //     }
        //     String groupUid = splits[2];
        //     log.info("groupUid {}", groupUid);
        //     // TODO: 群聊消息删除未读

        // } else if (TopicUtils.isOrgMemberTopic(threadTopic)) {
        //     // 同事私聊 topic格式：org/member/{self_member_uid}/{other_member_uid}
        //     String[] splits = threadTopic.split("/");
        //     if (splits.length < 3) {
        //         throw new RuntimeException("member topic format error");
        //     }
        //     String selfUid = splits[2];
        //     String otherUid = splits[3];
        //     log.info("selfUid {}, otherUid {}", selfUid, otherUid);
        //     // 删除未读
        //     messageUnreadService.delete(otherUid);

        // }
    
    }

    @EventListener
    public void onMqttConnectEvent(MqttConnectedEvent event) {
        // 用户clientId格式: uid/client/deviceUid
        // String clientId = event.getClientId();
        // log.info("message unread mqtt connect event: {}", clientId);
        // String[] clientIdArray = clientId.split("/");
        // if (clientIdArray.length != 3) {
        // return;
        // }
        // String userUid = clientIdArray[0];
        // TODO: 将缓存消息推送给相应客服端
        // List<MessageUnread> messageList = messageUnreadService.getMessages(userUid);
        // if (messageList == null || messageList.isEmpty()) {
        // return;
        // }
    }

    @EventListener
    public void onStompSessionConnectedEvent(StompConnectedEvent event) {
        // TODO: 将缓存消息推送给相应访客端
        // log.info("message unread stomp session connect event: {}", event.getClientId());
    }

}
