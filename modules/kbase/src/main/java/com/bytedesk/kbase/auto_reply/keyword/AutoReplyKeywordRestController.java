/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:05:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-10 16:45:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.keyword;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "关键词回复管理", description = "关键词回复管理相关接口")
@RestController
@RequestMapping("/api/v1/autoreply/keyword")
@AllArgsConstructor
public class AutoReplyKeywordRestController extends BaseRestController<AutoReplyKeywordRequest, AutoReplyKeywordRestService> {

    private final AutoReplyKeywordRestService keywordRestService;

    @Operation(summary = "查询组织下的关键词回复", description = "根据组织ID查询关键词回复列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyKeywordResponse.class)))
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_READ)
    @ActionAnnotation(title = "关键词回复", action = "组织查询", description = "query autoReplyKeyword by org")
    @Override
    public ResponseEntity<?> queryByOrg(AutoReplyKeywordRequest request) {
        
        Page<AutoReplyKeywordResponse> page = keywordRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询用户下的关键词回复", description = "根据用户ID查询关键词回复列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyKeywordResponse.class)))
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_READ)
    @ActionAnnotation(title = "关键词回复", action = "用户查询", description = "query autoReplyKeyword by user")
    @Override
    public ResponseEntity<?> queryByUser(AutoReplyKeywordRequest request) {
        
        Page<AutoReplyKeywordResponse> page = keywordRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "创建关键词回复", description = "创建新的关键词回复")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyKeywordResponse.class)))
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_CREATE)
    @ActionAnnotation(title = "关键词回复", action = "新建", description = "create autoReplyKeyword")
    @Override
    public ResponseEntity<?> create(@RequestBody AutoReplyKeywordRequest request) {
        
        AutoReplyKeywordResponse response = keywordRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "更新关键词回复", description = "更新关键词回复信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyKeywordResponse.class)))
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_UPDATE)
    @ActionAnnotation(title = "关键词回复", action = "更新", description = "update autoReplyKeyword")
    @Override
    public ResponseEntity<?> update(@RequestBody AutoReplyKeywordRequest request) {
        
        AutoReplyKeywordResponse response = keywordRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "删除关键词回复", description = "删除指定的关键词回复")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_DELETE)
    @ActionAnnotation(title = "关键词回复", action = "删除", description = "delete autoReplyKeyword")
    @Override
    public ResponseEntity<?> delete(@RequestBody AutoReplyKeywordRequest request) {

        keywordRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }

    @Operation(summary = "启用关键词回复", description = "启用或禁用关键词回复")
    @ApiResponse(responseCode = "200", description = "操作成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyKeywordResponse.class)))
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_UPDATE)
    @ActionAnnotation(title = "关键词回复", action = "启用", description = "enable autoReplyKeyword")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody AutoReplyKeywordRequest request) {
        
        AutoReplyKeywordResponse response = keywordRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "导出关键词回复", description = "导出关键词回复数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_EXPORT)
    @ActionAnnotation(title = "关键词回复", action = "导出", description = "export autoReplyKeyword")
    @GetMapping("/export")
    public Object export(AutoReplyKeywordRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            keywordRestService,
            AutoReplyKeywordExcel.class,
            "关键词",
            "auto_reply-keyword"
        );
    }

    @Override
    public ResponseEntity<?> queryByUid(AutoReplyKeywordRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }


    
}
