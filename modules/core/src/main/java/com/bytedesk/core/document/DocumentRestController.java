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
package com.bytedesk.core.document;

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
@RequestMapping("/api/v1/document")
@AllArgsConstructor
@Tag(name = "Document Management", description = "Document management APIs for organizing and categorizing content with documents")
@Description("Document Management Controller - Content documentging and categorization APIs")
public class DocumentRestController extends BaseRestController<DocumentRequest, DocumentRestService> {

    private final DocumentRestService documentRestService;

    @ActionAnnotation(title = I18Consts.I18N_DOCUMENT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query document by org")
    @Operation(summary = "Query Documents by Organization", description = "Retrieve documents for the current organization")
    @PreAuthorize(DocumentPermissions.HAS_DOCUMENT_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(DocumentRequest request) {
        
        Page<DocumentResponse> documents = documentRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(documents));
    }

    @ActionAnnotation(title = I18Consts.I18N_DOCUMENT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query document by user")
    @Operation(summary = "Query Documents by User", description = "Retrieve documents for the current user")
    @PreAuthorize(DocumentPermissions.HAS_DOCUMENT_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(DocumentRequest request) {
        
        Page<DocumentResponse> documents = documentRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(documents));
    }

    @ActionAnnotation(title = I18Consts.I18N_DOCUMENT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query document by uid")
    @Operation(summary = "Query Document by UID", description = "Retrieve a specific document by its unique identifier")
    @PreAuthorize(DocumentPermissions.HAS_DOCUMENT_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(DocumentRequest request) {
        
        DocumentResponse document = documentRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(document));
    }

    @ActionAnnotation(title = I18Consts.I18N_DOCUMENT, action = I18Consts.I18N_ACTION_CREATE, description = "create document")
    @Operation(summary = "Create Document", description = "Create a new document")
    @Override
    @PreAuthorize(DocumentPermissions.HAS_DOCUMENT_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DocumentRequest request) {
        
        DocumentResponse document = documentRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(document));
    }

    @ActionAnnotation(title = I18Consts.I18N_DOCUMENT, action = I18Consts.I18N_ACTION_UPDATE, description = "update document")
    @Operation(summary = "Update Document", description = "Update an existing document")
    @Override
    @PreAuthorize(DocumentPermissions.HAS_DOCUMENT_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody DocumentRequest request) {
        
        DocumentResponse document = documentRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(document));
    }

    @ActionAnnotation(title = I18Consts.I18N_DOCUMENT, action = I18Consts.I18N_ACTION_DELETE, description = "delete document")
    @Operation(summary = "Delete Document", description = "Delete a document")
    @Override
    @PreAuthorize(DocumentPermissions.HAS_DOCUMENT_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DocumentRequest request) {
        
        documentRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_DOCUMENT, action = I18Consts.I18N_ACTION_EXPORT, description = "export document")
    @Operation(summary = "Export Documents", description = "Export documents to Excel format")
    @Override
    @PreAuthorize(DocumentPermissions.HAS_DOCUMENT_EXPORT)
    @GetMapping("/export")
    public Object export(DocumentRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            documentRestService,
            DocumentExcel.class,
            "Document",
            "document"
        );
    }

    
    
}