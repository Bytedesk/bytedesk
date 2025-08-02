/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-31 16:33:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-31 16:37:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.kbase.article.ArticleEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ArticleCreateEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;

    private ArticleEntity article;

    public ArticleCreateEvent(Object source, ArticleEntity article) {
        super(source);
        this.article = article;
    }

}
