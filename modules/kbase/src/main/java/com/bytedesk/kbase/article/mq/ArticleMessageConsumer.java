/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 16:35:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 17:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article.mq;

import java.util.Optional;
import java.util.Random;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.jms.JmsArtemisConstants;
import com.bytedesk.kbase.article.ArticleEntity;
import com.bytedesk.kbase.article.ArticleRestService;
import com.bytedesk.kbase.article.elastic.ArticleElasticService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 文章索引消费者
 * 用于处理文章索引队列中的消息
 */
@Slf4j
@Component
@AllArgsConstructor
public class ArticleMessageConsumer {

    private final ArticleElasticService articleElasticService;
    private final ArticleRestService articleRestService;
    private final Random random = new Random();

    /**
     * 处理文章索引队列中的消息
     * 使用客户端确认模式，只有成功处理后才确认消息
     * 增强了对乐观锁冲突的处理
     * 
     * @param jmsMessage JMS原始消息
     * @param message 文章索引消息
     */
    @JmsListener(
        destination = JmsArtemisConstants.QUEUE_ARTICLE_INDEX, 
        containerFactory = "jmsListenerContainerFactory"
    )
    public void processIndexMessage(jakarta.jms.Message jmsMessage, ArticleIndexMessage message) {
        try {
            String articleUid = message.getArticleUid();
            String operationType = message.getOperationType();
            
            // 获取消息的投递次数
            int deliveryCount = 1;
            try {
                deliveryCount = jmsMessage.getIntProperty("JMSXDeliveryCount");
            } catch (Exception e) {
                log.warn("无法获取消息投递次数: {}", e.getMessage());
            }
            
            log.info("处理文章索引消息: {}, 操作: {}, 投递次数: {}", articleUid, operationType, deliveryCount);
            
            // 检查是否需要延迟处理
            if (deliveryCount > 1) {
                // 重试延迟时间随着重试次数增加（指数退避）
                long delay = (long) (Math.pow(2, deliveryCount - 1) * 100) + random.nextInt(200);
                log.info("消息重试: 延迟{}毫秒后处理", delay);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            // 查询文章实体
            Optional<ArticleEntity> articleOpt = articleRestService.findByUid(articleUid);
            
            if (articleOpt.isPresent()) {
                ArticleEntity article = articleOpt.get();
                
                // 根据操作类型进行处理
                if ("delete".equals(operationType)) {
                    handleDeleteOperation(article, message);
                } else {
                    // 默认为索引操作
                    handleIndexOperation(article, message);
                }
                
                // 确认消息
                jmsMessage.acknowledge();
                log.info("文章消息处理成功并确认: {}", articleUid);
                
            } else {
                if ("delete".equals(operationType)) {
                    // 对于删除操作，即使找不到文章实体也尝试从索引中删除
                    log.warn("找不到要删除的文章实体，仍然尝试从索引中删除: {}", articleUid);
                    try {
                        if (message.getUpdateElasticIndex()) {
                            articleElasticService.deleteArticle(articleUid);
                        }
                        
                        jmsMessage.acknowledge();
                        log.info("文章索引删除成功并确认: {}", articleUid);
                    } catch (Exception e) {
                        log.error("删除不存在文章的索引时发生错误: {}, 错误: {}", articleUid, e.getMessage());
                        throw e; // 重新抛出异常，触发消息重发
                    }
                } else {
                    // 对于其他操作，找不到实体则报错
                    log.warn("找不到要处理的文章: {}", articleUid);
                    jmsMessage.acknowledge(); // 仍然确认消息，因为找不到实体是业务问题，再试也没用
                }
            }
            
        } catch (Exception e) {
            log.error("处理文章索引消息失败: {}", e.getMessage(), e);
            // 异常情况下不确认消息，将触发消息重发
        }
    }
    
    /**
     * 处理索引操作
     * @param article 文章实体
     * @param message 索引消息
     */
    private void handleIndexOperation(ArticleEntity article, ArticleIndexMessage message) {
        String articleUid = article.getUid();
        
        try {
            // 只有已发布的文章才进行索引
            if (!article.getPublished()) {
                log.info("跳过未发布文章的索引: {}", articleUid);
                return;
            }

            // 更新Elasticsearch索引
            if (message.getUpdateElasticIndex()) {
                log.info("更新文章Elasticsearch索引: {}", articleUid);
                articleElasticService.indexArticle(article);
            }
            
            // 这里可以添加对向量索引的处理，如果需要
            if (message.getUpdateVectorIndex()) {
                log.info("更新文章向量索引: {}", articleUid);
                // 如果有文章向量服务，在这里调用
                // articleVectorService.indexArticle(article);
            }
            
            log.info("文章索引操作完成: {}", articleUid);
            
        } catch (Exception e) {
            log.error("处理文章索引操作时发生错误: {}, 错误信息: {}", articleUid, e.getMessage(), e);
            throw e; // 重新抛出异常，触发消息重发
        }
    }
    
    /**
     * 处理删除操作
     * @param article 文章实体
     * @param message 索引消息
     */
    private void handleDeleteOperation(ArticleEntity article, ArticleIndexMessage message) {
        String articleUid = article.getUid();
        
        try {
            // 从Elasticsearch中删除索引
            if (message.getUpdateElasticIndex()) {
                log.info("从Elasticsearch中删除文章索引: {}", articleUid);
                boolean deleted = articleElasticService.deleteArticle(articleUid);
                if (deleted) {
                    log.info("成功从Elasticsearch中删除文章索引: {}", articleUid);
                } else {
                    log.warn("从Elasticsearch中删除文章索引失败: {}", articleUid);
                }
            }
            
            // 这里可以添加对向量索引删除的处理，如果需要
            if (message.getUpdateVectorIndex()) {
                log.info("从向量引擎中删除文章索引: {}", articleUid);
                // 如果有文章向量服务，在这里调用
                // boolean vectorDeleted = articleVectorService.deleteArticle(articleUid);
                // if (vectorDeleted) {
                //     log.info("成功从向量引擎中删除文章索引: {}", articleUid);
                // } else {
                //     log.warn("从向量引擎中删除文章索引失败: {}", articleUid);
                // }
            }
            
            log.info("文章删除操作完成: {}", articleUid);
            
        } catch (Exception e) {
            log.error("处理文章删除操作时发生错误: {}, 错误信息: {}", articleUid, e.getMessage(), e);
            throw e; // 重新抛出异常，触发消息重发
        }
    }
}
