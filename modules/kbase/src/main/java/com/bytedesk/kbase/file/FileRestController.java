/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-23 14:20:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.file;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/file")
@AllArgsConstructor
public class FileRestController extends BaseRestController<FileRequest> {

    private final FileRestService fileService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(FileRequest request) {
        
        Page<FileResponse> files = fileService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(files));
    }

    @Override
    public ResponseEntity<?> queryByUser(FileRequest request) {
        
        Page<FileResponse> files = fileService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(files));
    }

    @Override
    public ResponseEntity<?> create(FileRequest request) {
        
        FileResponse file = fileService.create(request);

        return ResponseEntity.ok(JsonResult.success(file));
    }

    @Override
    public ResponseEntity<?> update(FileRequest request) {
        
        FileResponse file = fileService.update(request);

        return ResponseEntity.ok(JsonResult.success(file));
    }

    @Override
    public ResponseEntity<?> delete(FileRequest request) {
        
        fileService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}