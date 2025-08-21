/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 20:39:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.annotation.ActionAnnotation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Description;

@Tag(name = "知识库管理", description = "知识库管理相关接口")
@RestController
@RequestMapping("/api/v1/kbase")
@AllArgsConstructor
@Description("Knowledge Base Controller - Knowledge base content management and organization APIs")
public class KbaseRestController extends BaseRestController<KbaseRequest, KbaseRestService> {

    private final KbaseRestService knowledgeService;

    @Operation(summary = "查询组织下的知识库", description = "根据组织ID查询知识库列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseResponse.class)))
    @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(KbaseRequest request) {

        Page<KbaseResponse> page = knowledgeService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询用户下的知识库", description = "根据用户ID查询知识库列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseResponse.class)))
    @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUser(KbaseRequest request) {
        
        Page<KbaseResponse> page = knowledgeService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询指定知识库", description = "根据UID查询知识库详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseResponse.class)))
    @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUid(KbaseRequest request) {

        KbaseResponse kbase = knowledgeService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(kbase));
    }

    @Operation(summary = "创建知识库", description = "创建新的知识库")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseResponse.class)))
    @ActionAnnotation(title = "知识库", action = "新建", description = "create kbase")
    @PreAuthorize("hasAuthority('KBASE_CREATE')")
    @Override
    public ResponseEntity<?> create(@RequestBody KbaseRequest request) {

        KbaseResponse kbase = knowledgeService.create(request);

        return ResponseEntity.ok(JsonResult.success(kbase));
    }

    @Operation(summary = "更新知识库", description = "更新知识库信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseResponse.class)))
    @ActionAnnotation(title = "知识库", action = "更新", description = "update kbase")
    @PreAuthorize("hasAuthority('KBASE_UPDATE')")
    @Override
    public ResponseEntity<?> update(@RequestBody KbaseRequest request) {

        KbaseResponse kbase = knowledgeService.update(request);

        return ResponseEntity.ok(JsonResult.success(kbase));
    }

    @Operation(summary = "删除知识库", description = "删除指定的知识库")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ActionAnnotation(title = "知识库", action = "删除", description = "delete kbase")
    @PreAuthorize("hasAuthority('KBASE_DELETE')")
    @Override
    public ResponseEntity<?> delete(@RequestBody KbaseRequest request) {

        knowledgeService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @Operation(summary = "导出知识库", description = "导出知识库数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @ActionAnnotation(title = "知识库", action = "导出", description = "export kbase")
    @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    @Override
    public Object export(KbaseRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

}
