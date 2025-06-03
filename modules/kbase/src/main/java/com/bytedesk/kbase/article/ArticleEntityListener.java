/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-31 16:33:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 14:58:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.article.event.ArticleCreateEvent;
import com.bytedesk.kbase.article.event.ArticleDeleteEvent;
import com.bytedesk.kbase.article.event.ArticleUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ArticleEntityListener {

    @PostPersist
    public void onPostPersist(ArticleEntity article) {
        log.info("ArticleEntityListener: onPostPersist");
        if (!article.isVectorIndexed()) {
            ArticleEntity clonedArticle = SerializationUtils.clone(article);
            BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
            publisher.publishEvent(new ArticleCreateEvent(this, clonedArticle));
        }
    }

    @PostUpdate
    public void onPostUpdate(ArticleEntity article) {
        log.info("ArticleEntityListener: onPostUpdate");
        if (!article.isVectorIndexed() && !article.isStatusSuccess()) {
            ArticleEntity clonedArticle = SerializationUtils.clone(article);
            //
            if (article.isDeleted()) {
                BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
                publisher.publishEvent(new ArticleDeleteEvent(this, clonedArticle));
            } else {
                BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
                publisher.publishEvent(new ArticleUpdateEvent(this, clonedArticle));
            }
        }
    }

}
