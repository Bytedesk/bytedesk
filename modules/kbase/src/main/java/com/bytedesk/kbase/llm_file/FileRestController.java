/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-28 14:39:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

@Tag(name = "文件管理", description = "文件管理相关接口")
@RestController
@RequestMapping("/api/v1/llm/file")
@AllArgsConstructor
public class FileRestController extends BaseRestController<FileRequest, FileRestService> {

    private final FileRestService fileRestService;

    @Operation(summary = "查询组织下的文件", description = "根据组织ID查询文件列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FileResponse.class)))
    @PreAuthorize(FilePermissions.HAS_FILE_READ)
    @Override
    public ResponseEntity<?> queryByOrg(FileRequest request) {
        
        Page<FileResponse> files = fileRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(files));
    }

    @Operation(summary = "查询用户下的文件", description = "根据用户ID查询文件列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FileResponse.class)))
    @PreAuthorize(FilePermissions.HAS_FILE_READ)
    @Override
    public ResponseEntity<?> queryByUser(FileRequest request) {
        
        Page<FileResponse> files = fileRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(files));
    }

    @Operation(summary = "创建文件", description = "创建新的文件")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FileResponse.class)))
    @ActionAnnotation(title = "文件", action = "新建", description = "create file")
    @PreAuthorize(FilePermissions.HAS_FILE_CREATE)
    @Override
    public ResponseEntity<?> create(FileRequest request) {
        
        FileResponse file = fileRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(file));
    }

    @Operation(summary = "更新文件", description = "更新文件信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FileResponse.class)))
    @ActionAnnotation(title = "文件", action = "更新", description = "update file")
    @PreAuthorize(FilePermissions.HAS_FILE_UPDATE)
    @Override
    public ResponseEntity<?> update(FileRequest request) {
        
        FileResponse file = fileRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(file));
    }

    @Operation(summary = "删除文件", description = "删除指定的文件")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ActionAnnotation(title = "文件", action = "删除", description = "delete file")
    @PreAuthorize(FilePermissions.HAS_FILE_DELETE)
    @Override
    public ResponseEntity<?> delete(FileRequest request) {
        
        fileRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "删除所有文件", description = "删除所有文件")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PostMapping("/deleteAll")
    @PreAuthorize(FilePermissions.HAS_FILE_DELETE)
    public ResponseEntity<?> deleteAll(@RequestBody FileRequest request) {

        fileRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "启用文件", description = "启用或禁用文件")
    @ApiResponse(responseCode = "200", description = "操作成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FileResponse.class)))
    @PostMapping("/enable")
    @PreAuthorize(FilePermissions.HAS_FILE_UPDATE)
    public ResponseEntity<?> enable(@RequestBody FileRequest request) {

        FileResponse file = fileRestService.enable(request);
        
        return ResponseEntity.ok(JsonResult.success(file));
    }

    @Operation(summary = "导出文件", description = "导出文件数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @ActionAnnotation(title = "文件", action = "导出", description = "export file")
    @PreAuthorize(FilePermissions.HAS_FILE_EXPORT)
    @Override
    public Object export(FileRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            fileRestService,
            FileExcel.class,
            "文件",
            "file"
        );
    }

    @Operation(summary = "重新chunk文件", description = "重新对文件进行chunk切分")
    @ApiResponse(responseCode = "200", description = "重新chunk成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FileResponse.class)))
    @PostMapping("/rechunk")
    @PreAuthorize(FilePermissions.HAS_FILE_UPDATE)
    public ResponseEntity<?> rechunkFile(@RequestBody FileRequest request) {
        
        FileResponse file = fileRestService.rechunkFile(request.getUid());
        
        return ResponseEntity.ok(JsonResult.success(file));
    }
    
}