/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-01 12:37:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-05 12:14:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message_unread;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageCreateEvent;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageUpdateEvent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.socket.mqtt.event.MqttConnectedEvent;
import com.bytedesk.core.socket.stomp.event.StompConnectedEvent;
// import com.bytedesk.core.topic.Topic;
// import com.bytedesk.core.topic.TopicService;
import com.bytedesk.core.topic.TopicUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MessageUnreadEventListener {

    private final MessageUnreadService messageUnreadService;

    // private final TopicService topicService;

    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        Message message = event.getMessage();
        log.info("message unread create event: {}", message.getContent());
        //
        String threadTopic = message.getThreadTopic();
        String userString = message.getUser();
        UserProtobuf user = JSONObject.parseObject(userString, UserProtobuf.class);
        String userUid = user.getUid();
        //
        if (threadTopic.startsWith(TopicUtils.TOPIC_ORG_AGENT_PREFIX)) {
            // 一对一客服消息 org/agent/default_agent_uid/1420995827073219
            String[] splits = threadTopic.split("/");
            if (splits.length < 4) {
                throw new RuntimeException("appointed topic format error");
            }
            String agentUid = splits[2];
            String visitorUid = splits[3];
            log.info("agentUid {}, visitorUid {}", agentUid, visitorUid);
            // 仅缓存接受者未读消息
            if (userUid.equals(agentUid)) {
                messageUnreadService.create(message, visitorUid);
            }
            if (userUid.equals(visitorUid)) {
                messageUnreadService.create(message, agentUid);
            }
            //
        } else if (threadTopic.startsWith(TopicUtils.TOPIC_ORG_WORKGROUP_PREFIX)) {
            // 技能组客服 topic格式：org/workgroup/{workgroup_uid}/{agent_uid}/{visitor_uid}
            String[] splits = threadTopic.split("/");
            if (splits.length < 5) {
                throw new RuntimeException("workgroup topic format error");
            }
            String workgroupUid = splits[2];
            String agentUid = splits[3];
            String visitorUid = splits[4];
            log.info("workgroupUid {}, agentUid {}, visitorUid {}", workgroupUid, agentUid, visitorUid);
            //
            // 仅缓存接受者未读消息
            if (userUid.equals(agentUid)) {
                messageUnreadService.create(message, visitorUid);
            }
            if (userUid.equals(visitorUid)) {
                messageUnreadService.create(message, agentUid);
            }

        } else if (threadTopic.startsWith(TopicUtils.TOPIC_ORG_GROUP_PREFIX)) {
            // 群组消息 topic格式：org/group/{group_uid}
            String[] splits = threadTopic.split("/");
            if (splits.length < 3) {
                throw new RuntimeException("group topic format error");
            }
            String groupUid = splits[2];
            log.info("groupUid {}", groupUid);
            // TODO: 缓存群聊未读消息
            // Set<Topic> topics = topicService.findByTopic(threadTopic);
            // for (Topic topic : topics) {
            // Set<String> clientIds = topic.getClientIds();
            // for (String clientId : clientIds) {
            // // 用户clientId格式: uid/client/deviceUid
            // String[] clientIdSplits = clientId.split("/");
            // String userUid = clientIdSplits[0];
            // messageUnreadService.create(message, userUid);
            // }
            // }
        } else if (threadTopic.startsWith(TopicUtils.TOPIC_ORG_PRIVATE_PREFIX)) {
            // 同事私聊 topic格式：org/private/{self_member_uid}/{other_member_uid}
            String[] splits = threadTopic.split("/");
            if (splits.length < 3) {
                throw new RuntimeException("member topic format error");
            }
            String selfUid = splits[2];
            String otherUid = splits[3];
            log.info("selfUid {}, otherUid {}", selfUid, otherUid);
            // 同事私聊，仅给对方缓存消息即可
            messageUnreadService.create(message, otherUid);

        }
    }

    @EventListener
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        Message message = event.getMessage();
        log.info("message unread update event: {}", message.getContent());
        //
        String threadTopic = message.getThreadTopic();
        MessageStatusEnum messageStatus = message.getStatus();
        //
        if (messageStatus.ordinal() < MessageStatusEnum.DELIVERED.ordinal()) {
            return;
        }
        //
        if (threadTopic.startsWith(TopicUtils.TOPIC_ORG_AGENT_PREFIX)) {
            // 一对一客服消息 org/agent/default_agent_uid/1420995827073219
            String[] splits = threadTopic.split("/");
            if (splits.length < 4) {
                throw new RuntimeException("appointed topic format error");
            }
            String agentUid = splits[2];
            String visitorUid = splits[3];
            log.info("agentUid {}, visitorUid {}", agentUid, visitorUid);
            //
            messageUnreadService.delete(agentUid);
            messageUnreadService.delete(visitorUid);
            //
        } else if (threadTopic.startsWith(TopicUtils.TOPIC_ORG_WORKGROUP_PREFIX)) {
            // 技能组客服 topic格式：org/workgroup/{workgroup_uid}/{agent_uid}/{visitor_uid}
            String[] splits = threadTopic.split("/");
            if (splits.length < 5) {
                throw new RuntimeException("workgroup topic format error");
            }
            String workgroupUid = splits[2];
            String agentUid = splits[3];
            String visitorUid = splits[4];
            log.info("workgroupUid {}, agentUid {}, visitorUid {}", workgroupUid, agentUid, visitorUid);
            //
            messageUnreadService.delete(agentUid);
            messageUnreadService.delete(visitorUid);

        } else if (threadTopic.startsWith(TopicUtils.TOPIC_ORG_GROUP_PREFIX)) {
            // 群组消息 topic格式：org/group/{group_uid}
            String[] splits = threadTopic.split("/");
            if (splits.length < 3) {
                throw new RuntimeException("group topic format error");
            }
            String groupUid = splits[2];
            log.info("groupUid {}", groupUid);
            // TODO: 群聊消息删除未读

        } else if (threadTopic.startsWith(TopicUtils.TOPIC_ORG_PRIVATE_PREFIX)) {
            // 同事私聊 topic格式：org/private/{self_member_uid}/{other_member_uid}
            String[] splits = threadTopic.split("/");
            if (splits.length < 3) {
                throw new RuntimeException("member topic format error");
            }
            String selfUid = splits[2];
            String otherUid = splits[3];
            log.info("selfUid {}, otherUid {}", selfUid, otherUid);
            // 删除未读
            messageUnreadService.delete(otherUid);

        }
    }

    // @EventListener
    // public void onQuartzFiveSecondEvent(QuartzFiveSecondEvent event) {
    // // log.info("message quartz five second event: " + event);
    // List<String> keys = caffeineCacheService.getAllKeyList();
    // for (String key : keys) {
    // // log.info("message cache key: " + key);
    // List<String> messages = caffeineCacheService.getList(key);
    // if (messages == null || messages.isEmpty() || messages.size() == 0) {
    // continue;
    // }
    // Iterator<String> iterator = messages.iterator();
    // while (iterator.hasNext()) {
    // String messageJson = iterator.next();
    // // log.info("message cache value: " + message);
    // MessageProtobuf messageProtobuf = JSON.parseObject(messageJson,
    // MessageProtobuf.class);
    // // 仅存储指定类型
    // if (messageProtobuf.getType() == MessageTypeEnum.TEXT
    // || messageProtobuf.getType() == MessageTypeEnum.IMAGE
    // || messageProtobuf.getType() == MessageTypeEnum.FILE
    // || messageProtobuf.getType() == MessageTypeEnum.AUDIO
    // || messageProtobuf.getType() == MessageTypeEnum.VIDEO) {
    // // messageUnreadService.add(messageJson, key);
    // //

    // }
    // }
    // }
    // }

    @EventListener
    public void onMqttConnectEvent(MqttConnectedEvent event) {
        // 用户clientId格式: uid/client/deviceUid
        String clientId = event.getClientId();
        log.info("message unread mqtt connect event: {}", clientId);
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
        log.info("message unread stomp session connect event: {}", event.getClientId());
    }

}