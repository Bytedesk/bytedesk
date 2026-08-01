/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:06:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 08:35:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.crm.customer;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Customer Management", description = "Customer management APIs")
@RestController
@RequestMapping("/api/v1/customer")
@AllArgsConstructor
@Description("Customer Management Controller - Customer information and relationship management APIs")
public class CustomerRestController extends BaseRestController<CustomerRequest, CustomerRestService> {

    private final CustomerRestService customerRestService;

    @Operation(summary = "Query Customers by Organization", description = "Retrieve customer list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = CustomerResponse.class)))
    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(CustomerRequest request) {
        
        Page<CustomerResponse> response = customerRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Query Customers by User", description = "Retrieve customer list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = CustomerResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(CustomerRequest request) {
        
        Page<CustomerResponse> response = customerRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Query Customer by UID", description = "Retrieve customer details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = CustomerResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(CustomerRequest request) {
        
        CustomerResponse response = customerRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    // query/visitorUid
    @Operation(summary = "Query Customers by Visitor UID", description = "Retrieve customer list by visitor UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = CustomerResponse.class)))
    @GetMapping("/query/visitorUid")
    public ResponseEntity<?> queryByVisitorUid(CustomerRequest request) {
        
        CustomerResponse response = customerRestService.queryByVisitorUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Create Customer", description = "Create a new customer")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = CustomerResponse.class)))
    @Override
    public ResponseEntity<?> create(CustomerRequest request) {
        
        CustomerResponse response = customerRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Update Customer", description = "Update customer information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = CustomerResponse.class)))
    @Override
    public ResponseEntity<?> update(CustomerRequest request) {
        
        CustomerResponse response = customerRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Delete Customer", description = "Delete the specified customer")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @Override
    public ResponseEntity<?> delete(CustomerRequest request) {
        
        customerRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }

    @Operation(summary = "Export Customers", description = "Export customer data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @Override
    public Object export(CustomerRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            customerRestService,
            CustomerExcel.class,
            "客户信息",
            "customer"
        );
    }
    
    
}
