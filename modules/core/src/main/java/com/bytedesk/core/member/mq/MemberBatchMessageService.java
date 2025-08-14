/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-01 15:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-01 10:47:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member.mq;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.jms.JmsArtemisConsts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.member.MemberExcel;

import lombok.extern.slf4j.Slf4j;

/**
 * Member批量导入消息服务
 * 参考FAQ的异步处理模式，用于发送Member批量导入消息到消息队列
 */
@Slf4j
@Service
public class MemberBatchMessageService {

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 发送批量导入消息
     * 将Member列表分批发送到消息队列，避免大批量数据的并发冲突
     * 
     * @param memberExcelList Member Excel数据列表
     * @param orgUid 组织唯一标识
     */
    public void sendBatchImportMessages(List<MemberExcel> memberExcelList, String orgUid) {
        if (memberExcelList == null || memberExcelList.isEmpty()) {
            log.warn("Member列表为空，无需发送批量导入消息");
            return;
        }
        
        // 安全检查 - 确保orgUid不为空
        if (orgUid == null || orgUid.isEmpty()) {
            log.error("发送批量导入消息失败：组织UID为空");
            return;
        }

        String batchUid = UidUtils.getInstance().getUid();
        int total = memberExcelList.size();
        AtomicInteger index = new AtomicInteger(0);

        log.info("开始发送Member批量导入消息，批次ID: {}, 总数: {}", batchUid, total);

        // 分批发送消息，每个Member一个消息，避免大批量并发
        memberExcelList.forEach(memberExcel -> {
            int currentIndex = index.incrementAndGet();
            boolean isLastBatch = currentIndex == total;

            try {
                // 创建批量导入消息
                MemberBatchMessage message = MemberBatchMessage.builder()
                        .batchUid(batchUid)
                        .operationType("batch_import")
                        .memberExcelJson(JSON.toJSONString(memberExcel))
                        .orgUid(orgUid)
                        .batchIndex(currentIndex)
                        .batchTotal(total)
                        .isLastBatch(isLastBatch)
                        .retryCount(0)
                        .createTimestamp(System.currentTimeMillis())
                        .build();

                // 创建消息后置处理器，设置消息属性
                org.springframework.jms.core.MessagePostProcessor postProcessor = jmsMessage -> {
                    // 添加随机延迟，避免消息同时到达造成并发冲突
                    // 每个消息间隔50-200ms，错峰处理
                    long baseDelay = System.currentTimeMillis() + (currentIndex * 100L);
                    long randomDelay = baseDelay + new java.util.Random().nextInt(150);
                    jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", randomDelay);

                    // 设置消息的重试策略
                    jmsMessage.setIntProperty("JMSXDeliveryCount", 0);
                    jmsMessage.setBooleanProperty("_AMQ_ORIG_MESSAGE", true);

                    // 设置消息分组，确保相同批次的消息按顺序处理
                    jmsMessage.setStringProperty("JMSXGroupID", "member-batch-" + batchUid);

                    // 设置消息优先级（正常优先级）
                    jmsMessage.setJMSPriority(5);

                    // 设置消息类型标识符
                    jmsMessage.setStringProperty("_type", "memberBatchMessage");
                    
                    // 设置消息属性，便于监控和调试
                    jmsMessage.setStringProperty("batchUid", batchUid);
                    jmsMessage.setStringProperty("orgUid", orgUid);
                    jmsMessage.setIntProperty("batchIndex", currentIndex);
                    jmsMessage.setIntProperty("batchTotal", total);
                    jmsMessage.setBooleanProperty("isLastBatch", isLastBatch);

                    return jmsMessage;
                };

                // 发送消息到队列
                jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_MEMBER_BATCH_IMPORT, message, postProcessor);

                log.debug("Member批量导入消息已发送: 批次{}, 索引{}/{}", 
                         batchUid, currentIndex, total);

            } catch (Exception e) {
                log.error("发送Member批量导入消息失败: 批次{}, 索引{}/{}, 错误: {}", 
                         batchUid, currentIndex, total, e.getMessage(), e);
            }
        });

        log.info("Member批量导入消息发送完成，批次ID: {}, 总数: {}", batchUid, total);
    }

    /**
     * 发送重试消息
     * 当Member创建失败时，重新发送到队列进行重试
     * 
     * @param originalMessage 原始消息
     * @param retryDelay 重试延迟（毫秒）
     */
    public void sendRetryMessage(MemberBatchMessage originalMessage, long retryDelay) {
        try {
            // 安全检查 - 确保关键字段不为null
            if (originalMessage.getBatchUid() == null) {
                log.error("重试消息缺少批次UID，无法发送");
                return;
            }
            
            if (originalMessage.getOrgUid() == null) {
                log.error("重试消息缺少组织UID，无法发送: 批次{}, 索引{}", 
                        originalMessage.getBatchUid(), originalMessage.getBatchIndex());
                return;
            }
            
            // 增加重试次数
            Integer retryCount = originalMessage.getRetryCount();
            originalMessage.setRetryCount(retryCount != null ? retryCount + 1 : 1);

            // 创建消息后置处理器
            org.springframework.jms.core.MessagePostProcessor postProcessor = jmsMessage -> {
                // 设置延迟投递
                long deliveryTime = System.currentTimeMillis() + retryDelay;
                jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", deliveryTime);

                // 更新重试相关属性
                jmsMessage.setIntProperty("JMSXDeliveryCount", originalMessage.getRetryCount());
                jmsMessage.setStringProperty("JMSXGroupID", "member-batch-retry-" + originalMessage.getBatchUid());

                // 设置消息类型标识符
                jmsMessage.setStringProperty("_type", "memberBatchMessage");
                
                // 降低重试消息的优先级
                jmsMessage.setJMSPriority(3);
                
                // 重新设置所有必要的属性，确保它们在消费者端可用
                jmsMessage.setStringProperty("batchUid", originalMessage.getBatchUid());
                jmsMessage.setStringProperty("orgUid", originalMessage.getOrgUid());
                jmsMessage.setIntProperty("batchIndex", originalMessage.getBatchIndex());
                jmsMessage.setIntProperty("batchTotal", originalMessage.getBatchTotal());
                jmsMessage.setBooleanProperty("isLastBatch", originalMessage.getIsLastBatch());

                return jmsMessage;
            };

            // 重新发送到队列
            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_MEMBER_BATCH_IMPORT, originalMessage, postProcessor);

            log.info("Member批量导入重试消息已发送: 批次{}, 索引{}, 重试次数{}, 延迟{}ms",
                    originalMessage.getBatchUid(), originalMessage.getBatchIndex(), 
                    originalMessage.getRetryCount(), retryDelay);

        } catch (Exception e) {
            log.error("发送Member批量导入重试消息失败: 批次{}, 索引{}, 错误: {}",
                     originalMessage.getBatchUid(), originalMessage.getBatchIndex(), e.getMessage(), e);
        }
    }
}
