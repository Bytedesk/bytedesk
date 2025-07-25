/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-01 09:28:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:36:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
@Tag(name = "Notice Management", description = "Notice management APIs for system announcements and notifications")
@Description("Notice Management Controller - System notice and announcement management APIs")
public class NoticeRestController extends BaseRestController<NoticeRequest> {

    private final NoticeRestService noticeRestService;

    @Operation(summary = "Query Notices by Organization", description = "Retrieve notices for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(NoticeRequest request) {

        Page<NoticeResponse> page = noticeRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Notices by User", description = "Retrieve notices for the current user")
    @Override
    public ResponseEntity<?> queryByUser(NoticeRequest request) {

        Page<NoticeResponse> page = noticeRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Create Notice", description = "Create a new notice")
    @Override
    public ResponseEntity<?> create(NoticeRequest request) {

        NoticeResponse notice = noticeRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(notice));
    }

    @Operation(summary = "Update Notice", description = "Update an existing notice")
    @Override
    public ResponseEntity<?> update(NoticeRequest request) {

        NoticeResponse notice = noticeRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(notice));
    }

    @Operation(summary = "Delete Notice", description = "Delete a notice")
    @Override
    public ResponseEntity<?> delete(NoticeRequest request) {

        noticeRestService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(NoticeRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(NoticeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
