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
package com.bytedesk.marketing.portal;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.event.CategoryCreateEvent;
import com.bytedesk.core.category.event.CategoryUpdateEvent;
import com.bytedesk.marketing.portal.event.PortalCreateEvent;
import com.bytedesk.marketing.portal.event.PortalDeleteEvent;
import com.bytedesk.marketing.portal.event.PortalUpdateEvent;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;
import com.bytedesk.kbase.kbase.event.KbaseCreateEvent;
import com.bytedesk.kbase.kbase.event.KbaseUpdateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class PortalEventListener {

    private final KbaseRestService kbaseRestService;

    private final PortalStaticService portalStaticService;

    @EventListener
    public void onPortalCreateEvent(PortalCreateEvent event) {
        PortalEntity portal = event.getPortal();
        if (portal == null) {
            return;
        }
        if (portal.getKbUid() == null || portal.getKbUid().isBlank()) {
            log.warn("onPortalCreateEvent skipped: missing kbUid for portal uid={}", portal.getUid());
            return;
        }
        log.info("portal - created: {}", portal.getUid());
        portalStaticService.updatePortalPost(portal.getUid());
    }

    @EventListener
    public void onPortalUpdateEvent(PortalUpdateEvent event) {
        PortalEntity portal = event.getPortal();
        if (portal == null) {
            return;
        }
        if (portal.getKbUid() == null || portal.getKbUid().isBlank()) {
            log.warn("onPortalUpdateEvent skipped: missing kbUid for portal uid={}", portal.getUid());
            return;
        }
        log.info("portal - updated: {}", portal.getUid());
        portalStaticService.updatePortalPost(portal.getUid());
    }

    @EventListener
    public void onPortalDeleteEvent(PortalDeleteEvent event) {
        PortalEntity portal = event.getPortal();
        if (portal == null) {
            return;
        }
        if (portal.getKbUid() == null || portal.getKbUid().isBlank()) {
            log.warn("onPortalDeleteEvent skipped: missing kbUid for portal uid={}", portal.getUid());
            return;
        }
        log.info("portal - deleted: {}", portal.getUid());
        portalStaticService.deletePortalPostStatic(portal.getKbUid(), portal.getUid());
    }

    @EventListener
    public void onKbaseCreateEvent(KbaseCreateEvent event) {
        KbaseEntity kbase = event.getKbase();
        if (kbase == null || !KbaseTypeEnum.PORTAL.name().equals(kbase.getType())) {
            return;
        }
        portalStaticService.updatePortalKbase(kbase.getUid());
    }

    @EventListener
    public void onKbaseUpdateEvent(KbaseUpdateEvent event) {
        KbaseEntity kbase = event.getKbase();
        if (kbase == null || !KbaseTypeEnum.PORTAL.name().equals(kbase.getType())) {
            return;
        }
        portalStaticService.updatePortalKbase(kbase.getUid());
    }

    @EventListener
    public void onCategoryCreateEvent(CategoryCreateEvent event) {
        CategoryEntity category = event.getCategory();
        if (category == null || category.getKbUid() == null || category.getKbUid().isBlank()) {
            return;
        }
        Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(category.getKbUid());
        if (kbaseOptional.isPresent() && KbaseTypeEnum.PORTAL.name().equals(kbaseOptional.get().getType())) {
            portalStaticService.updatePortalKbase(category.getKbUid());
        }
    }

    @EventListener
    public void onCategoryUpdateEvent(CategoryUpdateEvent event) {
        CategoryEntity category = event.getCategory();
        if (category == null || category.getKbUid() == null || category.getKbUid().isBlank()) {
            return;
        }
        Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(category.getKbUid());
        if (kbaseOptional.isPresent() && KbaseTypeEnum.PORTAL.name().equals(kbaseOptional.get().getType())) {
            portalStaticService.updatePortalKbase(category.getKbUid());
        }
    }

 
}

