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

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/organization")
@Tag(name = "Organization", description = "Organization management APIs")
public class OrganizationRestController extends BaseRestController<OrganizationRequest, OrganizationRestService> {

    private final OrganizationRestService organizationRestService;

    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query organization by org")
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(OrganizationRequest request) {
        
        Page<OrganizationResponse> orgPage = organizationRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(orgPage));
    }

    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query organization by user")
    @PreAuthorize(OrganizationPermissions.HAS_ORGANIZATION_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(OrganizationRequest request) {
        
        Page<OrganizationResponse> orgPage = organizationRestService.queryByUser(request);
        //
        return ResponseEntity.ok(JsonResult.success(orgPage));
    }

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

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_CREATE, description = "organization create by admin")
    @PostMapping("/create/by/super")
    public ResponseEntity<?> createBySuper(@RequestBody OrganizationRequest request) {
        //
        OrganizationResponse response = organizationRestService.createBySuper(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

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

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_UPDATE, description = "organization update by super")
    @PostMapping("/update/by/super")
    public ResponseEntity<?> updateBySuper(@RequestBody OrganizationRequest request) {
        //
        OrganizationResponse response = organizationRestService.updateBySuper(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(OrganizationPermissions.HAS_ORGANIZATION_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION, action = I18Consts.I18N_ACTION_DELETE, description = "organization delete")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody OrganizationRequest request) {
        
        organizationRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

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
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> searchForJoin(OrganizationRequest request) {
        Page<OrganizationResponseSimple> page = organizationRestService.searchForJoin(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

}
