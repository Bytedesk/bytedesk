/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 18:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 17:36:41
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

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.jms.JmsArtemisConstants;
import com.bytedesk.kbase.article.ArticleEntity;
import com.bytedesk.kbase.article.ArticleRestService;
import com.bytedesk.kbase.article.vector.ArticleVectorService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 文章索引消息监听器
 * 处理文章的索引和删除消息
 */
@Slf4j
@Component
public class ArticleIndexMessageListener {

    private final ArticleRestService articleRestService;
    @Autowired(required = false)
    private ArticleVectorService articleVectorService;
    // private final ArticleElasticService articleElasticService;

    public ArticleIndexMessageListener(ArticleRestService articleRestService) {
        this.articleRestService = articleRestService;
    }

    /**
     * 监听文章索引队列，处理索引和删除操作
     * 使用并发消费者提高吞吐量，但保持消息的顺序处理
     * 
     * @param message 文章索引消息
     */
    @JmsListener(
        destination = JmsArtemisConstants.QUEUE_ARTICLE_INDEX, 
        concurrency = "3-5", // 并发消费者数量
        containerFactory = "jmsListenerContainerFactory"
    )
    public void onIndexMessage(ArticleIndexMessage message) {
        try {
            log.info("收到文章索引消息: {}, 操作: {}", message.getArticleUid(), message.getOperationType());
            
            // 获取文章实体
            Optional<ArticleEntity> articleOpt = articleRestService.findByUid(message.getArticleUid());
            if (!articleOpt.isPresent()) {
                log.error("文章实体不存在，无法处理索引操作: {}", message.getArticleUid());
                return;
            }
            
            ArticleEntity article = articleOpt.get();
            
            // 根据操作类型处理
            if ("delete".equals(message.getOperationType())) {
                // 处理删除操作
                log.info("处理文章删除索引: {}", article.getTitle());
                
                // 删除向量索引
                if (message.getUpdateVectorIndex() && articleVectorService != null) {
                    try {
                        articleVectorService.deleteArticle(article);
                    } catch (Exception e) {
                        log.error("删除文章向量索引时出错: {}, 错误: {}", article.getUid(), e.getMessage(), e);
                    }
                }
                
                // 删除全文索引 - 如果有相关实现的话
                // if (message.getUpdateElasticIndex()) {
                //     try {
                //         articleElasticService.deleteArticleIndex(article);
                //     } catch (Exception e) {
                //         log.error("删除文章全文索引时出错: {}, 错误: {}", article.getUid(), e.getMessage(), e);
                //     }
                // }
                
            } else {
                // 处理创建/更新索引操作
                log.info("处理文章创建/更新索引: {}", article.getTitle());
                
                // 创建/更新向量索引
                if (message.getUpdateVectorIndex() && articleVectorService != null) {
                    try {
                        articleVectorService.indexVector(article);
                    } catch (Exception e) {
                        log.error("创建文章向量索引时出错: {}, 错误: {}", article.getUid(), e.getMessage(), e);
                    }
                }
                
                // 创建/更新全文索引 - 如果有相关实现的话
                // if (message.getUpdateElasticIndex()) {
                //     try {
                //         articleElasticService.indexArticle(article);
                //     } catch (Exception e) {
                //         log.error("创建文章全文索引时出错: {}, 错误: {}", article.getUid(), e.getMessage(), e);
                //     }
                // }
            }
            
            log.info("文章索引消息处理完成: {}", message.getArticleUid());
            
        } catch (Exception e) {
            log.error("处理文章索引消息时出错: {}, 错误: {}", message.getArticleUid(), e.getMessage(), e);
            // 不抛出异常，避免消息重新投递，导致无限重试
        }
    }
}
