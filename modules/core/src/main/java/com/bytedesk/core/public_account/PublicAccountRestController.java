/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:06:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:25:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.public_account;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notice/account")
// @Tag(name = "Notice Account Management", description = "Notice account APIs")
public class PublicAccountRestController extends BaseRestController<PublicAccountRequest, PublicAccountRestService> {

    private final PublicAccountRestService channelService;

    @Operation(summary = "Query Public Accounts by Organization", description = "Retrieve public accounts for the current organization")
    @ActionAnnotation(title = I18Consts.I18N_PUBLIC_ACCOUNT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query public account")
    @Override
    public ResponseEntity<?> queryByOrg(PublicAccountRequest request) {
        
        Page<PublicAccountResponse> channelPage = channelService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(channelPage));
    }

    @Operation(summary = "Query Public Accounts by User", description = "Retrieve public accounts for the current user")
    @ActionAnnotation(title = I18Consts.I18N_PUBLIC_ACCOUNT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query public account")
    @Override
    public ResponseEntity<?> queryByUser(PublicAccountRequest request) {
        
        Page<PublicAccountResponse> channelPage = channelService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(channelPage));
    }

    @Operation(summary = "Query Public Accounts by UID", description = "Retrieve a specific public account entry by its unique identifier")
    @ActionAnnotation(title = I18Consts.I18N_PUBLIC_ACCOUNT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query public account")
    @Override
    public ResponseEntity<?> queryByUid(PublicAccountRequest request) {
        
        PublicAccountResponse channelResponse = channelService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(channelResponse));
    }

    @Operation(summary = "Create Public Accounts", description = "Create a new public account entry")
    @ActionAnnotation(title = I18Consts.I18N_PUBLIC_ACCOUNT, action = I18Consts.I18N_ACTION_CREATE, description = "create public account")
    @Override
    public ResponseEntity<?> create(PublicAccountRequest request) {
        
        PublicAccountResponse channelResponse = channelService.create(request);

        return ResponseEntity.ok(JsonResult.success(channelResponse));
    }

    @Operation(summary = "Update Public Accounts", description = "Update an existing public account entry")
    @ActionAnnotation(title = I18Consts.I18N_PUBLIC_ACCOUNT, action = I18Consts.I18N_ACTION_UPDATE, description = "update public account")
    @Override
    public ResponseEntity<?> update(PublicAccountRequest request) {
        
        PublicAccountResponse channelResponse = channelService.update(request);

        return ResponseEntity.ok(JsonResult.success(channelResponse));
    }

    @Operation(summary = "Delete Public Accounts", description = "Delete a public account entry")
    @ActionAnnotation(title = I18Consts.I18N_PUBLIC_ACCOUNT, action = I18Consts.I18N_ACTION_DELETE, description = "delete public account")
    @Override
    public ResponseEntity<?> delete(PublicAccountRequest request) {
        
        channelService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Public Accounts", description = "Export public accounts to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_PUBLIC_ACCOUNT, action = I18Consts.I18N_ACTION_EXPORT, description = "export public account")
    @Override
    public Object export(PublicAccountRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

}
