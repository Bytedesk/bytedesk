/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 16:40:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 14:57:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article.event;

import com.bytedesk.kbase.article.ArticleEntity;

public class ArticleDeleteEvent extends AbstractArticleEvent {

    private static final long serialVersionUID = 1L;

    public ArticleDeleteEvent(Object source, ArticleEntity article) {
        super(source, article);
    }
}