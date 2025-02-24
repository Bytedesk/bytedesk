/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-27 13:53:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 23:02:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.bytedesk.core.category.CategoryCreateEvent;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryUpdateEvent;
import com.bytedesk.kbase.article.ArticleCreateEvent;
import com.bytedesk.kbase.article.ArticleEntity;
import com.bytedesk.kbase.article.ArticleResponse;
import com.bytedesk.kbase.article.ArticleUpdateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO: delete event 删除相应网页
 */
@Slf4j
@Component
@AllArgsConstructor
public class KnowledgebaseVipEventListener {

        private final KnowledgebaseRestService knowledgebaseService;

        // private final ArticleService articleService;
        // private final CategoryService categoryService;

        private final KnowledgebaseVipStaticService knowledgebaseStaticService;

        @EventListener
        public void onKnowledgebaseCreateEvent(KnowledgebaseCreateEvent event) {
                // KnowledgebaseCreateEvent knowledgebaseCreateEvent = event.getObject();
                KnowledgebaseEntity knowledgebase = event.getKnowledgebase();
                log.info("onKnowledgebaseCreateEvent {}", knowledgebase.getUid());
                if (!knowledgebase.getType().equals(KnowledgebaseTypeEnum.HELPCENTER.name())) {
                        return;
                }
                // 
                List<CategoryResponse> categories = new ArrayList<>();
                Page<ArticleResponse> articles = new PageImpl<>(
                        Collections.emptyList(), // 空的内容列表
                        PageRequest.of(0, 1),    // 第一页，一页显示1个元素
                        0                        // 总元素数为0
                );
                knowledgebaseStaticService.toHtmlKb(knowledgebase, categories, articles, articles, articles);
                knowledgebaseStaticService.toHtmlSearch(knowledgebase);
        }

        @EventListener
        public void onKnowledgebaseUpdateEvent(KnowledgebaseUpdateEvent event) {
                // KnowledgebaseUpdateEvent knowledgebaseUpdateEvent = event.getObject();
                KnowledgebaseEntity knowledgebase = event.getKnowledgebase();
                log.info("onKnowledgebaseUpdateEvent {}", knowledgebase.getUid());
                if (!knowledgebase.getType().equals(KnowledgebaseTypeEnum.HELPCENTER.name())) {
                        return;
                }
                // 
                knowledgebaseStaticService.updateKbase(knowledgebase);
        }

        @EventListener
        public void onCategoryCreateEvent(CategoryCreateEvent event) {
                // CategoryCreateEvent categoryCreateEvent = event.getObject();
                CategoryEntity category = event.getCategory();
                log.info("onCategoryCreateEvent {}", category.getName());
                String kbUid = category.getKbUid();
                if (kbUid == null) {
                        return;
                }
                // 
                Optional<KnowledgebaseEntity> kbaseOptional = knowledgebaseService.findByUid(kbUid);
                if (kbaseOptional.isPresent()) {
                        if (!kbaseOptional.get().getType().equals(KnowledgebaseTypeEnum.HELPCENTER.name())) {
                                return;
                        }
                        // 
                        knowledgebaseStaticService.updateKbase(kbaseOptional.get());
                        // Page<CategoryResponse> categoriesPage = knowledgebaseService.getCategories(kbaseOptional.get());
                        // Page<ArticleResponse> articlesPage = knowledgebaseService.getArticlesByCategory(kbaseOptional.get(), category.getUid());
                        // knowledgebaseStaticService.toHtmlCategory(kbaseOptional.get(), categoryService.convertToResponse(category), categoriesPage.getContent(), articlesPage.getContent());
                } else {
                        log.error("onCategoryCreateEvent kb not found {}", kbUid);
                }
        }

        @EventListener
        public void onCategoryUpdateEvent(CategoryUpdateEvent event) {
                // CategoryUpdateEvent categoryUpdateEvent = event.getObject();
                CategoryEntity category = event.getCategory();
                log.info("onCategoryUpdateEvent {}, {}", category.getName(), category.getKbUid());
                if (category.getKbUid() == null) {
                        return;
                }
                // 
                Optional<KnowledgebaseEntity> kbaseOptional = knowledgebaseService.findByUid(category.getKbUid());
                if (kbaseOptional.isPresent()) {
                        if (!kbaseOptional.get().getType().equals(KnowledgebaseTypeEnum.HELPCENTER.name())) {
                                return;
                        }
                        // 
                        knowledgebaseStaticService.updateKbase(kbaseOptional.get());
                        // Page<CategoryResponse> categoriesPage = knowledgebaseService.getCategories(kbaseOptional.get());
                        // Page<ArticleResponse> articlesPage = knowledgebaseService.getArticlesByCategory(kbaseOptional.get(), category.getUid());
                        // knowledgebaseStaticService.toHtmlCategory(kbaseOptional.get(), categoryService.convertToResponse(category), categoriesPage.getContent(), articlesPage.getContent());
                } else {
                        log.error("onCategoryUpdateEvent kb not found {}", category.getKbUid());
                }
        }

        @EventListener
        public void onArticleCreateEvent(ArticleCreateEvent event) {
                // ArticleCreateEvent articleCreateEvent = event.getObject();
                ArticleEntity article = event.getArticle();
                log.info("onArticleCreateEvent {}, {}", article.getTitle(), article.getKbUid());
                if (article.getKbUid() == null) {
                        return;
                }
                // 
                Optional<KnowledgebaseEntity> kbaseOptional = knowledgebaseService.findByUid(article.getKbUid());
                if (kbaseOptional.isPresent()) {
                        if (!kbaseOptional.get().getType().equals(KnowledgebaseTypeEnum.HELPCENTER.name())) {
                                return;
                        }
                        // 
                        knowledgebaseStaticService.updateKbase(kbaseOptional.get());
                        // Page<CategoryResponse> categoriesPage = knowledgebaseService.getCategories(kbaseOptional.get());
                        // knowledgebaseStaticService.toHtmlArticle(kbaseOptional.get(), articleService.convertToResponse(article), categoriesPage.getContent(), new ArrayList<>());
                } else {
                        log.error("onArticleCreateEvent kb not found {}", article.getKbUid());
                }
        }

        @EventListener
        public void onArticleUpdateEvent(ArticleUpdateEvent event) {
                // ArticleUpdateEvent articleUpdateEvent = event.getObject();
                ArticleEntity article = event.getArticle();
                log.info("onArticleUpdateEvent {}, {}", article.getTitle(), article.getKbUid());
                if (article.getKbUid() == null) {
                        return;
                }
                // 
                Optional<KnowledgebaseEntity> kbaseOptional = knowledgebaseService.findByUid(article.getKbUid());
                if (kbaseOptional.isPresent()) {
                        if (!kbaseOptional.get().getType().equals(KnowledgebaseTypeEnum.HELPCENTER.name())) {
                                return;
                        }
                        // 
                        knowledgebaseStaticService.updateKbase(kbaseOptional.get());
                        // Page<CategoryResponse> categoriesPage = knowledgebaseService.getCategories(kbaseOptional.get());
                        // knowledgebaseStaticService.toHtmlArticle(kbaseOptional.get(), articleService.convertToResponse(article), categoriesPage.getContent(), new ArrayList<>());
                } else {
                        log.error("onArticleUpdateEvent kb not found {}", article.getKbUid());
                }
        }

        // 
        

}
