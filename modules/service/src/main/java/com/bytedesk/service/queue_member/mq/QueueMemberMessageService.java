/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-30 11:05:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-02 10:04:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member.mq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.jms.JmsArtemisConstants;
import com.bytedesk.service.queue_member.QueueMemberEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * 队列成员消息服务
 * 用于发送队列成员更新消息到消息队列
 */
@Slf4j
@Service
public class QueueMemberMessageService {

    @Autowired
    private JmsTemplate jmsTemplate;
    
    /**
     * 发送队列成员更新消息
     * 
     * @param member 队列成员实体
     * @param updates 需要更新的字段
     */
    public void sendUpdateMessage(QueueMemberEntity member, Map<String, Object> updates) {
        try {
            log.debug("发送队列成员更新消息: {}", member.getUid());
            
            // 创建消息对象
            QueueMemberMessage message = QueueMemberMessage.builder()
                    .memberUid(member.getUid())
                    .operationType("update")
                    .updates(updates)
                    .updateStats(true)
                    ._type("queueMemberMessage") // 确保设置了消息类型标记
                    .build();
                    
            // 处理特殊字段 - 从updates复制到消息对象中
            if (updates != null) {
                // 针对常用字段进行特殊处理
                if (updates.containsKey("agentAutoAcceptThread")) {
                    message.setAgentAutoAcceptThread((Boolean) updates.get("agentAutoAcceptThread"));
                }
                
                if (updates.containsKey("robotToAgent")) {
                    message.setRobotToAgent((Boolean) updates.get("robotToAgent"));
                }
                
                // 复制所有其他字段，确保消息包含完整信息
                for (Map.Entry<String, Object> entry : updates.entrySet()) {
                    // 根据字段名称设置相应的属性
                    switch(entry.getKey()) {
                        case "agentAcceptType":
                            message.setAgentAcceptType((String) entry.getValue());
                            break;
                        case "robotAcceptType":
                            message.setRobotAcceptType((String) entry.getValue());
                            break;
                        case "transferStatus":
                            message.setTransferStatus((String) entry.getValue());
                            break;
                        case "inviteStatus":
                            message.setInviteStatus((String) entry.getValue());
                            break;
                        case "rated":
                            message.setRated((Boolean) entry.getValue());
                            break;
                        case "resolved":
                            message.setResolved((Boolean) entry.getValue());
                            break;
                        // 其他字段也可以类似处理
                    }
                }
            }
                    
            // 将对象转换为JSON字符串，确保所有字段都被序列化
            String messageJson = com.alibaba.fastjson2.JSON.toJSONString(message, 
                com.alibaba.fastjson2.JSONWriter.Feature.WriteMapNullValue,
                com.alibaba.fastjson2.JSONWriter.Feature.WriteNulls);
            
            // 记录发送的消息内容，便于排查问题
            log.debug("发送的更新消息内容: {}", messageJson);
            
            // 创建一个消息后置处理器，设置消息属性
            org.springframework.jms.core.MessagePostProcessor postProcessor = jmsMessage -> {
                // 添加随机小延迟（0-300ms），避免消息同时到达造成冲突
                long randomDelay = System.currentTimeMillis() + new java.util.Random().nextInt(300);
                jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", randomDelay);
                
                // 设置消息的重试策略
                jmsMessage.setIntProperty("JMSXDeliveryCount", 0); // 初始投递次数
                jmsMessage.setBooleanProperty("_AMQ_ORIG_MESSAGE", true); // 标记为原始消息
                
                // 设置消息分组，确保相同成员的消息按顺序处理
                jmsMessage.setStringProperty("JMSXGroupID", "member-" + member.getUid());
                
                // 添加消息类型标记
                jmsMessage.setStringProperty("_type", "queueMemberMessage");
                
                return jmsMessage;
            };
            
            // 发送JSON字符串而不是对象
            jmsTemplate.convertAndSend(JmsArtemisConstants.QUEUE_MEMBER_UPDATE, messageJson, postProcessor);
            log.debug("消息已发送到队列: {}", JmsArtemisConstants.QUEUE_MEMBER_UPDATE);
        } catch (Exception e) {
            log.error("发送队列成员更新消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送队列成员删除消息
     * 
     * @param memberUid 队列成员唯一标识
     */
    public void sendDeleteMessage(String memberUid) {
        try {
            log.debug("发送队列成员删除消息: {}", memberUid);
            
            QueueMemberMessage message = QueueMemberMessage.builder()
                    .memberUid(memberUid)
                    .operationType("delete")
                    ._type("queueMemberMessage") // 确保设置了消息类型标记
                    .build();
                    
            // 将对象转换为JSON字符串，确保所有字段都被序列化
            String messageJson = com.alibaba.fastjson2.JSON.toJSONString(message, 
                com.alibaba.fastjson2.JSONWriter.Feature.WriteMapNullValue,
                com.alibaba.fastjson2.JSONWriter.Feature.WriteNulls);
                
            // 记录发送的消息内容，便于排查问题
            log.debug("发送的删除消息内容: {}", messageJson);
            
            // 创建一个消息后置处理器，设置消息属性
            org.springframework.jms.core.MessagePostProcessor postProcessor = jmsMessage -> {
                // 添加随机小延迟（100-400ms），避免消息同时到达造成冲突
                long randomDelay = System.currentTimeMillis() + 100 + new java.util.Random().nextInt(300);
                jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", randomDelay);
                
                // 设置消息的重试策略
                jmsMessage.setIntProperty("JMSXDeliveryCount", 0);
                jmsMessage.setBooleanProperty("_AMQ_ORIG_MESSAGE", true);
                
                // 设置消息优先级，删除操作优先级略低
                jmsMessage.setJMSPriority(4);
                
                // 设置消息分组
                jmsMessage.setStringProperty("JMSXGroupID", "member-" + memberUid);
                
                // 添加消息类型标记
                jmsMessage.setStringProperty("_type", "queueMemberMessage");
                
                return jmsMessage;
            };
            
            // 发送JSON字符串而不是对象
            jmsTemplate.convertAndSend(JmsArtemisConstants.QUEUE_MEMBER_UPDATE, messageJson, postProcessor);
            log.debug("删除消息已发送到队列: {}", JmsArtemisConstants.QUEUE_MEMBER_UPDATE);
        } catch (Exception e) {
            log.error("发送队列成员删除消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送消息计数更新消息
     * 
     * @param member 队列成员实体
     */
    public void sendMessageCountUpdate(QueueMemberEntity member) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("visitorMessageCount", member.getVisitorMessageCount());
        updates.put("agentMessageCount", member.getAgentMessageCount());
        updates.put("robotMessageCount", member.getRobotMessageCount());
        updates.put("systemMessageCount", member.getSystemMessageCount());
        sendUpdateMessage(member, updates);
    }
    
    /**
     * 发送时间戳更新消息
     * 
     * @param member 队列成员实体
     */
    public void sendTimestampUpdate(QueueMemberEntity member) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("visitorLastMessageAt", member.getVisitorLastMessageAt());
        updates.put("agentLastResponseAt", member.getAgentLastResponseAt());
        updates.put("robotLastResponseAt", member.getRobotLastResponseAt());
        // updates.put("workgroupLastResponseAt", member.getWorkgroupLastResponseAt());
        sendUpdateMessage(member, updates);
    }
    
    /**
     * 发送状态更新消息
     * 
     * @param member 队列成员实体
     */
    public void sendStatusUpdate(QueueMemberEntity member) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("agentAcceptType", member.getAgentAcceptType());
        updates.put("robotAcceptType", member.getRobotAcceptType());
        // updates.put("workgroupAcceptType", member.getWorkgroupAcceptType());
        updates.put("agentOffline", member.getAgentOffline());
        updates.put("robotToAgent", member.getRobotToAgent());
        updates.put("rated", member.getRated());
        // updates.put("rateLevel", member.getRateLevel());
        updates.put("resolved", member.getResolved());
        updates.put("qualityChecked", member.getQualityChecked());
        updates.put("qualityCheckResult", member.getQualityCheckResult());
        updates.put("messageLeave", member.getMessageLeave());
        // updates.put("leaveMsg", member.getLeaveMsg());
        updates.put("summarized", member.getSummarized());
        updates.put("transferStatus", member.getTransferStatus());
        updates.put("inviteStatus", member.getInviteStatus());
        updates.put("intentionType", member.getIntentionType());
        updates.put("emotionType", member.getEmotionType());
        sendUpdateMessage(member, updates);
    }
} 