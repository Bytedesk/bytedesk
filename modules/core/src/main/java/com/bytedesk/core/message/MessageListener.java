/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-30 09:50:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-04 20:55:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

// import java.io.IOException;

// import org.springframework.context.event.EventListener;
// import org.springframework.stereotype.Component;

// import com.bytedesk.core.socket.protobuf.model.MessageProto;
// import com.bytedesk.core.socket.utils.MessageConvertUtils;
// import com.bytedesk.core.thread.ThreadProtobuf;
// import com.bytedesk.core.thread.ThreadTypeEnum;
// import com.google.protobuf.InvalidProtocolBufferException;
// import com.alibaba.fastjson2.JSON;
// import com.bytedesk.core.cache.CaffeineCacheService;

// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Component
// @AllArgsConstructor
// public class MessageListener {

//     private final MessageSocketService messageSocketService;

//     private final CaffeineCacheService caffeineCacheService;

//     @EventListener
//     public void onMessageJsonEvent(MessageJsonEvent event) {
//         log.info("MessageJsonEvent {}", event.getJson());
//         //
//         String messageJson = event.getJson();
//         //
//         messageJson = processMessage(messageJson);
//         messageSocketService.sendJsonMessage(messageJson);
//         //
//         try {
//             MessageProto.Message message = MessageConvertUtils.toProtoBean(MessageProto.Message.newBuilder(),
//                     messageJson);
//             messageSocketService.sendProtoMessage(message);
//         } catch (IOException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
//     }

//     @EventListener
//     public void onMessageProtoEvent(MessageProtoEvent event) {
//         log.info("MessageProtoEvent");
//         //
//         try {
//             MessageProto.Message messageProto = MessageProto.Message.parseFrom(event.getMessageBytes());
//             // JSON
//             try {
//                 String messageJson = MessageConvertUtils.toJson(messageProto);
//                 //
//                 messageJson = processMessage(messageJson);
//                 messageSocketService.sendJsonMessage(messageJson);
//                 // process处理完毕之后，重新发送proto消息
//                 messageProto = MessageConvertUtils.toProtoBean(MessageProto.Message.newBuilder(), messageJson);
//                 messageSocketService.sendProtoMessage(messageProto);
//             } catch (IOException e) {
//                 // TODO Auto-generated catch block
//                 e.printStackTrace();
//             }
//             //
//         } catch (InvalidProtocolBufferException e) {
//             e.printStackTrace();
//         }
//     }

//     //
//     private String processMessage(String messageJson) {
//         log.info("processMessage {}", messageJson);
//         // 缓存消息，用于定期持久化到数据库
//         caffeineCacheService.pushForPersist(messageJson);
//         //
//         MessageProtobuf messageObject = JSON.parseObject(messageJson, MessageProtobuf.class);
//         if (messageObject.getStatus().equals(MessageStatusEnum.SENDING)) {
//             messageObject.setStatus(MessageStatusEnum.SUCCESS);
//         }
//         //
//         ThreadProtobuf thread = messageObject.getThread();
//         if (thread == null) {
//             throw new RuntimeException("thread is null");
//         }
//         String topic = thread.getTopic();
//         ThreadTypeEnum threadType = thread.getType();
//         MessageTypeEnum messageType = messageObject.getType();
//         //
//         if (threadType.equals(ThreadTypeEnum.AGENT)) {
//             // 一对一客服消息 org/agent/default_agent_uid/1420995827073219
//             String[] splits = topic.split("/");
//             if (splits.length < 4) {
//                 throw new RuntimeException("appointed topic format error");
//             }
//             String agentUid = splits[2];
//             String visitorUid = splits[3];
//             log.info("agentUid {}, visitorUid {}", agentUid, visitorUid);
//             //
//             if (MessageTypeEnum.isRecept(messageType)) {
//                 caffeineCacheService.removeMessage(agentUid, messageObject);
//                 caffeineCacheService.removeMessage(visitorUid, messageObject);
//             } else if (MessageTypeEnum.shouldCache(messageType)) {
//                 caffeineCacheService.push(agentUid, messageJson);
//                 caffeineCacheService.push(visitorUid, messageJson);
//             }
//         } else if (threadType.equals(ThreadTypeEnum.WORKGROUP)) {
//             // 技能组客服 topic格式：org/workgroup/{workgroup_uid}/{agent_uid}/{visitor_uid}
//             String[] splits = topic.split("/");
//             if (splits.length < 5) {
//                 throw new RuntimeException("workgroup topic format error");
//             }
//             String workgroupUid = splits[2];
//             String agentUid = splits[3];
//             String visitorUid = splits[4];
//             log.info("workgroupUid {}, agentUid {}, visitorUid {}", workgroupUid, agentUid, visitorUid);
//             //
//             if (MessageTypeEnum.isRecept(messageType)) {
//                 caffeineCacheService.removeMessage(agentUid, messageObject);
//                 caffeineCacheService.removeMessage(visitorUid, messageObject);
//             } else if (MessageTypeEnum.shouldCache(messageType)) {
//                 caffeineCacheService.push(agentUid, messageJson);
//                 caffeineCacheService.push(visitorUid, messageJson);
//             }
//         } else if (threadType.equals(ThreadTypeEnum.GROUP)) {
//             // 群组消息 topic格式：org/group/{group_uid}
//             String[] splits = topic.split("/");
//             if (splits.length < 3) {
//                 throw new RuntimeException("group topic format error");
//             }
//             String groupUid = splits[2];
//             log.info("groupUid {}", groupUid);
//             caffeineCacheService.pushGroup(groupUid, messageJson);

//         } else if (threadType.equals(ThreadTypeEnum.MEMBER)) {
//             // 同事私聊 topic格式：org/private/{self_member_uid}/{other_member_uid}
//             String[] splits = topic.split("/");
//             if (splits.length < 3) {
//                 throw new RuntimeException("member topic format error");
//             }
//             String selfUid = splits[2];
//             String otherUid = splits[3];
//             log.info("selfUid {}, otherUid {}", selfUid, otherUid);
//             // 同事私聊，仅给对方缓存消息即可
//             if (MessageTypeEnum.isRecept(messageType)) {
//                 caffeineCacheService.removeMessage(otherUid, messageObject);
//             } else if (MessageTypeEnum.shouldCache(messageType)) {                
//                 caffeineCacheService.push(otherUid, messageJson);
//             }
//         }
//         //
//         // if (messageType == MessageTypeEnum.TEXT) {
//         //     // TODO: 自动回复
//         // }

//         // TODO: 替换掉客户端时间戳，统一各个客户端时间戳，防止出现因为客户端时间戳不一致导致的消息乱序
//         // MessageResponse messageResponse = JSON.parseObject(messageJson,
//         // MessageResponse.class);
//         // messageResponse.setCreatedAt(new Date());
//         // String newStompJson = JSON.toJSONString(messageResponse);

//         // TODO: 拦截被拉黑/屏蔽用户消息，并给与提示

//         // TODO: 过滤敏感词，将敏感词替换为*
//         // String filterJson = TabooUtil.replaceSensitiveWord(json, '*');

//         // TODO: 自动回复

//         // TODO: 离线推送

//         // TODO: webhook

//         return JSON.toJSONString(messageObject);
//     }

// }
