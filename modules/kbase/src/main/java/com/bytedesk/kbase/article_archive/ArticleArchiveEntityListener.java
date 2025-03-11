/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-31 16:33:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 22:56:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article_archive;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.article_archive.event.ArticleArchiveCreateEvent;
import com.bytedesk.kbase.article_archive.event.ArticleArchiveUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ArticleArchiveEntityListener {

    @PostPersist
    public void onPostPersist(ArticleArchiveEntity article_archive) {
        log.info("ArticleArchiveEntityListener: onPostPersist");
        ArticleArchiveEntity clonedArticleArchive = SerializationUtils.clone(article_archive);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new ArticleArchiveCreateEvent(this, clonedArticleArchive));
    }

    @PostUpdate
    public void onPostUpdate(ArticleArchiveEntity article_archive) {
        log.info("ArticleArchiveEntityListener: onPostUpdate");
        ArticleArchiveEntity clonedArticleArchive = SerializationUtils.clone(article_archive);
        //
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new ArticleArchiveUpdateEvent(this, clonedArticleArchive));
    }
    
}
