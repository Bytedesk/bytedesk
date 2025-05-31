/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-27 13:53:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-04 15:48:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

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
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.kbase.article.ArticleEntity;
import com.bytedesk.kbase.article.ArticleResponse;
import com.bytedesk.kbase.article.event.ArticleCreateEvent;
import com.bytedesk.kbase.article.event.ArticleUpdateEvent;
import com.bytedesk.kbase.kbase.event.KbaseCreateEvent;
import com.bytedesk.kbase.kbase.event.KbaseUpdateEvent;
import com.bytedesk.kbase.quick_reply.QuickReplyRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO: delete event 删除相应网页
 */
@Slf4j
@Component
@AllArgsConstructor
public class KbaseEventListener {

        private final KbaseRestService kbaseService;

        private final KbaseStaticService kbaseStaticService;

        private final QuickReplyRestService quickReplyRestService;

        @EventListener
        public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
                OrganizationEntity organization = (OrganizationEntity) event.getSource();
                String orgUid = organization.getUid();
                log.info("onOrganizationCreateEvent: orgUid {}", orgUid);
                // 初始化知识库
                kbaseService.initKbase(orgUid);
                // 初始化快捷回复分类
                quickReplyRestService.initQuickReplyCategory(orgUid);
                // 初始化快捷回复
                quickReplyRestService.initQuickReply(orgUid);
        }

        @EventListener
        public void onKbaseCreateEvent(KbaseCreateEvent event) {
                // KbaseCreateEvent kbaseCreateEvent = event.getObject();
                KbaseEntity kbase = event.getKbase();
                // log.info("onKbaseCreateEvent {}", kbase.getUid());
                if (!kbase.getType().equals(KbaseTypeEnum.HELPCENTER.name())) {
                        return;
                }
                //
                List<CategoryResponse> categories = new ArrayList<>();
                Page<ArticleResponse> articles = new PageImpl<>(
                                Collections.emptyList(), // 空的内容列表
                                PageRequest.of(0, 10), // 第一页，一页显示1个元素
                                1 // 总元素数为0
                );
                kbaseStaticService.toHtmlKb(kbase, categories, articles, articles, articles);
                kbaseStaticService.toHtmlSearch(kbase);
        }

        @EventListener
        public void onKbaseUpdateEvent(KbaseUpdateEvent event) {
                // KbaseUpdateEvent kbaseUpdateEvent = event.getObject();
                KbaseEntity kbase = event.getKbase();
                // log.info("onKbaseUpdateEvent {}", kbase.getUid());
                if (!kbase.getType().equals(KbaseTypeEnum.HELPCENTER.name())) {
                        return;
                }
                //
                kbaseStaticService.updateKbase(kbase);
        }

        @EventListener
        public void onCategoryCreateEvent(CategoryCreateEvent event) {
                // CategoryCreateEvent categoryCreateEvent = event.getObject();
                CategoryEntity category = event.getCategory();
                // log.info("onCategoryCreateEvent {}", category.getName());
                String kbUid = category.getKbUid();
                if (kbUid == null) {
                        return;
                }
                //
                Optional<KbaseEntity> kbaseOptional = kbaseService.findByUid(kbUid);
                if (kbaseOptional.isPresent()) {
                        if (!kbaseOptional.get().getType().equals(KbaseTypeEnum.HELPCENTER.name())) {
                                return;
                        }
                        //
                        kbaseStaticService.updateKbase(kbaseOptional.get());
                        // Page<CategoryResponse> categoriesPage =
                        // kbaseService.getCategories(kbaseOptional.get());
                        // Page<ArticleResponse> articlesPage =
                        // kbaseService.getArticlesByCategory(kbaseOptional.get(), category.getUid());
                        // kbaseStaticService.toHtmlCategory(kbaseOptional.get(),
                        // categoryService.convertToResponse(category), categoriesPage.getContent(),
                        // articlesPage.getContent());
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
                Optional<KbaseEntity> kbaseOptional = kbaseService.findByUid(category.getKbUid());
                if (kbaseOptional.isPresent()) {
                        if (!kbaseOptional.get().getType().equals(KbaseTypeEnum.HELPCENTER.name())) {
                                return;
                        }
                        //
                        kbaseStaticService.updateKbase(kbaseOptional.get());
                        // Page<CategoryResponse> categoriesPage =
                        // kbaseService.getCategories(kbaseOptional.get());
                        // Page<ArticleResponse> articlesPage =
                        // kbaseService.getArticlesByCategory(kbaseOptional.get(), category.getUid());
                        // kbaseStaticService.toHtmlCategory(kbaseOptional.get(),
                        // categoryService.convertToResponse(category), categoriesPage.getContent(),
                        // articlesPage.getContent());
                } else {
                        log.error("onCategoryUpdateEvent kb not found {}", category.getKbUid());
                }
        }

        @EventListener
        public void onArticleCreateEvent(ArticleCreateEvent event) {
                // ArticleCreateEvent articleCreateEvent = event.getObject();
                ArticleEntity article = event.getArticle();
                log.info("onArticleCreateEvent {}, {}", article.getTitle(), article.getKbase().getUid());
                if (article.getKbase().getUid() == null) {
                        return;
                }
                //
                Optional<KbaseEntity> kbaseOptional = kbaseService.findByUid(article.getKbase().getUid());
                if (kbaseOptional.isPresent()) {
                        if (!kbaseOptional.get().getType().equals(KbaseTypeEnum.HELPCENTER.name())) {
                                return;
                        }
                        //
                        kbaseStaticService.updateKbase(kbaseOptional.get());
                        // Page<CategoryResponse> categoriesPage =
                        // kbaseService.getCategories(kbaseOptional.get());
                        // kbaseStaticService.toHtmlArticle(kbaseOptional.get(),
                        // articleService.convertToResponse(article), categoriesPage.getContent(), new
                        // ArrayList<>());
                } else {
                        log.error("onArticleCreateEvent kb not found {}", article.getKbase().getUid());
                }
        }

        @EventListener
        public void onArticleUpdateEvent(ArticleUpdateEvent event) {
                // ArticleUpdateEvent articleUpdateEvent = event.getObject();
                ArticleEntity article = event.getArticle();
                log.info("onArticleUpdateEvent {}, {}", article.getTitle(), article.getKbase().getUid());
                if (article.getKbase().getUid() == null) {
                        return;
                }
                //
                Optional<KbaseEntity> kbaseOptional = kbaseService.findByUid(article.getKbase().getUid());
                if (kbaseOptional.isPresent()) {
                        if (!kbaseOptional.get().getType().equals(KbaseTypeEnum.HELPCENTER.name())) {
                                return;
                        }
                        //
                        kbaseStaticService.updateKbase(kbaseOptional.get());
                        // Page<CategoryResponse> categoriesPage =
                        // kbaseService.getCategories(kbaseOptional.get());
                        // kbaseStaticService.toHtmlArticle(kbaseOptional.get(),
                        // articleService.convertToResponse(article), categoriesPage.getContent(), new
                        // ArrayList<>());
                } else {
                        log.error("onArticleUpdateEvent kb not found {}", article.getKbase().getUid());
                }
        }

        //

}
