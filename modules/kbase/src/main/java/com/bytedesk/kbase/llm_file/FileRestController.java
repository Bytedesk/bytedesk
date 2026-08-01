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
import org.springframework.web.bind.annotation.GetMapping;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.annotation.ActionAnnotation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "File Management", description = "File management APIs")
@RestController
@RequestMapping("/api/v1/llm/file")
@AllArgsConstructor
public class FileRestController extends BaseRestController<FileRequest, FileRestService> {

    private final FileRestService fileRestService;

    @Operation(summary = "Query Files by Organization", description = "Query the list of files by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FileResponse.class)))
    @PreAuthorize(FilePermissions.HAS_FILE_READ)
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(FileRequest request) {
        
        Page<FileResponse> files = fileRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(files));
    }

    @Operation(summary = "Query Files by User", description = "Query the list of files by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FileResponse.class)))
    @PreAuthorize(FilePermissions.HAS_FILE_READ)
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(FileRequest request) {
        
        Page<FileResponse> files = fileRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(files));
    }

    @Operation(summary = "Create File", description = "Create a new file")
    @ApiResponse(responseCode = "200", description = "Creation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FileResponse.class)))
    @ActionAnnotation(title = I18Consts.I18N_FILE, action = I18Consts.I18N_ACTION_CREATE, description = "create file")
    @PreAuthorize(FilePermissions.HAS_FILE_CREATE)
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody FileRequest request) {
        
        FileResponse file = fileRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(file));
    }

    @Operation(summary = "Update File", description = "Update file information")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FileResponse.class)))
    @ActionAnnotation(title = I18Consts.I18N_FILE, action = I18Consts.I18N_ACTION_UPDATE, description = "update file")
    @PreAuthorize(FilePermissions.HAS_FILE_UPDATE)
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody FileRequest request) {
        
        FileResponse file = fileRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(file));
    }

    @Operation(summary = "Delete File", description = "Delete the specified file")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @ActionAnnotation(title = I18Consts.I18N_FILE, action = I18Consts.I18N_ACTION_DELETE, description = "delete file")
    @PreAuthorize(FilePermissions.HAS_FILE_DELETE)
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody FileRequest request) {
        
        fileRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Delete All Files", description = "Delete all files")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @PostMapping("/deleteAll")
    @PreAuthorize(FilePermissions.HAS_FILE_DELETE)
    public ResponseEntity<?> deleteAll(@RequestBody FileRequest request) {

        fileRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Enable File", description = "Enable or disable the file")
    @ApiResponse(responseCode = "200", description = "Operation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FileResponse.class)))
    @PostMapping("/enable")
    @PreAuthorize(FilePermissions.HAS_FILE_UPDATE)
    public ResponseEntity<?> enable(@RequestBody FileRequest request) {

        FileResponse file = fileRestService.enable(request);
        
        return ResponseEntity.ok(JsonResult.success(file));
    }

    @Operation(summary = "Export Files", description = "Export file data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @ActionAnnotation(title = I18Consts.I18N_FILE, action = I18Consts.I18N_ACTION_EXPORT, description = "export file")
    @PreAuthorize(FilePermissions.HAS_FILE_EXPORT)
    @GetMapping("/export")
    @Override
    public Object export(FileRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            fileRestService,
            FileExcel.class,
            "File",
            "file"
        );
    }

    @Operation(summary = "Re-chunk File", description = "Re-split the file into chunks")
    @ApiResponse(responseCode = "200", description = "Re-chunk successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FileResponse.class)))
    @PostMapping("/rechunk")
    @PreAuthorize(FilePermissions.HAS_FILE_UPDATE)
    public ResponseEntity<?> rechunkFile(@RequestBody FileRequest request) {
        
        FileResponse file = fileRestService.rechunkFile(request.getUid());
        
        return ResponseEntity.ok(JsonResult.success(file));
    }
    
}