/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-31 16:33:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-31 16:40:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.event.GenericApplicationEvent;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ArticleEntityListener {

    @PostPersist
    public void onPostPersist(Article article) {
        log.info("ArticleEntityListener: onPostPersist");
        Article clonedArticle = SerializationUtils.clone(article);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishGenericApplicationEvent(new GenericApplicationEvent<ArticleCreateEvent>(this, new ArticleCreateEvent(this, clonedArticle)));
    }

    @PostUpdate
    public void onPostUpdate(Article article) {
        log.info("ArticleEntityListener: onPostUpdate");
        Article clonedArticle = SerializationUtils.clone(article);
        //
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishGenericApplicationEvent(new GenericApplicationEvent<ArticleUpdateEvent>(this, new ArticleUpdateEvent(this, clonedArticle)));
    }
    
}
