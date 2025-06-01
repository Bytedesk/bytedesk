/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-01 15:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-01 09:54:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.jms.JmsArtemisConstants;
import com.bytedesk.team.member.MemberEntity;
import com.bytedesk.team.member.MemberExcel;
import com.bytedesk.team.member.MemberRestService;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import lombok.extern.slf4j.Slf4j;

/**
 * Member批量导入消息消费者
 * 参考FAQ的异步处理模式，用于处理Member批量导入消息
 */
@Slf4j
@Component
public class MemberBatchConsumer implements MessageListener {

    @Autowired
    private MemberRestService memberRestService;

    @Autowired
    private MemberBatchMessageService memberBatchMessageService;

    /** 最大重试次数 */
    private static final int MAX_RETRY_COUNT = 3;
    
    /** 重试延迟基数（毫秒） */
    private static final long BASE_RETRY_DELAY = 2000L;

    @Override
    @JmsListener(destination = JmsArtemisConstants.QUEUE_MEMBER_BATCH_IMPORT, containerFactory = "jmsArtemisQueueFactory")
    @Transactional
    public void onMessage(Message message) {
        try {
            // 获取消息投递次数
            int deliveryCount = message.getIntProperty("JMSXDeliveryCount");
            String batchUid = message.getStringProperty("batchUid");
            int batchIndex = message.getIntProperty("batchIndex");
            
            log.debug("收到Member批量导入消息: 批次{}, 索引{}, 投递次数{}", batchUid, batchIndex, deliveryCount);

            // 如果投递次数超过阈值，记录错误并确认消息
            if (deliveryCount > MAX_RETRY_COUNT) {
                log.error("Member批量导入消息投递次数超过阈值，放弃处理: 批次{}, 索引{}", batchUid, batchIndex);
                message.acknowledge();
                return;
            }

            // 获取消息内容
            MemberBatchMessage batchMessage = message.getBody(MemberBatchMessage.class);
            if (batchMessage == null) {
                log.error("Member批量导入消息内容为空: 批次{}", batchUid);
                message.acknowledge();
                return;
            }

            // 处理批量导入
            boolean success = processBatchImport(batchMessage);
            
            if (success) {
                // 处理成功，确认消息
                message.acknowledge();
                log.debug("Member批量导入消息处理成功: 批次{}, 索引{}/{}", 
                         batchUid, batchMessage.getBatchIndex(), batchMessage.getBatchTotal());
                
                // 如果是最后一个批次，记录完成日志
                if (Boolean.TRUE.equals(batchMessage.getIsLastBatch())) {
                    log.info("Member批量导入完成: 批次{}, 总数{}", batchUid, batchMessage.getBatchTotal());
                }
            } else {
                // 处理失败，根据重试策略决定是否重试
                handleProcessingFailure(batchMessage, message);
            }

        } catch (JMSException e) {
            log.error("处理Member批量导入消息时发生JMS异常: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("处理Member批量导入消息时发生异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理Member批量导入
     */
    private boolean processBatchImport(MemberBatchMessage batchMessage) {
        try {
            // 解析Member Excel数据
            MemberExcel memberExcel = JSON.parseObject(batchMessage.getMemberExcelJson(), MemberExcel.class);
            if (memberExcel == null) {
                log.error("无法解析Member Excel数据: 批次{}, 索引{}", 
                         batchMessage.getBatchUid(), batchMessage.getBatchIndex());
                return false;
            }

            // 转换为Member实体
            MemberEntity member = memberRestService.convertExcelToMember(memberExcel, batchMessage.getOrgUid());
            if (member == null) {
                log.error("无法转换Member实体: 批次{}, 索引{}", 
                         batchMessage.getBatchUid(), batchMessage.getBatchIndex());
                return false;
            }

            // 保存Member（单个保存，避免批量冲突）
            MemberEntity savedMember = memberRestService.save(member);
            if (savedMember != null) {
                log.debug("Member创建成功: 批次{}, 索引{}, UID{}", 
                         batchMessage.getBatchUid(), batchMessage.getBatchIndex(), savedMember.getUid());
                return true;
            } else {
                log.error("Member保存失败: 批次{}, 索引{}", 
                         batchMessage.getBatchUid(), batchMessage.getBatchIndex());
                return false;
            }

        } catch (OptimisticLockingFailureException e) {
            // 乐观锁异常，这是我们要解决的主要问题
            log.warn("Member批量导入遇到乐观锁异常: 批次{}, 索引{}, 错误: {}", 
                    batchMessage.getBatchUid(), batchMessage.getBatchIndex(), e.getMessage());
            return false;
            
        } catch (Exception e) {
            log.error("Member批量导入处理失败: 批次{}, 索引{}, 错误: {}", 
                     batchMessage.getBatchUid(), batchMessage.getBatchIndex(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 处理失败的处理逻辑
     */
    private void handleProcessingFailure(MemberBatchMessage batchMessage, Message message) throws JMSException {
        int currentRetryCount = batchMessage.getRetryCount();
        
        if (currentRetryCount < MAX_RETRY_COUNT) {
            // 还可以重试，计算重试延迟（指数退避）
            long retryDelay = BASE_RETRY_DELAY * (long) Math.pow(2, currentRetryCount);
            
            log.info("Member批量导入失败，准备重试: 批次{}, 索引{}, 重试次数{}, 延迟{}ms",
                    batchMessage.getBatchUid(), batchMessage.getBatchIndex(), 
                    currentRetryCount + 1, retryDelay);
            
            // 发送重试消息
            memberBatchMessageService.sendRetryMessage(batchMessage, retryDelay);
            
            // 确认当前消息（避免重复处理）
            message.acknowledge();
            
        } else {
            // 重试次数已达上限，记录错误并确认消息
            log.error("Member批量导入重试次数达到上限，放弃处理: 批次{}, 索引{}, 重试次数{}",
                     batchMessage.getBatchUid(), batchMessage.getBatchIndex(), currentRetryCount);
            
            message.acknowledge();
        }
    }
}
