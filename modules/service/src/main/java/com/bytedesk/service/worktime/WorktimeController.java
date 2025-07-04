/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-18 14:46:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:34:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.worktime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/worktime")
public class WorktimeController extends BaseRestController<WorktimeRequest> {

    private final WorktimeService worktimeService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(WorktimeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public ResponseEntity<?> queryByUser(WorktimeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @RequestMapping("create")
    @Override
    public ResponseEntity<?> create(@RequestBody WorktimeRequest request) {
        
        WorktimeResponse response = worktimeService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @RequestMapping("update")
    @Override
    public ResponseEntity<?> update(@RequestBody WorktimeRequest request) {
        
        WorktimeResponse response = worktimeService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @RequestMapping("delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody WorktimeRequest request) {
        
        worktimeService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }

    @Override
    public Object export(WorktimeRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(WorktimeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
