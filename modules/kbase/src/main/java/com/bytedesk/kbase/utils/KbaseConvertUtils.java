/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 21:13:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 18:03:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import lombok.experimental.UtilityClass;

import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.kbase.article.ArticleEntity;
import com.bytedesk.kbase.article.ArticleResponse;
import com.bytedesk.kbase.article_archive.ArticleArchiveEntity;
import com.bytedesk.kbase.article_archive.ArticleArchiveResponse;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseResponse;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqResponse;
import com.bytedesk.kbase.faq.FaqResponseSimple;

@UtilityClass
public class KbaseConvertUtils {

    private static ModelMapper getModelMapper() {
        return ApplicationContextHolder.getBean(ModelMapper.class);
    }

    public static KbaseResponse convertToKbaseResponse(KbaseEntity entity) {
        return getModelMapper().map(entity, KbaseResponse.class);
    }

    public static ArticleResponse convertToArticleResponse(ArticleEntity entity) {
        ArticleResponse articleResponse = getModelMapper().map(entity, ArticleResponse.class);
        articleResponse.setUser(UserProtobuf.fromJson(entity.getUser()));
        return articleResponse;
    }

    public static ArticleArchiveResponse convertToArticleArchiveResponse(ArticleArchiveEntity entity) {
        ArticleArchiveResponse articleResponse = getModelMapper().map(entity, ArticleArchiveResponse.class);
        articleResponse.setUser(UserProtobuf.fromJson(entity.getUser()));
        return articleResponse;
    }

    public static FaqResponse convertToFaqResponse(FaqEntity entity) {
        FaqResponse response = getModelMapper().map(entity, FaqResponse.class);

        // 处理相关问题，避免循环依赖
        if (entity.getRelatedFaqs() != null) {
            List<FaqResponseSimple> simpleFaqs = entity.getRelatedFaqs().stream()
                    .map(relatedFaq -> convertToFaqResponseSimple(relatedFaq))
                    .collect(Collectors.toList());
            response.setRelatedFaqs(simpleFaqs);
        }

        return response;
    }

    public static FaqResponseSimple convertToFaqResponseSimple(FaqEntity entity) {
        return getModelMapper().map(entity, FaqResponseSimple.class);
    }
}
