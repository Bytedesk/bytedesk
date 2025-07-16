/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 16:17:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/org")
@Tag(name = "Organization", description = "Organization management APIs")
public class OrganizationRestController extends BaseRestController<OrganizationRequest> {

    private final OrganizationRestService organizationRestService;

    @Override
    public ResponseEntity<?> queryByOrg(OrganizationRequest request) {
        
        Page<OrganizationResponse> orgPage = organizationRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(orgPage));
    }

    @Override
    public ResponseEntity<?> queryByUser(OrganizationRequest request) {
        
        Page<OrganizationResponse> orgPage = organizationRestService.queryByUser(request);
        //
        return ResponseEntity.ok(JsonResult.success(orgPage));
    }

    @GetMapping("/uid")
    public ResponseEntity<?> queryByUid(OrganizationRequest request) {
        //
        Optional<OrganizationEntity> org = organizationRestService.findByUid(request.getUid());
        if (!org.isPresent()) {
            return ResponseEntity.ok(JsonResult.error("组织不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(organizationRestService.convertToResponse(org.get())));
    }

    @ActionAnnotation(title = "组织", action = "新建", description = "organization create")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody OrganizationRequest request) {
        //
        OrganizationResponse response = organizationRestService.create(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    // create/by/admin
    @ActionAnnotation(title = "组织", action = "新建", description = "organization create by admin")
    @PostMapping("/create/by/admin")
    public ResponseEntity<?> createByAdmin(@RequestBody OrganizationRequest request) {
        //
        OrganizationResponse response = organizationRestService.createByAdmin(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "组织", action = "更新", description = "organization update")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody OrganizationRequest request) {
        //
        OrganizationResponse response = organizationRestService.update(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("更新组织失败"));
        }
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    
    @Override
    public ResponseEntity<?> delete(OrganizationRequest request) {
        
        organizationRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(OrganizationRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

}
