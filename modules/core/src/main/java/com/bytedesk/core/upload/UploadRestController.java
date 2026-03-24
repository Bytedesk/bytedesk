/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 11:35:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 09:52:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * file upload - 文件上传, https://spring.io/guides/gs/uploading-files
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/upload")
@Tag(name = "Upload Management", description = "File upload management APIs")
@Description("File Upload Controller - File upload and management APIs for handling file operations")
public class UploadRestController extends BaseRestController<UploadRequest, UploadRestService> {

	private final UploadRestService uploadRestService;
	
	@Operation(summary = "Query Files by Organization", description = "Retrieve file list by organization ID")
	@ApiResponse(responseCode = "200", description = "Query successful",
		content = @Content(mediaType = "application/json", 
		schema = @Schema(implementation = UploadResponse.class)))
	@Override
	@GetMapping("/query/org")
	public ResponseEntity<?> queryByOrg(UploadRequest request) {

		Page<UploadResponse> page = uploadRestService.queryByOrg(request);

		return ResponseEntity.ok(JsonResult.success(page));
	}

	@Operation(summary = "Query Files by User", description = "Retrieve file list by user ID")
	@ApiResponse(responseCode = "200", description = "Query successful",
		content = @Content(mediaType = "application/json", 
		schema = @Schema(implementation = UploadResponse.class)))
	@Override
	@GetMapping({"/query", "/query/user"})
	public ResponseEntity<?> queryByUser(UploadRequest request) {
		
		Page<UploadResponse> page = uploadRestService.queryByUser(request);

		return ResponseEntity.ok(JsonResult.success(page));
	}

	@Operation(summary = "Query File by UID", description = "Retrieve file details by UID")
	@ApiResponse(responseCode = "200", description = "Query successful",
		content = @Content(mediaType = "application/json", 
		schema = @Schema(implementation = UploadResponse.class)))
	@Override
	@GetMapping("/query/uid")
	public ResponseEntity<?> queryByUid(UploadRequest request) {
		
		UploadResponse response = uploadRestService.queryByUid(request);

		return ResponseEntity.ok(JsonResult.success(response));
	}

	@Operation(summary = "Create File Record", description = "Create a new file record")
	@ApiResponse(responseCode = "200", description = "Created successfully",
		content = @Content(mediaType = "application/json", 
		schema = @Schema(implementation = UploadResponse.class)))
	@Override
	@PostMapping("/create")
	public ResponseEntity<?> create(@RequestBody UploadRequest request) {
		
		UploadResponse response = uploadRestService.create(request);

		return ResponseEntity.ok(JsonResult.success(response));
	}

	@Operation(summary = "Update File Record", description = "Update file record information")
	@ApiResponse(responseCode = "200", description = "Updated successfully",
		content = @Content(mediaType = "application/json", 
		schema = @Schema(implementation = UploadResponse.class)))
	@Override
	@PostMapping("/update")
	public ResponseEntity<?> update(@RequestBody UploadRequest request) {
		
		UploadResponse response = uploadRestService.update(request);
		
		return ResponseEntity.ok(JsonResult.success(response));
	}

	@Operation(summary = "Delete File", description = "Delete the specified file and record")
	@ApiResponse(responseCode = "200", description = "Deleted successfully")
	@ActionAnnotation(title = I18Consts.I18N_UPLOAD, action = I18Consts.I18N_ACTION_DELETE, description = "delete upload")
	@Override
	@PostMapping("/delete")
	public ResponseEntity<?> delete(@RequestBody UploadRequest request) {
		// 更新数据库
		uploadRestService.delete(request);
		// 删除文件
		uploadRestService.deleteFile(request.getFileName());
		
		return ResponseEntity.ok(JsonResult.success("delete success"));
	}

	@Operation(summary = "Export File Records", description = "Export file record data")
	@ApiResponse(responseCode = "200", description = "Export successful")
	@Override
	@GetMapping("/export")
	public Object export(UploadRequest request, HttpServletResponse response) {
		return exportTemplate(
            request,
            response,
            uploadRestService,
            UploadExcel.class,
            "文件",
            "file"
        );
	}

	@Operation(summary = "Upload File", description = "Upload a file to the server")
	@ApiResponse(responseCode = "200", description = "Uploaded successfully",
		content = @Content(mediaType = "application/json", 
		schema = @Schema(implementation = UploadResponse.class)))
	@ActionAnnotation(title = I18Consts.I18N_UPLOAD, action = I18Consts.I18N_ACTION_CREATE, description = "upload File")
	@PostMapping("/file")
	public ResponseEntity<?> uploadFile(
		@Parameter(description = "The file to upload", required = true) 
		@RequestParam("file") MultipartFile file, 
		UploadRequest request) {
	
		UploadResponse response = uploadRestService.handleFileUpload(file, request);
		
		return ResponseEntity.ok(JsonResult.success("upload file success", response));
	}
}
