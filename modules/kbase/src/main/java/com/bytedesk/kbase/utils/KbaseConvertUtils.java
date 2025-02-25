/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 21:13:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 21:49:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.utils;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.kbase.article.ArticleEntity;
import com.bytedesk.kbase.article.ArticleResponse;

public class KbaseConvertUtils {

    private static final ModelMapper modelMapper = new ModelMapper();

    private KbaseConvertUtils() {}

    public static ArticleResponse convertToArticleResponse(ArticleEntity entity) {
        ArticleResponse articleResponse = modelMapper.map(entity, ArticleResponse.class);
        articleResponse.setUser(UserProtobuf.parseFrom(entity.getUser()));
        return articleResponse;
    }
}
