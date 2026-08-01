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
package com.bytedesk.kbase.llm_embedding;

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
@RequestMapping("/api/v1/llm_embedding")
@AllArgsConstructor
@Tag(name = "LlmEmbedding Management", description = "LlmEmbedding management APIs for organizing and categorizing content with llm_embeddings")
@Description("LlmEmbedding Management Controller - Content llm_embeddingging and categorization APIs")
public class LlmEmbeddingRestController extends BaseRestController<LlmEmbeddingRequest, LlmEmbeddingRestService> {

    private final LlmEmbeddingRestService llm_embeddingRestService;

    @ActionAnnotation(title = I18Consts.I18N_LLM_EMBEDDING, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query llm_embedding by org")
    @Operation(summary = "Query LlmEmbeddings by Organization", description = "Retrieve llm_embeddings for the current organization")
    @PreAuthorize(LlmEmbeddingPermissions.HAS_LLM_EMBEDDING_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(LlmEmbeddingRequest request) {
        
        Page<LlmEmbeddingResponse> llm_embeddings = llm_embeddingRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(llm_embeddings));
    }

    @ActionAnnotation(title = I18Consts.I18N_LLM_EMBEDDING, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query llm_embedding by user")
    @Operation(summary = "Query LlmEmbeddings by User", description = "Retrieve llm_embeddings for the current user")
    @PreAuthorize(LlmEmbeddingPermissions.HAS_LLM_EMBEDDING_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(LlmEmbeddingRequest request) {
        
        Page<LlmEmbeddingResponse> llm_embeddings = llm_embeddingRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(llm_embeddings));
    }

    @ActionAnnotation(title = I18Consts.I18N_LLM_EMBEDDING, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query llm_embedding by uid")
    @Operation(summary = "Query LlmEmbedding by UID", description = "Retrieve a specific llm_embedding by its unique identifier")
    @PreAuthorize(LlmEmbeddingPermissions.HAS_LLM_EMBEDDING_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(LlmEmbeddingRequest request) {
        
        LlmEmbeddingResponse llm_embedding = llm_embeddingRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(llm_embedding));
    }

    @ActionAnnotation(title = I18Consts.I18N_LLM_EMBEDDING, action = I18Consts.I18N_ACTION_CREATE, description = "create llm_embedding")
    @Operation(summary = "Create LlmEmbedding", description = "Create a new llm_embedding")
    @Override
    @PreAuthorize(LlmEmbeddingPermissions.HAS_LLM_EMBEDDING_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody LlmEmbeddingRequest request) {
        
        LlmEmbeddingResponse llm_embedding = llm_embeddingRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(llm_embedding));
    }

    @ActionAnnotation(title = I18Consts.I18N_LLM_EMBEDDING, action = I18Consts.I18N_ACTION_UPDATE, description = "update llm_embedding")
    @Operation(summary = "Update LlmEmbedding", description = "Update an existing llm_embedding")
    @Override
    @PreAuthorize(LlmEmbeddingPermissions.HAS_LLM_EMBEDDING_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody LlmEmbeddingRequest request) {
        
        LlmEmbeddingResponse llm_embedding = llm_embeddingRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(llm_embedding));
    }

    @ActionAnnotation(title = I18Consts.I18N_LLM_EMBEDDING, action = I18Consts.I18N_ACTION_DELETE, description = "delete llm_embedding")
    @Operation(summary = "Delete LlmEmbedding", description = "Delete a llm_embedding")
    @Override
    @PreAuthorize(LlmEmbeddingPermissions.HAS_LLM_EMBEDDING_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody LlmEmbeddingRequest request) {
        
        llm_embeddingRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_LLM_EMBEDDING, action = I18Consts.I18N_ACTION_EXPORT, description = "export llm_embedding")
    @Operation(summary = "Export LlmEmbeddings", description = "Export llm_embeddings to Excel format")
    @Override
    @PreAuthorize(LlmEmbeddingPermissions.HAS_LLM_EMBEDDING_EXPORT)
    @GetMapping("/export")
    public Object export(LlmEmbeddingRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            llm_embeddingRestService,
            LlmEmbeddingExcel.class,
            "LlmEmbedding",
            "llm_embedding"
        );
    }

    
    
}