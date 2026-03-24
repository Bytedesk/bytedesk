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
package com.bytedesk.crm.contract;

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
@RequestMapping("/api/v1/contract")
@AllArgsConstructor
@Tag(name = "Contract Management", description = "Contract management APIs for organizing and categorizing content with contracts")
@Description("Contract Management Controller - Content contractging and categorization APIs")
public class ContractRestController extends BaseRestController<ContractRequest, ContractRestService> {

    private final ContractRestService contractRestService;

    @ActionAnnotation(title = I18Consts.I18N_CONTRACT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query contract by org")
    @Operation(summary = "Query Contracts by Organization", description = "Retrieve contracts for the current organization")
    @PreAuthorize(ContractPermissions.HAS_CONTRACT_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(ContractRequest request) {
        
        Page<ContractResponse> contracts = contractRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(contracts));
    }

    @ActionAnnotation(title = I18Consts.I18N_CONTRACT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query contract by user")
    @Operation(summary = "Query Contracts by User", description = "Retrieve contracts for the current user")
    @PreAuthorize(ContractPermissions.HAS_CONTRACT_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(ContractRequest request) {
        
        Page<ContractResponse> contracts = contractRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(contracts));
    }

    @ActionAnnotation(title = I18Consts.I18N_CONTRACT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query contract by uid")
    @Operation(summary = "Query Contract by UID", description = "Retrieve a specific contract by its unique identifier")
    @PreAuthorize(ContractPermissions.HAS_CONTRACT_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(ContractRequest request) {
        
        ContractResponse contract = contractRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(contract));
    }

    @ActionAnnotation(title = I18Consts.I18N_CONTRACT, action = I18Consts.I18N_ACTION_CREATE, description = "create contract")
    @Operation(summary = "Create Contract", description = "Create a new contract")
    @Override
    @PreAuthorize(ContractPermissions.HAS_CONTRACT_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ContractRequest request) {
        
        ContractResponse contract = contractRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(contract));
    }

    @ActionAnnotation(title = I18Consts.I18N_CONTRACT, action = I18Consts.I18N_ACTION_UPDATE, description = "update contract")
    @Operation(summary = "Update Contract", description = "Update an existing contract")
    @Override
    @PreAuthorize(ContractPermissions.HAS_CONTRACT_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody ContractRequest request) {
        
        ContractResponse contract = contractRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(contract));
    }

    @ActionAnnotation(title = I18Consts.I18N_CONTRACT, action = I18Consts.I18N_ACTION_DELETE, description = "delete contract")
    @Operation(summary = "Delete Contract", description = "Delete a contract")
    @Override
    @PreAuthorize(ContractPermissions.HAS_CONTRACT_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody ContractRequest request) {
        
        contractRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_CONTRACT, action = I18Consts.I18N_ACTION_EXPORT, description = "export contract")
    @Operation(summary = "Export Contracts", description = "Export contracts to Excel format")
    @Override
    @PreAuthorize(ContractPermissions.HAS_CONTRACT_EXPORT)
    @GetMapping("/export")
    public Object export(ContractRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            contractRestService,
            ContractExcel.class,
            "合同",
            "contract"
        );
    }

    
    
}