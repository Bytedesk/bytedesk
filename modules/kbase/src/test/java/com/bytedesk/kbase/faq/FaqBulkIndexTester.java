/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 11:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 09:12:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.time.ZonedDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bytedesk.kbase.faq.mq.FaqMessageService;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * FAQ索引批量测试工具
 * 用于模拟批量处理FAQ创建事件，测试消息队列的有效性
 */
@Slf4j
@Component
public class FaqBulkIndexTester {
    
    @Autowired
    private FaqMessageService faqMessageService;
    
    @Autowired
    private FaqRepository faqRepository;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    /**
     * 测试批量发送FAQ到索引队列
     * 每个FAQ都分别发送一条索引消息
     * 
     * @param faqCount 测试的FAQ数量
     */
    public void testBulkFaqIndexing(int faqCount) {
        log.info("开始测试批量FAQ索引, 数量: {}", faqCount);
        
        // 从数据库获取前N个FAQ
        var faqs = faqRepository.findAll().stream().limit(faqCount).toList();
        if (faqs.isEmpty()) {
            log.warn("没有找到FAQ记录，无法进行批量索引测试");
            return;
        }
        
        log.info("共找到{}个FAQ记录用于测试", faqs.size());
        
        // 多线程并发发送消息
        for (FaqEntity faq : faqs) {
            executorService.submit(() -> {
                try {
                    log.debug("发送FAQ索引请求: {}", faq.getUid());
                    faqMessageService.sendToIndexQueue(faq.getUid());
                } catch (Exception e) {
                    log.error("发送FAQ索引消息失败: {}", e.getMessage(), e);
                }
            });
        }
        
        log.info("批量索引测试任务已提交，时间: {}", ZonedDateTime.now());
    }
    
    /**
     * 清理资源
     */
    @PreDestroy
    public void destroy() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
