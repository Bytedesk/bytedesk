/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 16:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 09:38:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.llm_faq.FaqRestService;
import com.bytedesk.kbase.llm_faq.mq.FaqMessageService;

import lombok.extern.slf4j.Slf4j;

/**
 * FAQ索引测试控制器
 * 用于手动触发并发索引测试
 */
@Slf4j
@RestController
@RequestMapping("/api/faq/test")
public class FaqIndexTestController {
    
    @Autowired
    private FaqRestService faqRestService;
    
    @Autowired
    private FaqMessageService faqMessageService;

    /**
     * 批量索引FAQ
     * 
     * @param count 索引的FAQ数量
     * @param threads 并发线程数
     * @return 测试结果信息
     */
    @GetMapping("/batch-index")
    public ResponseEntity<Object> batchIndex(
            @RequestParam(required = false, defaultValue = "50") int count,
            @RequestParam(required = false, defaultValue = "5") int threads) {
        
        // 验证管理员权限
        // if (!authService.isCurrentUserAdmin()) {
        //     return ResponseEntity.status(403).body("权限不足，仅管理员可执行此操作");
        // }
        
        log.info("启动批量索引测试 - 数量: {}, 线程数: {}", count, threads);
        
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        List<String> processedFaqs = new ArrayList<>();
        
        int perThread = count / threads;
        // if (perThread < 1) perThread = 1;
        
        // 启动所有线程
        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < perThread; j++) {
                        try {
                            Optional<FaqEntity> optFaq = faqRestService.findRandomFaq();
                            if (optFaq.isPresent()) {
                                FaqEntity faq = optFaq.get();
                                String faqUid = faq.getUid();
                                
                                // 发送到索引队列
                                faqMessageService.sendToIndexQueue(faqUid);
                                
                                synchronized (processedFaqs) {
                                    processedFaqs.add(faqUid);
                                }
                                
                                successCount.incrementAndGet();
                                
                                // 小暂停避免过载
                                Thread.sleep(100);
                            }
                        } catch (Exception e) {
                            log.error("批量索引FAQ时出错: {}", e.getMessage(), e);
                            failCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    log.error("执行批量索引线程时出错: {}", e.getMessage(), e);
                }
            });
        }
        
        // 关闭线程池并等待完成
        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.warn("批量索引等待中断: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("success", successCount.get());
        result.put("fail", failCount.get());
        result.put("total", count);
        result.put("threads", threads);
        result.put("processedCount", processedFaqs.size());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试单个FAQ的索引处理
     * 
     * @param faqUid FAQ的唯一ID
     * @return 处理结果
     */
    @GetMapping("/index-single")
    public ResponseEntity<Object> indexSingle(
            @RequestParam String faqUid) {
        
        try {
            // 验证管理员权限
            // if (!authService.isCurrentUserAdmin()) {
            //     return ResponseEntity.status(403).body("权限不足，仅管理员可执行此操作");
            // }
            
            log.info("测试单个FAQ索引: {}", faqUid);
            
            // 检查FAQ是否存在
            Optional<FaqEntity> optFaq = faqRestService.findByUid(faqUid);
            if (!optFaq.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // 发送到索引队列
            faqMessageService.sendToIndexQueue(faqUid);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("faqUid", faqUid);
            result.put("message", "FAQ索引消息已发送到队列");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("索引单个FAQ时出错: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("处理失败: " + e.getMessage());
        }
    }
}
