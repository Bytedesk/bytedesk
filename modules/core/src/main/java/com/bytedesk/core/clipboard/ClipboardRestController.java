/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-05 22:19:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:35:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.clipboard;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/clipboard")
@AllArgsConstructor
@Tag(name = "Clipboard Management", description = "Clipboard management APIs for sharing and managing clipboard content")
public class ClipboardRestController extends BaseRestController<ClipboardRequest, ClipboardRestService> {

    private final ClipboardRestService clipboardService;

    @Override
    public ResponseEntity<?> queryByOrg(ClipboardRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Operation(summary = "Query Clipboard by User", description = "Retrieve clipboard items for the current user")
    @Override
    public ResponseEntity<?> queryByUser(ClipboardRequest request) {
        
        Page<ClipboardResponse> page = clipboardService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Create Clipboard Item", description = "Create a new clipboard item")
    @Override
    public ResponseEntity<?> create(ClipboardRequest request) {
        
        ClipboardResponse response = clipboardService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Update Clipboard Item", description = "Update an existing clipboard item")
    @Override
    public ResponseEntity<?> update(ClipboardRequest request) {
        
        ClipboardResponse response = clipboardService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Delete Clipboard Item", description = "Delete a clipboard item")
    @Override
    public ResponseEntity<?> delete(ClipboardRequest request) {
        
        clipboardService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(ClipboardRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(ClipboardRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

}
