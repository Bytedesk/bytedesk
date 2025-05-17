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
     * 
     * @param message FAQ索引消息
     */
    @JmsListener(destination = "bytedesk.faq.index", containerFactory = "jmsArtemisQueueFactory", concurrency = "3-10")
    public void processIndexMessage(FaqIndexMessage message) {
        try {
            log.debug("接收到FAQ索引请求: {}", message.getFaqUid());
            
            // 引入随机延迟，避免并发冲突
            int delay = 50 + random.nextInt(200); // 50-250ms随机延迟
            Thread.sleep(delay);
            
            // 获取FAQ实体
            Optional<FaqEntity> optionalFaq = faqRestService.findByUid(message.getFaqUid());
            if (!optionalFaq.isPresent()) {
                log.warn("无法找到要索引的FAQ: {}", message.getFaqUid());
                return;
            }
            
            FaqEntity faq = optionalFaq.get();
            
            // 根据操作类型执行相应的操作
            if ("delete".equals(message.getOperationType())) {
                handleDeleteOperation(faq, message);
            } else {
                handleIndexOperation(faq, message);
            }
            
        } catch (Exception e) {
            log.error("处理FAQ索引消息时出错: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理索引操作
     */
    private void handleIndexOperation(FaqEntity faq, FaqIndexMessage message) {
        // 执行全文索引
        if (message.isUpdateElasticIndex()) {
            try {
                log.debug("为FAQ创建全文索引: {}", faq.getUid());
                faqElasticService.indexFaq(faq);
            } catch (Exception e) {
                log.error("FAQ全文索引创建失败: {}, 错误: {}", faq.getUid(), e.getMessage(), e);
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
            }
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
            }
        }
    }
}
