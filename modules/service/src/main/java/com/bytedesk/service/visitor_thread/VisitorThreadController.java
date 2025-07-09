/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:08:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:34:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/visitor/api/v1/visitor_thread")
@AllArgsConstructor
@Tag(name = "访客会话管理", description = "访客会话管理相关接口")
public class VisitorThreadController extends BaseRestController<VisitorThreadRequest> {

    private VisitorThreadService visitorThreadService;
 
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "根据组织查询访客会话", description = "管理员查询组织的访客会话")
    @Override
    public ResponseEntity<?> queryByOrg(VisitorThreadRequest request) {
        
        Page<VisitorThreadResponse> page = visitorThreadService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "根据用户查询访客会话", description = "查询用户的访客会话")
    @Override
    public ResponseEntity<?> queryByUser(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @Operation(summary = "创建访客会话", description = "创建新的访客会话")
    @Override
    public ResponseEntity<?> create(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Operation(summary = "更新访客会话", description = "更新现有的访客会话")
    @Override
    public ResponseEntity<?> update(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Operation(summary = "删除访客会话", description = "删除指定的访客会话")
    @Override
    public ResponseEntity<?> delete(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Operation(summary = "导出访客会话", description = "导出访客会话数据")
    @Override
    public Object export(VisitorThreadRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "根据UID查询访客会话", description = "通过UID查询具体的访客会话")
    @Override
    public ResponseEntity<?> queryByUid(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
