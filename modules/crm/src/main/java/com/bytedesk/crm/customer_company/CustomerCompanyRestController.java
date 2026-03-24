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
package com.bytedesk.crm.customer_company;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/customer_company")
@AllArgsConstructor
@Tag(name = "CustomerCompany Management", description = "CustomerCompany management APIs for organizing and categorizing content with customer_companys")
@Description("CustomerCompany Management Controller - Content customer_companyging and categorization APIs")
public class CustomerCompanyRestController extends BaseRestController<CustomerCompanyRequest, CustomerCompanyRestService> {

    private final CustomerCompanyRestService customer_companyRestService;

    @ActionAnnotation(title = I18Consts.I18N_CUSTOMER_COMPANY, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query customer_company by org")
    @Operation(summary = "Query CustomerCompanys by Organization", description = "Retrieve customer_companys for the current organization")
    @PreAuthorize(CustomerCompanyPermissions.HAS_CUSTOMER_COMPANY_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(CustomerCompanyRequest request) {
        
        Page<CustomerCompanyResponse> customer_companys = customer_companyRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(customer_companys));
    }

    @ActionAnnotation(title = I18Consts.I18N_CUSTOMER_COMPANY, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query customer_company by user")
    @Operation(summary = "Query CustomerCompanys by User", description = "Retrieve customer_companys for the current user")
    @PreAuthorize(CustomerCompanyPermissions.HAS_CUSTOMER_COMPANY_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(CustomerCompanyRequest request) {
        
        Page<CustomerCompanyResponse> customer_companys = customer_companyRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(customer_companys));
    }

    @ActionAnnotation(title = I18Consts.I18N_CUSTOMER_COMPANY, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query customer_company by uid")
    @Operation(summary = "Query CustomerCompany by UID", description = "Retrieve a specific customer_company by its unique identifier")
    @PreAuthorize(CustomerCompanyPermissions.HAS_CUSTOMER_COMPANY_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(CustomerCompanyRequest request) {
        
        CustomerCompanyResponse customer_company = customer_companyRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(customer_company));
    }

    @ActionAnnotation(title = I18Consts.I18N_CUSTOMER_COMPANY, action = I18Consts.I18N_ACTION_CREATE, description = "create customer_company")
    @Operation(summary = "Create CustomerCompany", description = "Create a new customer_company")
    @Override
    @PreAuthorize(CustomerCompanyPermissions.HAS_CUSTOMER_COMPANY_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CustomerCompanyRequest request) {
        
        CustomerCompanyResponse customer_company = customer_companyRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(customer_company));
    }

    @ActionAnnotation(title = I18Consts.I18N_CUSTOMER_COMPANY, action = I18Consts.I18N_ACTION_UPDATE, description = "update customer_company")
    @Operation(summary = "Update CustomerCompany", description = "Update an existing customer_company")
    @Override
    @PreAuthorize(CustomerCompanyPermissions.HAS_CUSTOMER_COMPANY_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody CustomerCompanyRequest request) {
        
        CustomerCompanyResponse customer_company = customer_companyRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(customer_company));
    }

    @ActionAnnotation(title = I18Consts.I18N_CUSTOMER_COMPANY, action = I18Consts.I18N_ACTION_DELETE, description = "delete customer_company")
    @Operation(summary = "Delete CustomerCompany", description = "Delete a customer_company")
    @Override
    @PreAuthorize(CustomerCompanyPermissions.HAS_CUSTOMER_COMPANY_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody CustomerCompanyRequest request) {
        
        customer_companyRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_CUSTOMER_COMPANY, action = I18Consts.I18N_ACTION_EXPORT, description = "export customer_company")
    @Operation(summary = "Export CustomerCompanys", description = "Export customer_companys to Excel format")
    @Override
    @PreAuthorize(CustomerCompanyPermissions.HAS_CUSTOMER_COMPANY_EXPORT)
    @GetMapping("/export")
    public Object export(CustomerCompanyRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            customer_companyRestService,
            CustomerCompanyExcel.class,
            "客户公司",
            "customer-company"
        );
    }

    
    
}