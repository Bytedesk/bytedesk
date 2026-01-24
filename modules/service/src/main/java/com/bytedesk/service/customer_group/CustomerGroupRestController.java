/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer_group;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/customer_group")
@AllArgsConstructor
@Tag(name = "CustomerGroup Management", description = "CustomerGroup management APIs for organizing and categorizing content with customer_groups")
@Description("CustomerGroup Management Controller - Content customer_groupging and categorization APIs")
public class CustomerGroupRestController extends BaseRestController<CustomerGroupRequest, CustomerGroupRestService> {

    private final CustomerGroupRestService customer_groupRestService;

    @ActionAnnotation(title = "CustomerGroup", action = "组织查询", description = "query customer_group by org")
    @Operation(summary = "Query CustomerGroups by Organization", description = "Retrieve customer_groups for the current organization")
    @PreAuthorize(CustomerGroupPermissions.HAS_CUSTOMER_GROUP_READ)
    @Override
    public ResponseEntity<?> queryByOrg(CustomerGroupRequest request) {
        
        Page<CustomerGroupResponse> customer_groups = customer_groupRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(customer_groups));
    }

    @ActionAnnotation(title = "CustomerGroup", action = "用户查询", description = "query customer_group by user")
    @Operation(summary = "Query CustomerGroups by User", description = "Retrieve customer_groups for the current user")
    @PreAuthorize(CustomerGroupPermissions.HAS_CUSTOMER_GROUP_READ)
    @Override
    public ResponseEntity<?> queryByUser(CustomerGroupRequest request) {
        
        Page<CustomerGroupResponse> customer_groups = customer_groupRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(customer_groups));
    }

    @ActionAnnotation(title = "CustomerGroup", action = "查询详情", description = "query customer_group by uid")
    @Operation(summary = "Query CustomerGroup by UID", description = "Retrieve a specific customer_group by its unique identifier")
    @PreAuthorize(CustomerGroupPermissions.HAS_CUSTOMER_GROUP_READ)
    @Override
    public ResponseEntity<?> queryByUid(CustomerGroupRequest request) {
        
        CustomerGroupResponse customer_group = customer_groupRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(customer_group));
    }

    @ActionAnnotation(title = "CustomerGroup", action = "新建", description = "create customer_group")
    @Operation(summary = "Create CustomerGroup", description = "Create a new customer_group")
    @Override
    @PreAuthorize(CustomerGroupPermissions.HAS_CUSTOMER_GROUP_CREATE)
    public ResponseEntity<?> create(CustomerGroupRequest request) {
        
        CustomerGroupResponse customer_group = customer_groupRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(customer_group));
    }

    @ActionAnnotation(title = "CustomerGroup", action = "更新", description = "update customer_group")
    @Operation(summary = "Update CustomerGroup", description = "Update an existing customer_group")
    @Override
    @PreAuthorize(CustomerGroupPermissions.HAS_CUSTOMER_GROUP_UPDATE)
    public ResponseEntity<?> update(CustomerGroupRequest request) {
        
        CustomerGroupResponse customer_group = customer_groupRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(customer_group));
    }

    @ActionAnnotation(title = "CustomerGroup", action = "删除", description = "delete customer_group")
    @Operation(summary = "Delete CustomerGroup", description = "Delete a customer_group")
    @Override
    @PreAuthorize(CustomerGroupPermissions.HAS_CUSTOMER_GROUP_DELETE)
    public ResponseEntity<?> delete(CustomerGroupRequest request) {
        
        customer_groupRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "CustomerGroup", action = "导出", description = "export customer_group")
    @Operation(summary = "Export CustomerGroups", description = "Export customer_groups to Excel format")
    @Override
    @PreAuthorize(CustomerGroupPermissions.HAS_CUSTOMER_GROUP_EXPORT)
    @GetMapping("/export")
    public Object export(CustomerGroupRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            customer_groupRestService,
            CustomerGroupExcel.class,
            "CustomerGroup",
            "customer_group"
        );
    }

    
    
}