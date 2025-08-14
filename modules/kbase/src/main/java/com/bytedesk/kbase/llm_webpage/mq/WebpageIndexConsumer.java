/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 09:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 19:47:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage.mq;

import java.util.Optional;
import java.util.Random;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.jms.JmsArtemisConsts;
import com.bytedesk.kbase.llm_webpage.WebpageEntity;
import com.bytedesk.kbase.llm_webpage.WebpageRestService;
import com.bytedesk.kbase.llm_webpage.elastic.WebpageElasticService;
import com.bytedesk.kbase.llm_webpage.vector.WebpageVectorService;
import com.bytedesk.kbase.llm_webpage.service.WebpageCrawlerService;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

/**
 * 网页索引消费者
 * 用于处理网页索引队列中的消息，使用Artemis JMS
 */
@Slf4j
@Component
public class WebpageIndexConsumer {

    private final WebpageElasticService webpageElasticService;
    @Autowired(required = false)
    private WebpageVectorService webpageVectorService;
    private final WebpageRestService webpageRestService;
    private final WebpageCrawlerService webpageCrawlerService;

    public WebpageIndexConsumer(WebpageElasticService webpageElasticService, WebpageRestService webpageRestService, WebpageCrawlerService webpageCrawlerService) {
        this.webpageElasticService = webpageElasticService;
        this.webpageRestService = webpageRestService;
        this.webpageCrawlerService = webpageCrawlerService;
    }
    private final Random random = new Random();

    /**
     * 处理网页索引队列中的消息
     * 使用客户端确认模式，只有成功处理后才确认消息
     * 增强了对乐观锁冲突的处理
     * 
     * @param jmsMessage JMS原始消息
     * @param message 网页索引消息
     */
    @JmsListener(destination = JmsArtemisConsts.QUEUE_WEBPAGE_INDEX, containerFactory = "jmsArtemisQueueFactory", concurrency = "3-10")
    public void processIndexMessage(jakarta.jms.Message jmsMessage, WebpageIndexMessage message) {
        boolean success = false;
        int maxRetryAttempts = 3; // 最大重试次数
        int currentAttempt = 1;
        
        while (currentAttempt <= maxRetryAttempts && !success) {
            try {
                if (currentAttempt > 1) {
                    log.info("尝试第{}次处理网页索引消息: {}", currentAttempt, message.getWebpageUid());
                } else {
                    log.debug("接收到网页索引请求: {}", message.getWebpageUid());
                }
                
                // 引入随机延迟，避免并发冲突，重试时增加延迟
                int baseDelay = 50 + random.nextInt(200); // 基础随机延迟
                int retryDelay = baseDelay * currentAttempt; // 根据重试次数增加延迟
                Thread.sleep(retryDelay);
                
                // 获取网页实体
                Optional<WebpageEntity> optionalWebpage = webpageRestService.findByUid(message.getWebpageUid());
                
                if (!optionalWebpage.isPresent()) {
                    log.warn("无法找到要索引的网页: {}", message.getWebpageUid());
                    // 消息处理完成，但没有找到实体，也认为是成功的（避免重复处理已删除的实体）
                    success = true;
                    break;
                }
                
                WebpageEntity webpage = optionalWebpage.get();
                
                // 根据操作类型执行相应的操作
                if ("delete".equals(message.getOperationType())) {
                    handleDeleteOperation(webpage, message);
                } else {
                    handleIndexOperation(webpage, message);
                }
                
                // 成功处理消息
                success = true;
                log.debug("成功处理网页索引消息: {}", message.getWebpageUid());
                
            } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
                // 乐观锁冲突，特殊处理
                log.warn("处理网页索引时发生乐观锁冲突: {}, 尝试次数: {}", message.getWebpageUid(), currentAttempt);
                
                if (currentAttempt == maxRetryAttempts) {
                    log.error("达到最大重试次数，乐观锁冲突无法解决: {}", message.getWebpageUid());
                    // 最后一次尝试失败，标记为成功以避免消息无限重试
                    success = true;
                }
            } catch (Exception e) {
                log.error("处理网页索引消息时出错: {}, 错误: {}", message.getWebpageUid(), e.getMessage(), e);
                if (currentAttempt < maxRetryAttempts) {
                    log.info("将在当前消费者内进行重试处理");
                } else {
                    log.error("达到最大重试次数，处理失败: {}", message.getWebpageUid());
                    success = false;
                    break;
                }
            }
            
            currentAttempt++;
        }
        
        // 根据处理结果确认或拒绝消息
        if (success) {
            acknowledgeMessage(jmsMessage);
        } else {
            log.warn("网页索引消息处理失败，不确认消息，等待消息队列重新投递: {}", message.getWebpageUid());
        }
    }

    /**
     * 处理索引操作
     * 
     * @param webpage 网页实体
     * @param message 索引消息
     */
    private void handleIndexOperation(WebpageEntity webpage, WebpageIndexMessage message) {
        try {
            // 首先检查是否需要抓取网页内容
            if (webpageCrawlerService.needsCrawling(webpage)) {
                log.info("网页需要抓取内容，开始抓取: {}", webpage.getUrl());
                webpage = webpageCrawlerService.crawlAndUpdateContent(webpage);
                
                // 验证抓取到的内容是否有效
                if (!webpageCrawlerService.isValidContent(webpage.getContent())) {
                    log.warn("网页内容无效或为空，跳过索引操作: {}", webpage.getUrl());
                    return;
                }
                
                log.info("成功抓取网页内容，继续索引操作: {} (内容长度: {})", 
                    webpage.getUrl(), webpage.getContent().length());
            } else {
                log.debug("网页已有内容或不需要抓取，直接进行索引: {}", webpage.getUrl());
            }
            
            // 处理Elastic搜索索引
            if (message.isUpdateElasticIndex()) {
                processElasticIndex(webpage);
            }
            
            // 处理向量索引
            if (message.isUpdateVectorIndex()) {
                processVectorIndex(webpage);
            }
            
            log.debug("成功完成网页索引操作: {}", webpage.getUid());
        } catch (Exception e) {
            log.error("处理网页索引操作失败: {}, 错误: {}", webpage.getUid(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 处理删除操作
     * 
     * @param webpage 网页实体
     * @param message 索引消息
     */
    private void handleDeleteOperation(WebpageEntity webpage, WebpageIndexMessage message) {
        try {
            // 处理Elastic搜索索引删除
            if (message.isUpdateElasticIndex()) {
                processElasticDelete(webpage);
            }
            
            // 处理向量索引删除
            if (message.isUpdateVectorIndex()) {
                processVectorDelete(webpage);
            }
            
            log.debug("成功完成网页删除操作: {}", webpage.getUid());
        } catch (Exception e) {
            log.error("处理网页删除操作失败: {}, 错误: {}", webpage.getUid(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 处理Elastic搜索索引
     * 
     * @param webpage 网页实体
     */
    private void processElasticIndex(WebpageEntity webpage) {
        try {
            log.debug("开始处理网页Elastic索引: {}", webpage.getUid());
            webpageElasticService.indexWebpage(webpage);
            log.debug("成功完成网页Elastic索引: {}", webpage.getUid());
        } catch (Exception e) {
            log.error("处理网页Elastic索引失败: {}, 错误: {}", webpage.getUid(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 处理向量索引
     * 
     * @param webpage 网页实体
     */
    private void processVectorIndex(WebpageEntity webpage) {
        try {
            log.debug("开始处理网页向量索引: {}", webpage.getUid());
            webpageVectorService.indexWebpageVector(webpage);
            log.debug("成功完成网页向量索引: {}", webpage.getUid());
        } catch (Exception e) {
            log.error("处理网页向量索引失败: {}, 错误: {}", webpage.getUid(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 处理Elastic搜索索引删除
     * 
     * @param webpage 网页实体
     */
    private void processElasticDelete(WebpageEntity webpage) {
        try {
            log.debug("开始删除网页Elastic索引: {}", webpage.getUid());
            webpageElasticService.deleteWebpageIndex(webpage.getUid());
            log.debug("成功删除网页Elastic索引: {}", webpage.getUid());
        } catch (Exception e) {
            log.error("删除网页Elastic索引失败: {}, 错误: {}", webpage.getUid(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 处理向量索引删除
     * 
     * @param webpage 网页实体
     */
    private void processVectorDelete(WebpageEntity webpage) {
        try {
            log.debug("开始删除网页向量索引: {}", webpage.getUid());
            webpageVectorService.deleteWebpageVector(webpage);
            log.debug("成功删除网页向量索引: {}", webpage.getUid());
        } catch (Exception e) {
            log.error("删除网页向量索引失败: {}, 错误: {}", webpage.getUid(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 确认消息处理
     * 
     * @param jmsMessage JMS消息
     */
    private void acknowledgeMessage(jakarta.jms.Message jmsMessage) {
        try {
            jmsMessage.acknowledge();
            log.debug("成功确认JMS消息");
        } catch (Exception e) {
            log.error("确认JMS消息失败: {}", e.getMessage(), e);
        }
    }
}
