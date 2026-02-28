/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:50:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.marketing.blog;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.event.CategoryCreateEvent;
import com.bytedesk.core.category.event.CategoryUpdateEvent;
import com.bytedesk.marketing.blog.event.BlogCreateEvent;
import com.bytedesk.marketing.blog.event.BlogDeleteEvent;
import com.bytedesk.marketing.blog.event.BlogUpdateEvent;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;
import com.bytedesk.kbase.kbase.event.KbaseCreateEvent;
import com.bytedesk.kbase.kbase.event.KbaseUpdateEvent;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class BlogEventListener {

    private final KbaseRestService kbaseRestService;

    private final BlogRestService blogRestService;

    private final BlogStaticService blogStaticService;

    @Order(12)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("thread - organization created: {}", organization.getName());
        blogRestService.initBlogs(orgUid);
    }

    @EventListener
    public void onBlogCreateEvent(BlogCreateEvent event) {
        BlogEntity blog = event.getBlog();
        if (blog == null) {
            return;
        }
        if (blog.getKbUid() == null || blog.getKbUid().isBlank()) {
            log.warn("onBlogCreateEvent skipped: missing kbUid for blog uid={}", blog.getUid());
            return;
        }
        log.info("blog - created: {}", blog.getUid());
        blogStaticService.updateBlogPost(blog.getUid());
    }

    @EventListener
    public void onBlogUpdateEvent(BlogUpdateEvent event) {
        BlogEntity blog = event.getBlog();
        if (blog == null) {
            return;
        }
        if (blog.getKbUid() == null || blog.getKbUid().isBlank()) {
            log.warn("onBlogUpdateEvent skipped: missing kbUid for blog uid={}", blog.getUid());
            return;
        }
        log.info("blog - updated: {}", blog.getUid());
        blogStaticService.updateBlogPost(blog.getUid());
    }

    @EventListener
    public void onBlogDeleteEvent(BlogDeleteEvent event) {
        BlogEntity blog = event.getBlog();
        if (blog == null) {
            return;
        }
        if (blog.getKbUid() == null || blog.getKbUid().isBlank()) {
            log.warn("onBlogDeleteEvent skipped: missing kbUid for blog uid={}", blog.getUid());
            return;
        }
        log.info("blog - deleted: {}", blog.getUid());
        blogStaticService.deleteBlogPostStatic(blog.getKbUid(), blog.getUid());
    }

    @EventListener
    public void onKbaseCreateEvent(KbaseCreateEvent event) {
        KbaseEntity kbase = event.getKbase();
        if (kbase == null || !KbaseTypeEnum.BLOG.name().equals(kbase.getType())) {
            return;
        }
        blogStaticService.updateBlogKbase(kbase.getUid());
    }

    @EventListener
    public void onKbaseUpdateEvent(KbaseUpdateEvent event) {
        KbaseEntity kbase = event.getKbase();
        if (kbase == null || !KbaseTypeEnum.BLOG.name().equals(kbase.getType())) {
            return;
        }
        blogStaticService.updateBlogKbase(kbase.getUid());
    }

    @EventListener
    public void onCategoryCreateEvent(CategoryCreateEvent event) {
        CategoryEntity category = event.getCategory();
        if (category == null || category.getKbUid() == null || category.getKbUid().isBlank()) {
            return;
        }
        Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(category.getKbUid());
        if (kbaseOptional.isPresent() && KbaseTypeEnum.BLOG.name().equals(kbaseOptional.get().getType())) {
            blogStaticService.updateBlogKbase(category.getKbUid());
        }
    }

    @EventListener
    public void onCategoryUpdateEvent(CategoryUpdateEvent event) {
        CategoryEntity category = event.getCategory();
        if (category == null || category.getKbUid() == null || category.getKbUid().isBlank()) {
            return;
        }
        Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(category.getKbUid());
        if (kbaseOptional.isPresent() && KbaseTypeEnum.BLOG.name().equals(kbaseOptional.get().getType())) {
            blogStaticService.updateBlogKbase(category.getKbUid());
        }
    }

 
}

