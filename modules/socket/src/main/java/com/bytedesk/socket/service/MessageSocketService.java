/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-26 10:36:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-04 15:53:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.service;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.constant.MessageTypeConsts;
import com.bytedesk.core.constant.MqConsts;
import com.bytedesk.core.constant.StatusConsts;
import com.bytedesk.core.constant.ThreadTypeConsts;
import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.redis.RedisUserService;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.socket.mqtt.model.MqttSubscribe;
import com.bytedesk.socket.mqtt.service.MqttClientIdStoreService;
import com.bytedesk.socket.mqtt.service.MqttMessageIdService;
import com.bytedesk.socket.mqtt.service.MqttSessionStoreService;
import com.bytedesk.socket.mqtt.service.MqttSubscribeStoreService;
import com.bytedesk.socket.protobuf.model.MessageProto;
import com.bytedesk.socket.protobuf.model.ThreadProto;
import com.bytedesk.socket.protobuf.model.UserProto;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final MqttMessageIdService mqttMessageIdService;

    private final MqttSessionStoreService mqttSessionStoreService;

    private final MqttSubscribeStoreService mqttSubscribeStoreService;

    // private final MqttDupPublishMessageStoreService
    // mqttDupPublishMessageStoreService;

    private final MqttClientIdStoreService mqttClientIdStoreService;

    // private final EventPublisher eventPublisher;

    private final RedisUserService redisUserService;

    private final MessageService messageService;

    private final ThreadService threadService;

    private final UserService userService;

    public void sendJsonMessage(String messageJSON) {
        log.debug("send json message {}", messageJSON);

        if (!StringUtils.hasLength(messageJSON)) {
            return;
        }
        //
        try {
            //
            JSONObject messageObject = JSON.parseObject(messageJSON);
            //
            JSONObject topicObject;
            String messageType = messageObject.getString("type");
            //
            if (messageType.equals(MessageTypeConsts.TEXT) || messageType.equals(MessageTypeConsts.IMAGE)) {
                // 一般消息
                topicObject = messageObject.getJSONObject("thread");
            } else if (messageType.equals(MessageTypeConsts.NOTIFICATION_TRANSFER)
                    || messageType.equals(MessageTypeConsts.NOTIFICATION_TRANSFER_ACCEPT)
                    || messageType.equals(MessageTypeConsts.NOTIFICATION_TRANSFER_REJECT)) {
                // 会话转接
                topicObject = messageObject.getJSONObject("transfer");
            } else if (messageType.equals(MessageTypeConsts.NOTIFICATION_INVITE)
                    || messageType.equals(MessageTypeConsts.NOTIFICATION_INVITE_ACCEPT)
                    || messageType.equals(MessageTypeConsts.NOTIFICATION_INVITE_REJECT)) {
                // 会话邀请
                topicObject = messageObject.getJSONObject("invite");
            } else if (messageType.equals(MessageTypeConsts.NOTIFICATION_NOTICE)) {
                // 非会话消息，如：会议通知等
                topicObject = messageObject.getJSONObject("notice");
            } else {
                // 一般消息
                topicObject = messageObject.getJSONObject("thread");
            }
            //
            String jsonTopic = topicObject.getString("topic");
            if (jsonTopic != null) {
                // stomp客户端topic需要以'topic/'为前缀，故在发送给stomp客户端之前，添加此前缀
                // stomp客户端以'.'为多级分隔符，mqtt客户端以'/'为多级分隔符，故在发送给stomp客户端之前，需要替换掉'/'
                String topic = MqConsts.TOPIC_PREFIX + jsonTopic.replace("/", ".");
                log.debug("stomp topic {}", topic);
                // 发送给Stomp客户端
                simpMessagingTemplate.convertAndSend(topic, messageJSON);
            }

        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
        }
    }

    public void sendProtoMessage(MessageProto.Message messageProto) {
        log.debug("send proto message");
        //
        // 广播给消息发送者的多个客户端，如：pc客户端发送消息，手机客户端可以同步收到自己发送的消息, 群组会话除外
        String messageType = messageProto.getType();
        String threadType = messageProto.getThread().getType();
        byte[] messageBytes = messageProto.toByteArray();

        // 发送消息给订阅者
        // 只有contact会话需要替换tid/topic/nickname/avatar
        if (threadType.equals(ThreadTypeConsts.MEMBER)) {
            // 广播给消息接收者，一对一会话的tid互为翻转
            doSendToSenderClients(messageProto);
            // 广播给消息发送者的多个客户端，如：pc客户端发送消息，手机客户端可以同步收到自己发送的消息
            String tid = messageProto.getThread().getTid();
            String reverseTid = new StringBuffer(tid).reverse().toString();
            //
            String userNickname = messageProto.getUser().getNickname();
            String userAvatar = messageProto.getUser().getAvatar();
            String userTopic = messageProto.getUser().getUid();
            ThreadProto.Thread thread = messageProto.getThread().toBuilder()
                    .setTid(reverseTid)
                    .setNickname(userNickname)
                    .setAvatar(userAvatar)
                    .setTopic(userTopic)
                    .build();
            MessageProto.Message message = messageProto.toBuilder()
                    .setThread(thread)
                    .build();
            messageBytes = message.toByteArray();
        }

        // 转接会话
        if (messageType.equals(MessageTypeConsts.NOTIFICATION_TRANSFER)
                || messageType.equals(MessageTypeConsts.NOTIFICATION_TRANSFER_ACCEPT)
                || messageType.equals(MessageTypeConsts.NOTIFICATION_TRANSFER_REJECT)) {
            // 广播给消息发送者的多个客户端，如：pc客户端发送消息，手机客户端可以同步收到自己发送的消息
            doSendToSenderClients(messageProto);
            // 会话转接
            String transferTopic = messageProto.getTransfer().getTopic();
            // log.debug("doSendMqttProtobufMessage transferTopic {}", transferTopic);
            doSendToSubscribers(transferTopic, messageBytes);
            // TODO: 如果是accept的话，在数据库中修改thread的agent
            if (messageType.equals(MessageTypeConsts.NOTIFICATION_TRANSFER_ACCEPT)) {
                //
                String topic = messageProto.getThread().getTopic();
                String subscriberUid = messageProto.getUser().getUid();
                String unSubscriberUid = messageProto.getTransfer().getTopic();
                // 如果是accept的话，接受者这添加订阅，被接受者取消订阅
                subscribe(subscriberUid, topic);
                unsubscribe(unSubscriberUid, topic);
                // 发送事件通知，在其他地方更新数据库
                // FIXME: 多个服务器实例，会更新多次，但不影响正确性
                // eventPublisher.publishThreadTransferAccept(messageProto);
            }
            //
        } else if (messageType.equals(MessageTypeConsts.NOTIFICATION_INVITE)
                || messageType.equals(MessageTypeConsts.NOTIFICATION_INVITE_ACCEPT)
                || messageType.equals(MessageTypeConsts.NOTIFICATION_INVITE_REJECT)) {
            // 广播给消息发送者的多个客户端，如：pc客户端发送消息，手机客户端可以同步收到自己发送的消息, 群组会话除外
            doSendToSenderClients(messageProto);
            // 会话邀请
            String inviteTopic = messageProto.getInvite().getTopic();
            doSendToSubscribers(inviteTopic, messageBytes);
            //
        } else if (messageType.equals(MessageTypeConsts.NOTIFICATION_GROUP_CREATE)) {
            // 创建群组，给群成员添加订阅群组topic
            String topic = messageProto.getThread().getTopic();
            String uids = messageProto.getExtra().getContent();
            String[] uidList = uids.split(",");
            for (String uid : uidList) {
                subscribe(uid, topic);
            }
            //
            doSendToSubscribers(topic, messageBytes);
        } else if (messageType.equals(MessageTypeConsts.NOTIFICATION_NOTICE)) {
            // 非会话消息，如：会议通知等
            String topic = messageProto.getNotice().getTopic();
            doSendToSubscribers(topic, messageBytes);
        } else if (messageType.equals(MessageTypeConsts.NOTIFICATION_QUEUE_ACCEPT)) {
            //
            String topic = messageProto.getThread().getTopic();
            String subscriberUid = messageProto.getUser().getUid();
            // 给接入客服添加订阅
            subscribe(subscriberUid, topic);
            doSendToSubscribers(topic, messageBytes);
        } else {
            //
            String topic = messageProto.getThread().getTopic();
            log.debug("mqtt send topic {}", topic);
            doSendToSubscribers(topic, messageBytes);
        }
    }

    private void doSendToSubscribers(String topic, byte[] messageBytes) {
        log.debug("doSendToSubscribers {}", topic);
        //
        try {
            //
            MqttQoS mqttQoS = MqttQoS.AT_LEAST_ONCE;
            boolean dup = false;
            boolean retain = false;
            // 内存订阅信息
            List<MqttSubscribe> subscribeStores = mqttSubscribeStoreService.search(topic);
            subscribeStores.forEach(subscribeStore -> {
                //
                String clientId = subscribeStore.getClientId();
                // 当前活跃长连接信息
                if (mqttSessionStoreService.containsKey(clientId)) {
                    // 订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
                    // MqttQoS respQoS = mqttQoS.value() > subscribeStore.getMqttQoS() ?
                    // MqttQoS.valueOf(subscribeStore.getMqttQoS()) : mqttQoS;
                    MqttQoS respQoS = mqttQoS;
                    int messageId = mqttMessageIdService.getNextMessageId();
                    //
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, messageId),
                            Unpooled.buffer().writeBytes(messageBytes));
                    mqttSessionStoreService.get(clientId).getChannel().writeAndFlush(publishMessage);
                    // log.debug("topic {} clientId {}", topic, clientId);
                    //
                    // if (respQoS == MqttQoS.AT_LEAST_ONCE || respQoS == MqttQoS.EXACTLY_ONCE) {
                    // MqttDupPublishMessage dupPublishMessageStore = new MqttDupPublishMessage()
                    // .setClientId(clientId)
                    // .setTopic(topic)
                    // .setMqttQoS(respQoS.value())
                    // .setMessageBytes(messageBytes)
                    // .setMessageId(messageId);
                    // mqttDupPublishMessageStoreService.put(clientId, dupPublishMessageStore);
                    // }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSendToSenderClients(MessageProto.Message messageProto) {
        // String uid, String topic, byte[] messageBytes
        String uid = messageProto.getUser().getUid();
        String topic = messageProto.getThread().getTopic();
        byte[] messageBytes = messageProto.toByteArray();
        //
        List<String> clientIdList = mqttClientIdStoreService.get(uid);
        clientIdList.forEach(clientId -> {
            log.debug("doSendToSenderClients clientId {}", clientId);
            int messageId = mqttMessageIdService.getNextMessageId();
            MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_LEAST_ONCE, false, 0),
                    new MqttPublishVariableHeader(topic, messageId),
                    Unpooled.buffer().writeBytes(messageBytes));
            mqttSessionStoreService.get(clientId).getChannel().writeAndFlush(publishMessage);
        });
    }

    private void subscribe(String uid, String topic) {
        log.debug("subscribe uid {}, topic {}", uid, topic);
        redisUserService.addTopic(uid, topic);
        //
        MqttQoS qoS = MqttQoS.AT_LEAST_ONCE;
        List<String> clientIdList = mqttClientIdStoreService.get(uid);
        clientIdList.forEach(clientId -> {
            log.debug("subscribe clientId {}", clientId);
            final MqttSubscribe subscribeStore = new MqttSubscribe(clientId, topic, qoS.value());
            mqttSubscribeStoreService.put(topic, subscribeStore);
        });
    }

    private void unsubscribe(String uid, String topic) {
        log.debug("unsubscribe uid {}, topic {}", uid, topic);
        redisUserService.removeTopic(uid, topic);
        //
        List<String> clientIdList = mqttClientIdStoreService.get(uid);
        clientIdList.forEach(clientId -> {
            log.debug("unsubscribe clientId {}", clientId);
            mqttSubscribeStoreService.remove(topic, clientId);
        });
    }

    public void saveToDb(@NonNull byte[] messageBytes) {
        //
        try {
            MessageProto.Message messageProto = MessageProto.Message.parseFrom(messageBytes);
            //
            String type = messageProto.getType();
            log.info("saveToDb {}", type);

            // 处理消息回执
            dealWithMessageReceipt(type, messageProto);

            // 消息撤回，从数据库中删除消息
            dealWithMessageRecall(type, messageProto);

            // 流式输出，根据mid替换掉旧消息内容content
            dealWithMessageStream(messageProto);

            // 消息所属的会话
            Thread thread = getThread(messageProto);

            Message message = getMessage(thread, messageProto);

            threadService.save(thread);
            //
            messageService.save(message);

            log.info("save msg mid {}, tid {}", message.getMid(), thread.getTid());

            // 以json格式存储到message archive里面，方便搜索
            // String messageJson = ProtobufJsonUtil.toJson(messageProto);
            // messageArchiveService.save(messageJson, type, sendUser.getSubDomain());

        } catch (InvalidProtocolBufferException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 处理消息回执
     * 
     * @param type
     * @param messageProto
     */
    private void dealWithMessageReceipt(String type, MessageProto.Message messageProto) {
        //
        if (type.equals(MessageTypeConsts.NOTIFICATION_RECEIPT)) {
            // receipt消息状态，更新到数据库
            String receiptMid = messageProto.getReceipt().getMid();
            String status = messageProto.getReceipt().getStatus();
            //
            Optional<Message> messageOptional = messageService.findByMid(receiptMid);
            if (messageOptional.isPresent()
                    && !messageOptional.get().getStatus().equals(StatusConsts.MESSAGE_STATUS_READ)) {
                // 更新非read状态
                messageOptional.get().setStatus(status);
                messageService.save(messageOptional.get());
            }
            // 收到已读回执，删除缓存
            if (status.equals(StatusConsts.MESSAGE_STATUS_READ)) {
                // 更新缓存中已读状态
                // String topic = messageProto.getThread().getTopic();
                // redisMessageCacheFetchService.updateMessageStatusByTopic(topic, receiptMid);
                // // 会话tid
                // String threadTid = messageProto.getThread().getTid();
                // // 客服uid
                // String agentUid = redisThreadService.getThreadAgentMap(threadTid);
                // // 发送消息者的uid
                // String senderUid = messageProto.getUser().getUid();
                // // 如果不是客服自己发送的消息
                // if (!senderUid.equals(agentUid)) {
                // // 访客发送的消息。收到receipt之后，会从客服缓存中删除
                // redisMessageCacheFetchService.removeMessageAgentUnread(agentUid, receiptMid);
                // } else {
                // String[] tids = threadTid.split("_");
                // if (tids.length > 1) {
                // // 访客uid
                // String visitorUid = tids[1];
                // // 客服发送的消息。收到receipt之后，会从访客缓存中删除
                // redisMessageCacheFetchService.removeMessageVisitorUnread(visitorUid,
                // receiptMid);
                // }
                // }
            }
            return;
        }

    }

    /**
     * 消息撤回，从数据库中删除消息
     * 
     * @param type
     * @param messageProto
     */
    private void dealWithMessageRecall(String type, MessageProto.Message messageProto) {
        //
        if (type.equals(MessageTypeConsts.NOTIFICATION_RECALL)) {
            String recallMid = messageProto.getRecall().getMid();
            messageService.deleteByMid(recallMid);
            // 会话tid
            String threadTid = messageProto.getThread().getTid();
            String[] tids = threadTid.split("_");
            if (tids.length > 1) {
                // 访客uid
                // String visitorUid = tids[1];
                /// 从缓存中删除
                // redisMessageCacheFetchService.removeMessage(visitorUid, recallMid);
            }
            // 会话topic
            // String topic = messageProto.getThread().getTopic();
            // redisMessageCacheFetchService.removeMessageByTopic(topic, recallMid);
            return;
        }

    }

    /**
     * 流式输出，根据mid替换掉旧消息内容content
     * 
     * @param type
     * @param messageProto
     */
    private void dealWithMessageStream(MessageProto.Message messageProto) {
        String mid = messageProto.getMid();
        Optional<Message> messageOptional = messageService.findByMid(mid);
        if (messageOptional.isPresent()) {
            //
            String content = messageProto.getText().getContent();
            messageOptional.get().setContent(content);
            messageService.save(messageOptional.get());
            return;
        }
    }

    private Thread getThread(MessageProto.Message messageProto) {

        ThreadProto.Thread threadProto = messageProto.getThread();
        String tid = threadProto.getTid();
        // log.info("tid: {}, threadType {}", tid, threadType);
        Thread thread = threadService.findByTid(tid).orElse(null);
        thread.setContent(threadProto.getContent());
        return thread;
    }

    private Message getMessage(Thread thread, MessageProto.Message messageProto) {
        //
        String type = messageProto.getType();
        // 消息发送者
        UserProto.User userProto = messageProto.getUser();
        String senderUid = userProto.getUid();
        log.info("uid: {}", senderUid);
        User user = userService.findByUid(senderUid).orElse(null);
        //
        String mid = messageProto.getMid();
        // 持久化消息
        Message message = new Message();
        message.setMid(mid);
        message.setType(type);
        message.setStatus(StatusConsts.MESSAGE_STATUS_STORED);
        message.setClient(messageProto.getClient());
        message.setUser(user);
        // message.setThread(thread);
        message.getThreads().add(thread);
        if (thread.getType().equals(ThreadTypeConsts.MEMBER)) {
            Thread reverseThread = threadService.getReverse(thread);
            message.getThreads().add(reverseThread);
        }
        //
        // 设置为消息体内的时间戳，但是数据库中还是存储由@CreatedDate生成的时间戳
        message.setCreatedAt(BdDateUtils.formatStringToDateTime(messageProto.getTimestamp()));
        message.setUpdatedAt(BdDateUtils.formatStringToDateTime(messageProto.getTimestamp()));
        // 暂定：所有消息类型均放到text里面处理，复杂类型存储json
        String content = messageProto.getText().getContent();
        message.setContent(content);
        // switch (type) {
        //     case MessageTypeConsts.TEXT:
        //         log.info("message type {}", type);
        //         String content = messageProto.getText().getContent();
        //         message.setContent(content);
        //         break;
        //     case MessageTypeConsts.IMAGE:
        //         //
        //         String imageUrl = messageProto.getImage().getImageUrl();
        //         message.setContent(imageUrl);
        //         break;
        //     case MessageTypeConsts.FILE:
        //         //
        //         String fileUrl = messageProto.getFile().getFileUrl();
        //         message.setContent(fileUrl);
        //         break;
        //     case MessageTypeConsts.VOICE:
        //         //
        //         String vioceUrl = messageProto.getVoice().getVoiceUrl();
        //         message.setContent(vioceUrl);
        //         break;
        //     case MessageTypeConsts.VIDEO:
        //         //
        //         String videoUrl = messageProto.getVideo().getVideoOrShortUrl();
        //         message.setContent(videoUrl);
        //         break;
        //     case MessageTypeConsts.COMMODITY:
        //         String commodity = messageProto.getText().getContent();
        //         message.setContent(commodity);
        //         break;
        //     default:
        //         log.info("message other type {}", type);
        //         String otherContent = messageProto.getText().getContent();
        //         if (!StringUtils.hasLength(otherContent)) {
        //             otherContent = messageProto.getExtra().getContent();
        //         }
        //         message.setContent(otherContent);
        //         break;
        // }
        //
        return message;
    }

}
