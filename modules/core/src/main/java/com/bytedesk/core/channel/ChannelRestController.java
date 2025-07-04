/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:06:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-24 12:57:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.channel;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/channel")
// @Tag(name = "channel - 频道", description = "channel apis")
public class ChannelRestController extends BaseRestController<ChannelRequest> {

    private final ChannelRestService channelService;

    @Override
    public ResponseEntity<?> queryByOrg(ChannelRequest request) {
        
        Page<ChannelResponse> channelPage = channelService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(channelPage));
    }

    @Override
    public ResponseEntity<?> queryByUser(ChannelRequest request) {
        
        Page<ChannelResponse> channelPage = channelService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(channelPage));
    }

    @Override
    public ResponseEntity<?> queryByUid(ChannelRequest request) {
        
        ChannelResponse channelResponse = channelService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(channelResponse));
    }

    @Override
    public ResponseEntity<?> create(ChannelRequest request) {
        
        ChannelResponse channelResponse = channelService.create(request);

        return ResponseEntity.ok(JsonResult.success(channelResponse));
    }

    @Override
    public ResponseEntity<?> update(ChannelRequest request) {
        
        ChannelResponse channelResponse = channelService.update(request);

        return ResponseEntity.ok(JsonResult.success(channelResponse));
    }

    @Override
    public ResponseEntity<?> delete(ChannelRequest request) {
        
        channelService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(ChannelRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

}
