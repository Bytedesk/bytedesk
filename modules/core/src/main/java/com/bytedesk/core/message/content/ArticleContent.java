/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-12 16:38:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 16:40:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleContent extends BaseContent {
    
    private static final long serialVersionUID = 1L;
    
    private String title;

    private String url;

    private String imageUrl;

    private String description;

    private String content;

    /**
     * 从JSON字符串反序列化为ArticleContent对象
     * @param json JSON字符串
     * @return ArticleContent对象，如果解析失败返回null
     */
    public static ArticleContent fromJson(String json) {
        return BaseContent.fromJson(json, ArticleContent.class);
    }

}
