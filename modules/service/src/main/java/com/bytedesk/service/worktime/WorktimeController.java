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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Description;

@Tag(name = "工作时间管理", description = "工作时间管理相关接口")
@RestController
@RequestMapping("/api/v1/worktime")
@AllArgsConstructor
@Description("Worktime Management Controller - Agent working hours and schedule management APIs")
public class WorktimeController extends BaseRestController<WorktimeRequest> {

    private final WorktimeService worktimeService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "根据组织查询工作时间", description = "管理员查询组织的工作时间设置")
    @Override
    public ResponseEntity<?> queryByOrg(WorktimeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Operation(summary = "根据用户查询工作时间", description = "查询用户的工作时间设置")
    @Override
    public ResponseEntity<?> queryByUser(WorktimeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @RequestMapping("create")
    @Operation(summary = "创建工作时间", description = "创建新的工作时间设置")
    @Override
    public ResponseEntity<?> create(@RequestBody WorktimeRequest request) {
        
        WorktimeResponse response = worktimeService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @RequestMapping("update")
    @Operation(summary = "更新工作时间", description = "更新现有的工作时间设置")
    @Override
    public ResponseEntity<?> update(@RequestBody WorktimeRequest request) {
        
        WorktimeResponse response = worktimeService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @RequestMapping("delete")
    @Operation(summary = "删除工作时间", description = "删除指定的工作时间设置")
    @Override
    public ResponseEntity<?> delete(@RequestBody WorktimeRequest request) {
        
        worktimeService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }

    @Operation(summary = "导出工作时间", description = "导出工作时间数据")
    @Override
    public Object export(WorktimeRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "根据UID查询工作时间", description = "通过UID查询具体的工作时间设置")
    @Override
    public ResponseEntity<?> queryByUid(WorktimeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
