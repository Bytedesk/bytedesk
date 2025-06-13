/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 21:48:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.webrtc;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/freeswitch/webrtc")
@AllArgsConstructor
public class FreeswitchWebRTCRestController extends BaseRestController<FreeswitchWebRTCRequest> {

    private final FreeswitchWebRTCRestService freeswitchWebRTCRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "视频客服", action = "组织查询", description = "query webrtc by org")
    @Override
    public ResponseEntity<?> queryByOrg(FreeswitchWebRTCRequest request) {
        
        Page<FreeswitchWebRTCResponse> webrtcPage = freeswitchWebRTCRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(webrtcPage));
    }

    @ActionAnnotation(title = "视频客服", action = "用户查询", description = "query webrtc by user")
    @Override
    public ResponseEntity<?> queryByUser(FreeswitchWebRTCRequest request) {
        
        Page<FreeswitchWebRTCResponse> webrtcPage = freeswitchWebRTCRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(webrtcPage));
    }

    @ActionAnnotation(title = "视频客服", action = "查询详情", description = "query webrtc by uid")
    @Override
    public ResponseEntity<?> queryByUid(FreeswitchWebRTCRequest request) {
        
        FreeswitchWebRTCResponse webrtc = freeswitchWebRTCRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(webrtc));
    }

    @ActionAnnotation(title = "视频客服", action = "新建", description = "create webrtc")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(FreeswitchWebRTCRequest request) {
        
        FreeswitchWebRTCResponse webrtc = freeswitchWebRTCRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(webrtc));
    }

    @ActionAnnotation(title = "视频客服", action = "更新", description = "update webrtc")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(FreeswitchWebRTCRequest request) {
        
        FreeswitchWebRTCResponse webrtc = freeswitchWebRTCRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(webrtc));
    }

    @ActionAnnotation(title = "视频客服", action = "删除", description = "delete webrtc")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(FreeswitchWebRTCRequest request) {
        
        freeswitchWebRTCRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "视频客服", action = "导出", description = "export webrtc")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(FreeswitchWebRTCRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            freeswitchWebRTCRestService,
            FreeswitchWebRTCExcel.class,
            "视频客服",
            "webrtc"
        );
    }

    
    
}