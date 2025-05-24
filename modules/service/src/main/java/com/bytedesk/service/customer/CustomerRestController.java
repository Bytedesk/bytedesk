/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:06:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 10:45:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "客户管理", description = "客户管理相关接口")
@RestController
@RequestMapping("/api/v1/customer")
@AllArgsConstructor
public class CustomerRestController extends BaseRestController<CustomerRequest> {

    private final CustomerRestService customerService;

    @Operation(summary = "查询组织下的客户", description = "根据组织ID查询客户列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = CustomerResponse.class)))
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(CustomerRequest request) {
        
        Page<CustomerResponse> response = customerService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "查询用户下的客户", description = "根据用户ID查询客户列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = CustomerResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(CustomerRequest request) {
        
        Page<CustomerResponse> response = customerService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "创建客户", description = "创建新的客户")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = CustomerResponse.class)))
    @Override
    public ResponseEntity<?> create(CustomerRequest request) {
        
        CustomerResponse response = customerService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "更新客户", description = "更新客户信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = CustomerResponse.class)))
    @Override
    public ResponseEntity<?> update(CustomerRequest request) {
        
        CustomerResponse response = customerService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "删除客户", description = "删除指定的客户")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(CustomerRequest request) {
        
        customerService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }

    @Operation(summary = "导出客户", description = "导出客户数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    public Object export(CustomerRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            customerService,
            CustomerExcel.class,
            "客户信息",
            "customer"
        );
    }

    @Operation(summary = "查询指定客户", description = "根据UID查询客户详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = CustomerResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(CustomerRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    
    
}
