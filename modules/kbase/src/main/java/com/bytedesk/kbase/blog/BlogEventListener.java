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
package com.bytedesk.kbase.blog;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.kbase.blog.event.BlogCreateEvent;
import com.bytedesk.kbase.blog.event.BlogDeleteEvent;
import com.bytedesk.kbase.blog.event.BlogUpdateEvent;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class BlogEventListener {

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

 
}

