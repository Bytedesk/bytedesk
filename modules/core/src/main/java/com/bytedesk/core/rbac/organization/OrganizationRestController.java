/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 09:36:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/organization")
@Tag(name = "Organization", description = "Organization management APIs")
public class OrganizationRestController extends BaseRestController<OrganizationRequest, OrganizationRestService> {

    private final OrganizationRestService organizationRestService;

    @Operation(summary = "Query Organizations by Organization", description = "Retrieve organizations for the current organization")
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query organization by org")
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(OrganizationRequest request) {
        
        Page<OrganizationResponse> orgPage = organizationRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(orgPage));
    }

    @Operation(summary = "Query Organizations by User", description = "Retrieve organizations for the current user")
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query organization by user")
    @PreAuthorize(OrganizationPermissions.HAS_ORGANIZATION_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(OrganizationRequest request) {
        
        Page<OrganizationResponse> orgPage = organizationRestService.queryByUser(request);
        //
        return ResponseEntity.ok(JsonResult.success(orgPage));
    }

    @Operation(summary = "Query Organizations by UID", description = "Retrieve a specific organization entry by its unique identifier")
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query organization by uid")
    @PreAuthorize(OrganizationPermissions.HAS_ORGANIZATION_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(OrganizationRequest request) {
        //
        OrganizationResponse response = organizationRestService.queryByUid(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Create Organizations", description = "Create a new organization entry")
    @PreAuthorize(OrganizationPermissions.HAS_ORGANIZATION_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_CREATE, description = "organization create")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody OrganizationRequest request) {
        //
        OrganizationResponse response = organizationRestService.create(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Create Organization by Super Admin", description = "Create a new organization by super admin")
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_CREATE, description = "organization create by admin")
    @PostMapping("/create/by/super")
    public ResponseEntity<?> createBySuper(@RequestBody OrganizationRequest request) {
        //
        OrganizationResponse response = organizationRestService.createBySuper(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Update Organizations", description = "Update an existing organization entry")
    @PreAuthorize(OrganizationPermissions.HAS_ORGANIZATION_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_UPDATE, description = "organization update")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody OrganizationRequest request) {
        //
        OrganizationResponse response = organizationRestService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Update Organization by Super Admin", description = "Update an organization by super admin")
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_UPDATE, description = "organization update by super")
    @PostMapping("/update/by/super")
    public ResponseEntity<?> updateBySuper(@RequestBody OrganizationRequest request) {
        //
        OrganizationResponse response = organizationRestService.updateBySuper(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 更换组织管理员（统一入口）。
     * 权限：超级管理员 或 该组织管理员本人（服务层按 request.uid 查组织后严格比对，
     * 不使用 isOrgAdminOrSuperUser，该判定不限定组织）。
     */
    @Operation(summary = "Transfer Organization Admin", description = "Transfer the administrator role of an organization")
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_UPDATE, description = "organization transfer admin")
    @PostMapping("/transfer/admin")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> transferAdmin(@RequestBody OrganizationRequest request) {
        //
        OrganizationResponse response = organizationRestService.transferAdmin(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Update Organization Enabled Status by Super Admin", description = "Enable or disable an organization by super admin")
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_UPDATE, description = "organization enabled update by super")
    @PostMapping("/update/enabled/by/super")
    public ResponseEntity<?> updateEnabledBySuper(@RequestBody OrganizationRequest request) {

        OrganizationResponse response = organizationRestService.updateEnabledBySuper(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Delete Organizations", description = "Delete a organization entry")
    @PreAuthorize(OrganizationPermissions.HAS_ORGANIZATION_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_DELETE, description = "organization delete")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody OrganizationRequest request) {
        
        organizationRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Organizations", description = "Export organizations to Excel format")
    @PreAuthorize(OrganizationPermissions.HAS_ORGANIZATION_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_EXPORT, description = "organization export")
    @GetMapping("/export")
    @Override
    public Object export(OrganizationRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    /**
     * 搜索组织（用于“申请加入组织”场景），允许用户在没有 currentOrganization 的情况下调用。
     */
    @Operation(summary = "Search Organizations to Join", description = "Search public organizations available to join")
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_SEARCH, description = "search organization")
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> searchForJoin(OrganizationRequest request) {
        Page<OrganizationResponseSimple> page = organizationRestService.searchForJoin(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 平台查询组织列表（desktop「平台查询」组织选择器数据源）。
     * 仅超级管理员/平台默认组织成员可用（服务层校验），返回简版字段。
     */
    @Operation(summary = "Query Organizations by Platform", description = "Retrieve organizations filtered by platform")
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query organization for platform cross-org query")
    @GetMapping("/query/platform")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> queryByPlatform(OrganizationRequest request) {
        Page<OrganizationResponseSimple> page = organizationRestService.queryByPlatform(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

}
