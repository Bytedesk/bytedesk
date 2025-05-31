/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-31 16:33:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 09:49:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

// import com.bytedesk.kbase.article.elastic.ArticleElasticService;
import com.bytedesk.kbase.article.event.ArticleCreateEvent;
import com.bytedesk.kbase.article.event.ArticleDeleteEvent;
import com.bytedesk.kbase.article.event.ArticleUpdateEvent;
import com.bytedesk.kbase.article.mq.ArticleMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleEventListener {

    // private final ArticleElasticService articleElasticService;
    private final ArticleMessageService articleMessageService;

    // Article仅用于全文搜索，通过消息队列异步处理
    @EventListener
    public void onArticleCreateEvent(ArticleCreateEvent event) {
        ArticleEntity article = event.getArticle();
        log.info("ArticleEventListener onArticleCreateEvent: {}", article.getTitle());

        // 使用消息队列异步处理索引，避免乐观锁冲突
        articleMessageService.sendToIndexQueue(article.getUid());
    }

    // Article用于全文搜索和向量搜索，通过消息队列异步处理
    @EventListener
    public void onArticleUpdateEvent(ArticleUpdateEvent event) {
        ArticleEntity article = event.getArticle();
        log.info("ArticleEventListener ArticleUpdateEvent: {}", article.getTitle());

        // 使用消息队列异步处理索引更新
        articleMessageService.sendToIndexQueue(article.getUid());
    }

    @EventListener
    public void onArticleDeleteEvent(ArticleDeleteEvent event) {
        ArticleEntity article = event.getArticle();
        log.info("ArticleEventListener onArticleDeleteEvent: {}", article.getTitle());

        // 删除操作通过消息队列异步处理
        articleMessageService.sendToDeleteQueue(article.getUid());
    }
}
