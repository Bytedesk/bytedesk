/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 21:13:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 18:38:57
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
import com.bytedesk.kbase.article_archive.ArticleArchiveEntity;
import com.bytedesk.kbase.article_archive.ArticleArchiveResponse;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseResponse;

public class KbaseConvertUtils {

    private static final ModelMapper modelMapper = new ModelMapper();

    private KbaseConvertUtils() {}

    public static KbaseResponse convertToKbaseResponse(KbaseEntity entity) {
        return modelMapper.map(entity, KbaseResponse.class);
    }

    public static ArticleResponse convertToArticleResponse(ArticleEntity entity) {
        ArticleResponse articleResponse = modelMapper.map(entity, ArticleResponse.class);
        articleResponse.setUser(UserProtobuf.fromJson(entity.getUser()));
        return articleResponse;
    }

    public static ArticleArchiveResponse convertToArticleArchiveResponse(ArticleArchiveEntity entity) {
        ArticleArchiveResponse articleResponse = modelMapper.map(entity, ArticleArchiveResponse.class);
        articleResponse.setUser(UserProtobuf.fromJson(entity.getUser()));
        return articleResponse;
    }
}
