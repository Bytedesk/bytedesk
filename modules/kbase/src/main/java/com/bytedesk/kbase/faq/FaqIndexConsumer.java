/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 10:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 10:10:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.util.Optional;
import java.util.Random;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.jms.JmsArtemisConstants;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FAQ索引消费者
 * 用于处理FAQ索引队列中的消息
 */
@Slf4j
@Component
@AllArgsConstructor
public class FaqIndexConsumer {

    private final FaqElasticService faqElasticService;
    private final FaqVectorService faqVectorService;
    private final FaqRestService faqRestService;
    private final Random random = new Random();

    /**
     * 处理FAQ索引队列中的消息
     * 使用客户端确认模式，只有成功处理后才确认消息
     * 
     * @param jmsMessage JMS原始消息
     * @param message FAQ索引消息
     */
    @JmsListener(destination = JmsArtemisConstants.QUEUE_FAQ_INDEX, containerFactory = "jmsArtemisQueueFactory", concurrency = "3-10")
    public void processIndexMessage(jakarta.jms.Message jmsMessage, FaqIndexMessage message) {
        boolean success = false;
        try {
            log.debug("接收到FAQ索引请求: {}", message.getFaqUid());
            
            // 引入随机延迟，避免并发冲突
            int delay = 50 + random.nextInt(200); // 50-250ms随机延迟
            Thread.sleep(delay);
            
            // 获取FAQ实体
            Optional<FaqEntity> optionalFaq = faqRestService.findByUid(message.getFaqUid());
            if (!optionalFaq.isPresent()) {
                log.warn("无法找到要索引的FAQ: {}", message.getFaqUid());
                // 消息处理完成，但没有找到实体，也认为是成功的（避免重复处理已删除的实体）
                success = true;
                return;
            }
            
            FaqEntity faq = optionalFaq.get();
            
            // 根据操作类型执行相应的操作
            if ("delete".equals(message.getOperationType())) {
                handleDeleteOperation(faq, message);
            } else {
                handleIndexOperation(faq, message);
            }
            
            // 成功处理消息
            success = true;
            
        } catch (Exception e) {
            log.error("处理FAQ索引消息时出错: {}", e.getMessage(), e);
            success = false; // 发生异常，处理失败
        } finally {
            // 只有成功处理消息后才确认
            if (success) {
                acknowledgeMessage(jmsMessage);
            } else {
                log.warn("FAQ索引消息处理失败，不确认消息，等待重新处理: {}", message.getFaqUid());
            }
    }
    
    /**
     * 处理索引操作
     * 如果索引操作失败，将抛出异常以便于消息重试
     */
    private void handleIndexOperation(FaqEntity faq, FaqIndexMessage message) {
        boolean elasticSuccess = true;
        boolean vectorSuccess = true;
        Exception elasticException = null;
        Exception vectorException = null;
        
        // 执行全文索引
        if (message.isUpdateElasticIndex()) {
            try {
                log.debug("为FAQ创建全文索引: {}", faq.getUid());
                faqElasticService.indexFaq(faq);
            } catch (Exception e) {
                log.error("FAQ全文索引创建失败: {}, 错误: {}", faq.getUid(), e.getMessage(), e);
                elasticSuccess = false;
                elasticException = e;
            }
        }
        
        // 执行向量索引
        if (message.isUpdateVectorIndex()) {
            try {
                log.debug("为FAQ创建向量索引: {}", faq.getUid());
                // 先尝试删除旧的向量索引
                faqVectorService.deleteFaqVector(faq);
                // 创建新的向量索引
                faqVectorService.indexFaqVector(faq);
            } catch (Exception e) {
                log.error("FAQ向量索引创建失败: {}, 错误: {}", faq.getUid(), e.getMessage(), e);
                vectorSuccess = false;
                vectorException = e;
            }
        }
        
        // 如果有任何索引失败，抛出异常以便于消息重试
        if (!elasticSuccess || !vectorSuccess) {
            StringBuilder errorMessage = new StringBuilder("FAQ索引失败: ");
            if (!elasticSuccess) {
                errorMessage.append("全文索引错误");
            }
            if (!vectorSuccess) {
                if (!elasticSuccess) {
                    errorMessage.append(" 和 ");
                }
                errorMessage.append("向量索引错误");
            }
            
            // 抛出异常，阻止消息确认，系统将重新投递消息
            throw new RuntimeException(errorMessage.toString(), 
                    !elasticSuccess ? elasticException : vectorException);
        }
    }
    
    /**
     * 处理删除操作
     */
    private void handleDeleteOperation(FaqEntity faq, FaqIndexMessage message) {
        // 从全文索引中删除
        if (message.isUpdateElasticIndex()) {
            try {
                log.debug("从全文索引中删除FAQ: {}", faq.getUid());
                boolean deleted = faqElasticService.deleteFaq(faq.getUid());
                if (!deleted) {
                    log.warn("从Elasticsearch中删除FAQ全文索引失败: {}", faq.getUid());
                }
            } catch (Exception e) {
                log.error("从全文索引中删除FAQ失败: {}, 错误: {}", faq.getUid(), e.getMessage(), e);
                throw new RuntimeException("全文索引删除失败", e); // 抛出异常以便于消息重试
            }
        }
        
        // 从向量索引中删除
        if (message.isUpdateVectorIndex()) {
            try {
                log.debug("从向量索引中删除FAQ: {}", faq.getUid());
                boolean vectorDeleted = faqVectorService.deleteFaqVector(faq);
                if (!vectorDeleted) {
                    log.warn("从向量存储中删除FAQ索引失败: {}", faq.getUid());
                }
            } catch (Exception e) {
                log.error("从向量索引中删除FAQ失败: {}, 错误: {}", faq.getUid(), e.getMessage(), e);
                throw new RuntimeException("向量索引删除失败", e); // 抛出异常以便于消息重试
            }
        }
    }
    
    /**
     * 安全地确认消息
     * 只有在消息处理成功后才调用此方法
     *
     * @param message JMS消息
     */
    private void acknowledgeMessage(jakarta.jms.Message message) {
        try {
            if (message != null) {
                message.acknowledge();
                log.debug("消息已确认");
            }
        } catch (jakarta.jms.JMSException e) {
            log.error("确认消息失败: {}", e.getMessage(), e);
        }
    }
}
