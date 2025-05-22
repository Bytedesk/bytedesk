/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-22 11:21:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.organization_apply;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/org/apply")
@AllArgsConstructor
public class OrganizationApplyRestController extends BaseRestController<OrganizationApplyRequest> {

    private final OrganizationApplyRestService organizationRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "加入组织申请", action = "组织查询", description = "query organizationApply by org")
    @Override
    public ResponseEntity<?> queryByOrg(OrganizationApplyRequest request) {
        
        Page<OrganizationApplyResponse> organizationApplies = organizationRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(organizationApplies));
    }

    @ActionAnnotation(title = "加入组织申请", action = "用户查询", description = "query organizationApply by user")
    @Override
    public ResponseEntity<?> queryByUser(OrganizationApplyRequest request) {
        
        Page<OrganizationApplyResponse> organizationApplies = organizationRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(organizationApplies));
    }

    @ActionAnnotation(title = "加入组织申请", action = "查询详情", description = "query organizationApply by uid")
    @Override
    public ResponseEntity<?> queryByUid(OrganizationApplyRequest request) {
        
        OrganizationApplyResponse organizationApply = organizationRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(organizationApply));
    }

    @ActionAnnotation(title = "加入组织申请", action = "新建", description = "create organizationApply")
    @Override
    // @PreAuthorize("hasAuthority('organizationApply_CREATE')")
    public ResponseEntity<?> create(OrganizationApplyRequest request) {
        
        OrganizationApplyResponse organizationApply = organizationRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(organizationApply));
    }

    @ActionAnnotation(title = "加入组织申请", action = "更新", description = "update organizationApply")
    @Override
    // @PreAuthorize("hasAuthority('organizationApply_UPDATE')")
    public ResponseEntity<?> update(OrganizationApplyRequest request) {
        
        OrganizationApplyResponse organizationApply = organizationRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(organizationApply));
    }

    @ActionAnnotation(title = "加入组织申请", action = "删除", description = "delete organizationApply")
    @Override
    // @PreAuthorize("hasAuthority('organizationApply_DELETE')")
    public ResponseEntity<?> delete(OrganizationApplyRequest request) {
        
        organizationRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "加入组织申请", action = "导出", description = "export organizationApply")
    @Override
    // @PreAuthorize("hasAuthority('organizationApply_EXPORT')")
    @GetMapping("/export")
    public Object export(OrganizationApplyRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            organizationRestService,
            OrganizationApplyExcel.class,
            "加入组织申请",
            "organizationApply"
        );
    }

    
    
}