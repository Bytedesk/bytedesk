/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-30 11:05:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-29 21:42:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member.mq;

// import java.util.Map;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.jms.annotation.JmsListener;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import com.bytedesk.core.jms.JmsArtemisConsts;
// import com.bytedesk.core.utils.OptimisticLockingHandler;
// import com.bytedesk.service.queue_member.QueueMemberEntity;
// import com.bytedesk.service.queue_member.QueueMemberRepository;

// import jakarta.jms.JMSException;
// import jakarta.jms.Message;
// import jakarta.jms.MessageListener;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * 队列成员消息消费者
//  * 用于处理队列成员更新消息
//  */
// @Slf4j
// @Component
// public class QueueMemberConsumer implements MessageListener {

//     @Autowired
//     private QueueMemberRepository queueMemberRepository;
    
//     @Autowired
//     private OptimisticLockingHandler optimisticLockingHandler;
    
//     /**
//      * 验证消息类型是否是队列成员消息
//      */
//     private boolean isQueueMemberMessage(Message message) {
//         try {
//             if (message.propertyExists("_type")) {
//                 String type = message.getStringProperty("_type");
//                 return "queueMemberMessage".equals(type);
//             }
//             return false;
//         } catch (Exception e) {
//             log.warn("验证消息类型时发生异常: {}", e.getMessage());
//             return false;
//         }
//     }
    
//     @Override
//     @JmsListener(destination = JmsArtemisConsts.QUEUE_MEMBER_UPDATE)
//     @Transactional
//     public void onMessage(Message message) {
//         String memberUid = "";
//         try {
//             // 验证消息类型
//             if (!isQueueMemberMessage(message)) {
//                 log.error("消息类型不是队列成员消息");
//                 message.acknowledge();
//                 return;
//             }
            
//             // 安全获取消息属性，避免空值异常
//             int deliveryCount = 0;
            
//             try {
//                 // 检查属性是否存在，并提供默认值
//                 if (message.propertyExists("JMSXDeliveryCount")) {
//                     deliveryCount = message.getIntProperty("JMSXDeliveryCount");
//                 }
                
//                 if (message.propertyExists("JMSXGroupID")) {
//                     String groupId = message.getStringProperty("JMSXGroupID");
//                     if (groupId != null && groupId.startsWith("member-")) {
//                         memberUid = groupId.substring(7); // 移除 "member-" 前缀
//                     }
//                 }
//             } catch (JMSException ex) {
//                 log.warn("获取消息属性异常，使用默认值: {}", ex.getMessage());
//             }
            
//             log.debug("收到队列成员消息，投递次数: {}, 成员ID: {}", deliveryCount, 
//                     (memberUid.isEmpty() ? "未知" : memberUid));
            
//             // 如果投递次数超过阈值，记录错误并确认消息
//             if (deliveryCount > 3) {
//                 log.error("消息投递次数超过阈值，放弃处理: {}", 
//                         (memberUid.isEmpty() ? "未知ID" : memberUid));
//                 message.acknowledge();
//                 return;
//             }
            
//             // 获取消息内容 - 从TextMessage中获取JSON字符串，然后转换为对象
//             String messageBody = message.getBody(String.class);
//             if (messageBody == null || messageBody.isEmpty()) {
//                 log.error("消息内容为空");
//                 message.acknowledge();
//                 return;
//             }
            
//             // 检查消息体中是否包含必要的数据特征 - 使用JSON解析器来验证字段存在性
//             try {
//                 com.alibaba.fastjson2.JSONObject jsonObj = com.alibaba.fastjson2.JSON.parseObject(messageBody);
//                 if (jsonObj == null || !jsonObj.containsKey("operationType") || !jsonObj.containsKey("memberUid")) {
//                     log.error("消息格式不正确，缺少必要字段: operationType或memberUid不存在，消息内容: {}", 
//                             messageBody.length() > 200 ? messageBody.substring(0, 200) + "..." : messageBody);
//                     message.acknowledge();
//                     return;
//                 }
//                 // 记录消息内容以便调试
//                 log.debug("消息内容验证通过，operationType={}, memberUid={}", 
//                         jsonObj.getString("operationType"), jsonObj.getString("memberUid"));
//             } catch (Exception e) {
//                 log.error("消息格式解析错误: {}, 消息内容: {}", e.getMessage(), 
//                         messageBody.length() > 200 ? messageBody.substring(0, 200) + "..." : messageBody);
//                 message.acknowledge();
//                 return;
//             }
            
//             // 使用JSON转换器解析消息内容为QueueMemberMessage对象
//             QueueMemberMessage queueMemberMessage;
//             try {
//                 // 预处理消息内容，移除可能的额外引号和转义字符
//                 String processedMessageBody = messageBody;
//                 // 检查消息是否被额外引号包裹，如果是就移除它们
//                 if (processedMessageBody.startsWith("\"") && processedMessageBody.endsWith("\"")) {
//                     // 移除首尾引号，并处理内部的转义字符
//                     processedMessageBody = processedMessageBody.substring(1, processedMessageBody.length() - 1);
//                     // 处理转义字符
//                     processedMessageBody = processedMessageBody.replace("\\\"", "\"")
//                                                             .replace("\\\\", "\\")
//                                                             .replace("\\/", "/");
//                 }
                
//                 log.debug("处理后的消息内容: {}", 
//                         processedMessageBody.length() > 200 ? processedMessageBody.substring(0, 200) + "..." : processedMessageBody);
                
//                 // 使用安全的方式解析JSON，不再使用已弃用的SupportAutoType
//                 queueMemberMessage = com.alibaba.fastjson2.JSON.parseObject(
//                     processedMessageBody, 
//                     QueueMemberMessage.class
//                 );
                
//                 if (queueMemberMessage == null) {
//                     log.error("无法解析队列成员消息内容");
//                     message.acknowledge();
//                     return;
//                 }
                
//                 // 验证解析后对象的关键字段
//                 if (queueMemberMessage.getMemberUid() == null || queueMemberMessage.getMemberUid().isEmpty()) {
//                     log.error("解析后的消息对象缺少memberUid字段");
//                     message.acknowledge();
//                     return;
//                 }
                
//                 if (queueMemberMessage.getOperationType() == null || queueMemberMessage.getOperationType().isEmpty()) {
//                     log.error("解析后的消息对象缺少operationType字段");
//                     message.acknowledge();
//                     return;
//                 }
                
//                 // 确保消息类型标记正确
//                 if (queueMemberMessage.get_type() == null) {
//                     // 如果消息中没有_type字段，手动设置它
//                     queueMemberMessage.set_type("queueMemberMessage");
//                 }
                
//                 // 详细记录消息内容，便于调试
//                 log.debug("消息解析成功: memberUid={}, operationType={}, _type={}, updateStats={}", 
//                         queueMemberMessage.getMemberUid(), 
//                         queueMemberMessage.getOperationType(),
//                         queueMemberMessage.get_type(),
//                         queueMemberMessage.getUpdateStats());
                
//                 // 记录更新字段信息
//                 if (queueMemberMessage.getUpdates() != null) {
//                     log.debug("更新字段: {}", queueMemberMessage.getUpdates().keySet());
//                 }
                
//             } catch (Exception e) {
//                 log.error("解析消息对象时发生异常: {}, 消息内容: {}", e.getMessage(), 
//                         messageBody.length() > 200 ? messageBody.substring(0, 200) + "..." : messageBody);
//                 message.acknowledge();
//                 return;
//             }
            
//             // 保存成员ID用于错误日志
//             memberUid = queueMemberMessage.getMemberUid();
            
//             // 根据操作类型处理消息
//             switch (queueMemberMessage.getOperationType()) {
//                 case "update":
//                     handleUpdate(queueMemberMessage);
//                     break;
//                 case "delete":
//                     handleDelete(queueMemberMessage);
//                     break;
//                 default:
//                     log.warn("未知的操作类型: {}", queueMemberMessage.getOperationType());
//             }
            
//             // 确认消息
//             message.acknowledge();
//             log.debug("消息处理完成: {}", message.getJMSMessageID());
            
//         } catch (jakarta.jms.MessageFormatException e) {
//             // 专门处理消息格式异常
//             log.error("消息格式错误，无法解析: {} - 错误: {}", 
//                     (memberUid.isEmpty() ? "未知ID" : memberUid), e.getMessage());
//             try {
//                 // 确认消息以避免无限重试
//                 message.acknowledge();
//             } catch (JMSException ex) {
//                 log.error("确认消息失败: {}", ex.getMessage());
//             }
//         } catch (JMSException e) {
//             log.error("处理队列成员消息时发生JMS异常: {} - 错误: {}", 
//                     (memberUid.isEmpty() ? "未知ID" : memberUid), e.getMessage(), e);
//         } catch (org.springframework.dao.OptimisticLockingFailureException e) {
//             // 特殊处理乐观锁异常
//             log.warn("队列成员更新时发生乐观锁冲突: {} - 将在下次投递时重试", 
//                     (memberUid.isEmpty() ? "未知ID" : memberUid));
//             // 不确认消息，让它重新投递
//         } catch (Exception e) {
//             log.error("处理队列成员消息时发生异常: {} - 错误类型: {}, 详细信息: {}", 
//                     (memberUid.isEmpty() ? "未知ID" : memberUid), 
//                     e.getClass().getName(), e.getMessage(), e);
//             try {
//                 // 确认消息以避免无限重试（除非是乐观锁异常）
//                 message.acknowledge();
//             } catch (JMSException ex) {
//                 log.error("确认消息失败: {}", ex.getMessage());
//             }
//         }
//     }
    
//     /**
//      * 处理更新操作
//      */
//     private void handleUpdate(QueueMemberMessage message) {
//         Optional<QueueMemberEntity> optionalMember = queueMemberRepository.findByUid(message.getMemberUid());
//         if (!optionalMember.isPresent()) {
//             log.warn("未找到队列成员: {}", message.getMemberUid());
//             return;
//         }
        
//         QueueMemberEntity member = optionalMember.get();
        
//         // 使用乐观锁处理器执行更新操作
//         optimisticLockingHandler.executeWithRetry(
//             () -> {
//                 // 更新消息计数
//                 if (message.getUpdateStats()) {
//                     updateMessageCounts(member, message);
//                 }
                
//                 // 更新其他字段
//                 Map<String, Object> updates = message.getUpdates();
//                 if (updates != null && !updates.isEmpty()) {
//                     updateFields(member, updates);
//                 }
                
//                 // 保存更新
//                 queueMemberRepository.save(member);
//                 return null;
//             },
//             "QueueMember",  // 实体名称
//             "update",       // 操作类型
//             member.getUid() // 实体ID
//         );
//     }
    
//     /**
//      * 处理删除操作
//      */
//     private void handleDelete(QueueMemberMessage message) {
//         Optional<QueueMemberEntity> optionalMember = queueMemberRepository.findByUid(message.getMemberUid());
//         if (!optionalMember.isPresent()) {
//             log.warn("未找到要删除的队列成员: {}", message.getMemberUid());
//             return;
//         }
        
//         // 使用乐观锁处理器执行删除操作
//         optimisticLockingHandler.executeWithRetry(
//             () -> {
//                 queueMemberRepository.delete(optionalMember.get());
//                 return null;
//             },
//             "QueueMember",  // 实体名称
//             "delete",       // 操作类型
//             message.getMemberUid() // 实体ID
//         );
//     }
    
//     /**
//      * 更新消息计数
//      */
//     private void updateMessageCounts(QueueMemberEntity member, QueueMemberMessage message) {
//         // 更新访客消息数
//         if (message.getVisitorMessageCount() != null) {
//             member.setVisitorMessageCount(message.getVisitorMessageCount());
//         }
        
//         // 更新客服消息数
//         if (message.getAgentMessageCount() != null) {
//             member.setAgentMessageCount(message.getAgentMessageCount());
//         }
        
//         // 更新机器人消息数
//         if (message.getRobotMessageCount() != null) {
//             member.setRobotMessageCount(message.getRobotMessageCount());
//         }
        
//         // 更新系统消息数
//         if (message.getSystemMessageCount() != null) {
//             member.setSystemMessageCount(message.getSystemMessageCount());
//         }
        
//         // 更新时间戳
//         if (message.getVisitorLastMessageAt() != null) {
//             member.setVisitorLastMessageAt(message.getVisitorLastMessageAt());
//         }
//         if (message.getAgentLastResponseAt() != null) {
//             member.setAgentLastResponseAt(message.getAgentLastResponseAt());
//         }
//         if (message.getRobotLastResponseAt() != null) {
//             member.setRobotLastResponseAt(message.getRobotLastResponseAt());
//         }
//     }
    
//     /**
//      * 更新其他字段
//      */
//     private void updateFields(QueueMemberEntity member, Map<String, Object> updates) {
//         updates.forEach((key, value) -> {
//             switch (key) {
//                 case "agentAcceptType":
//                     member.setAgentAcceptType((String) value);
//                     break;
//                 case "robotAcceptType":
//                     member.setRobotAcceptType((String) value);
//                     break;
//                 case "agentOffline":
//                     member.setAgentOffline((Boolean) value);
//                     break;
//                 case "robotToAgent":
//                     if (Boolean.TRUE.equals(value)) {
//                         // 执行完整的转人工逻辑
//                         member.transferRobotToAgent();
//                         // // 设置转人工状态
//                         // member.setTransferStatus(ThreadTransferStatusEnum.ROBOT_TO_AGENT.name());
//                         // // 设置机器人结束时间
//                         // member.setRobotClosedAt(BdDateUtils.now());
//                         // // 设置人工开始时间
//                         // member.setAgentAcceptedAt(BdDateUtils.now());
//                         // // 设置人工接入方式为自动
//                         // member.setAgentAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
//                     }
//                     break;
//                 case "rated":
//                     member.setRated((Boolean) value);
//                     break;
//                 case "resolved":
//                     member.setResolved((Boolean) value);
//                     break;
//                 case "qualityChecked":
//                     member.setQualityChecked((Boolean) value);
//                     break;
//                 case "qualityCheckScore":
//                     member.setQualityCheckScore((Integer) value);
//                     break;
//                 case "messageLeave":
//                     member.setMessageLeave((Boolean) value);
//                     break;
//                 case "summarized":
//                     member.setSummarized((Boolean) value);
//                     break;
//                 case "transferStatus":
//                     member.setTransferStatus((String) value);
//                     break;
//                 case "inviteStatus":
//                     member.setInviteStatus((String) value);
//                     break;
//                 case "intentionType":
//                     member.setIntentionType((String) value);
//                     break;
//                 case "emotionType":
//                     member.setEmotionType((String) value);
//                     break;
//                 case "agentAutoAcceptThread":
//                     if (Boolean.TRUE.equals(value)) {
//                         // 执行客服自动接受会话的逻辑
//                         member.agentAutoAcceptThread();
//                     }
//                     break;
//                 default:
//                     log.warn("未知的字段: {}", key);
//             }
//         });
//     }
// } 