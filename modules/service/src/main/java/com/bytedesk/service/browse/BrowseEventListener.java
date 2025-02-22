/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-23 21:24:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 12:06:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.browse;

import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzDay0Event;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor.event.VisitorBrowseEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class BrowseEventListener {

    private final BrowseRestService browseRestService;

    private final ModelMapper modelMapper;

    @EventListener
    public void handleVisitorBrowseEvent(VisitorBrowseEvent event) {
        log.info("Visitor browse event: {}", event);
        VisitorRequest visitorRequest = event.getVisitorRequest();
        // String url = visitorRequest.getUrl();
        // String referrer = visitorRequest.getReferrer();
        // String title = visitorRequest.getTitle();
        // String ip = visitorRequest.getIp();
        // String ipLocation = visitorRequest.getIpLocation();
        // String visitorUid = visitorRequest.getUid();
        String orgUid = visitorRequest.getOrgUid();
        //
        // 保存浏览记录
        BrowseRequest request = modelMapper.map(visitorRequest, BrowseRequest.class);
        // BrowseRequest request = BrowseRequest.builder()
        //     .referrer(referrer)
        //     .url(url)
        //     .title(title)
        //     .ip(ip)
        //     .ipLocation(ipLocation)
        //     .visitorUid(visitorUid)
        //     .build();
        request.setOrgUid(orgUid);
        //
        browseRestService.create(request);
    }
    
    @EventListener
    public void handleQuartzDay0Event(QuartzDay0Event event) {  
        log.info("Quartz day 0 event: {}", event);
        // 只保留当天数据，删除过期数据
        browseRestService.deleteAll();
    }

    
}

