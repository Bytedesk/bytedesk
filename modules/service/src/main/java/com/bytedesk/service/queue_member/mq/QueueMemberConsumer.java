/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-30 11:05:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-30 12:00:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member.mq;

import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.jms.JmsArtemisConstants;
import com.bytedesk.core.thread.ThreadTransferStatusEnum;
import com.bytedesk.core.utils.OptimisticLockingHandler;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRepository;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 队列成员消息消费者
 * 用于处理队列成员更新消息
 */
@Slf4j
@Component
public class QueueMemberConsumer implements MessageListener {

    @Autowired
    private QueueMemberRepository queueMemberRepository;
    
    @Autowired
    private OptimisticLockingHandler optimisticLockingHandler;
    
    @Override
    @JmsListener(destination = JmsArtemisConstants.QUEUE_MEMBER_UPDATE)
    @Transactional
    public void onMessage(Message message) {
        try {
            // 获取消息投递次数
            int deliveryCount = message.getIntProperty("JMSXDeliveryCount");
            log.debug("收到队列成员消息，投递次数: {}", deliveryCount);
            
            // 如果投递次数超过阈值，记录错误并确认消息
            if (deliveryCount > 3) {
                log.error("消息投递次数超过阈值，放弃处理: {}", message.getJMSMessageID());
                message.acknowledge();
                return;
            }
            
            // 获取消息内容
            QueueMemberMessage queueMemberMessage = message.getBody(QueueMemberMessage.class);
            if (queueMemberMessage == null) {
                log.error("消息内容为空");
                message.acknowledge();
                return;
            }
            
            // 根据操作类型处理消息
            switch (queueMemberMessage.getOperationType()) {
                case "update":
                    handleUpdate(queueMemberMessage);
                    break;
                case "delete":
                    handleDelete(queueMemberMessage);
                    break;
                default:
                    log.warn("未知的操作类型: {}", queueMemberMessage.getOperationType());
            }
            
            // 确认消息
            message.acknowledge();
            log.debug("消息处理完成: {}", message.getJMSMessageID());
            
        } catch (JMSException e) {
            log.error("处理消息时发生JMS异常: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("处理消息时发生异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理更新操作
     */
    private void handleUpdate(QueueMemberMessage message) {
        Optional<QueueMemberEntity> optionalMember = queueMemberRepository.findByUid(message.getMemberUid());
        if (!optionalMember.isPresent()) {
            log.warn("未找到队列成员: {}", message.getMemberUid());
            return;
        }
        
        QueueMemberEntity member = optionalMember.get();
        
        // 使用乐观锁处理器执行更新操作
        optimisticLockingHandler.executeWithRetry(
            () -> {
                // 更新消息计数
                if (message.getUpdateStats()) {
                    updateMessageCounts(member, message);
                }
                
                // 更新其他字段
                Map<String, Object> updates = message.getUpdates();
                if (updates != null && !updates.isEmpty()) {
                    updateFields(member, updates);
                }
                
                // 保存更新
                queueMemberRepository.save(member);
                return null;
            },
            "QueueMember",  // 实体名称
            "update",       // 操作类型
            member.getUid() // 实体ID
        );
    }
    
    /**
     * 处理删除操作
     */
    private void handleDelete(QueueMemberMessage message) {
        Optional<QueueMemberEntity> optionalMember = queueMemberRepository.findByUid(message.getMemberUid());
        if (!optionalMember.isPresent()) {
            log.warn("未找到要删除的队列成员: {}", message.getMemberUid());
            return;
        }
        
        // 使用乐观锁处理器执行删除操作
        optimisticLockingHandler.executeWithRetry(
            () -> {
                queueMemberRepository.delete(optionalMember.get());
                return null;
            },
            "QueueMember",  // 实体名称
            "delete",       // 操作类型
            message.getMemberUid() // 实体ID
        );
    }
    
    /**
     * 更新消息计数
     */
    private void updateMessageCounts(QueueMemberEntity member, QueueMemberMessage message) {
        // 更新访客消息数
        if (message.getVisitorMessageCount() != null) {
            member.setVisitorMessageCount(message.getVisitorMessageCount());
        }
        
        // 更新客服消息数
        if (message.getAgentMessageCount() != null) {
            member.setAgentMessageCount(message.getAgentMessageCount());
        }
        
        // 更新机器人消息数
        if (message.getRobotMessageCount() != null) {
            member.setRobotMessageCount(message.getRobotMessageCount());
        }
        
        // 更新系统消息数
        if (message.getSystemMessageCount() != null) {
            member.setSystemMessageCount(message.getSystemMessageCount());
        }
        
        // 更新时间戳
        if (message.getVisitorLastMessageAt() != null) {
            member.setVisitorLastMessageAt(message.getVisitorLastMessageAt());
        }
        if (message.getAgentLastResponseAt() != null) {
            member.setAgentLastResponseAt(message.getAgentLastResponseAt());
        }
        if (message.getRobotLastResponseAt() != null) {
            member.setRobotLastResponseAt(message.getRobotLastResponseAt());
        }
    }
    
    /**
     * 更新其他字段
     */
    private void updateFields(QueueMemberEntity member, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "agentAcceptType":
                    member.setAgentAcceptType((String) value);
                    break;
                case "robotAcceptType":
                    member.setRobotAcceptType((String) value);
                    break;
                case "agentOffline":
                    member.setAgentOffline((Boolean) value);
                    break;
                case "robotToAgent":
                    if (Boolean.TRUE.equals(value)) {
                        // 执行完整的转人工逻辑
                        member.transferRobotToAgent();
                        // // 设置转人工状态
                        // member.setTransferStatus(ThreadTransferStatusEnum.ROBOT_TO_AGENT.name());
                        // // 设置机器人结束时间
                        // member.setRobotClosedAt(LocalDateTime.now());
                        // // 设置人工开始时间
                        // member.setAgentAcceptedAt(LocalDateTime.now());
                        // // 设置人工接入方式为自动
                        // member.setAgentAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
                    }
                    break;
                case "rated":
                    member.setRated((Boolean) value);
                    break;
                case "resolved":
                    member.setResolved((Boolean) value);
                    break;
                case "qualityChecked":
                    member.setQualityChecked((Boolean) value);
                    break;
                case "qualityCheckResult":
                    member.setQualityCheckResult((String) value);
                    break;
                case "messageLeave":
                    member.setMessageLeave((Boolean) value);
                    break;
                case "summarized":
                    member.setSummarized((Boolean) value);
                    break;
                case "transferStatus":
                    member.setTransferStatus((String) value);
                    break;
                case "inviteStatus":
                    member.setInviteStatus((String) value);
                    break;
                case "intentionType":
                    member.setIntentionType((String) value);
                    break;
                case "emotionType":
                    member.setEmotionType((String) value);
                    break;
                default:
                    log.warn("未知的字段: {}", key);
            }
        });
    }
} 