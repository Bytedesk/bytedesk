/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 11:35:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 09:26:17
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
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
public class UploadRestController extends BaseRestController<UploadRequest> {

	private final UploadRestService uploadRestService;
	
	@Operation(summary = "查询组织下的文件", description = "根据组织ID查询文件列表")
	@ApiResponse(responseCode = "200", description = "查询成功",
		content = @Content(mediaType = "application/json", 
		schema = @Schema(implementation = UploadResponse.class)))
	@Override
	public ResponseEntity<?> queryByOrg(UploadRequest request) {

		Page<UploadResponse> page = uploadRestService.queryByOrg(request);

		return ResponseEntity.ok(JsonResult.success(page));
	}

	@Operation(summary = "查询用户下的文件", description = "根据用户ID查询文件列表")
	@ApiResponse(responseCode = "200", description = "查询成功",
		content = @Content(mediaType = "application/json", 
		schema = @Schema(implementation = UploadResponse.class)))
	@Override
	public ResponseEntity<?> queryByUser(UploadRequest request) {
		
		Page<UploadResponse> page = uploadRestService.queryByUser(request);

		return ResponseEntity.ok(JsonResult.success(page));
	}

	@Operation(summary = "查询指定文件", description = "根据UID查询文件详情")
	@ApiResponse(responseCode = "200", description = "查询成功",
		content = @Content(mediaType = "application/json", 
		schema = @Schema(implementation = UploadResponse.class)))
	@Override
	public ResponseEntity<?> queryByUid(UploadRequest request) {
		
		UploadResponse response = uploadRestService.queryByUid(request);
		if (response == null) {
			return ResponseEntity.ok(JsonResult.error("not found"));
		}

		return ResponseEntity.ok(JsonResult.success(response));
	}

	@Operation(summary = "创建文件记录", description = "创建新的文件记录")
	@ApiResponse(responseCode = "200", description = "创建成功",
		content = @Content(mediaType = "application/json", 
		schema = @Schema(implementation = UploadResponse.class)))
	@Override
	public ResponseEntity<?> create(UploadRequest request) {
		
		UploadResponse response = uploadRestService.create(request);

		return ResponseEntity.ok(JsonResult.success(response));
	}

	@Operation(summary = "更新文件记录", description = "更新文件记录信息")
	@ApiResponse(responseCode = "200", description = "更新成功",
		content = @Content(mediaType = "application/json", 
		schema = @Schema(implementation = UploadResponse.class)))
	@Override
	public ResponseEntity<?> update(UploadRequest request) {
		
		UploadResponse response = uploadRestService.update(request);
		
		return ResponseEntity.ok(JsonResult.success(response));
	}

	@Operation(summary = "删除文件", description = "删除指定的文件和记录")
	@ApiResponse(responseCode = "200", description = "删除成功")
	@ActionAnnotation(title = "上传", action = "删除", description = "delete upload")
	@Override
	public ResponseEntity<?> delete(UploadRequest request) {
		// 更新数据库
		uploadRestService.delete(request);
		// 删除文件
		uploadRestService.deleteFile(request.getFileName());
		
		return ResponseEntity.ok(JsonResult.success("delete success"));
	}

	@Operation(summary = "导出文件记录", description = "导出文件记录数据")
	@ApiResponse(responseCode = "200", description = "导出成功")
	@Override
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

	@Operation(summary = "上传文件", description = "上传文件到服务器")
	@ApiResponse(responseCode = "200", description = "上传成功",
		content = @Content(mediaType = "application/json", 
		schema = @Schema(implementation = UploadResponse.class)))
	@ActionAnnotation(title = "上传", action = "新建", description = "upload File")
	@PostMapping("/file")
	public ResponseEntity<?> uploadFile(
		@Parameter(description = "要上传的文件", required = true) 
		@RequestParam("file") MultipartFile file, 
		UploadRequest request) {
	
		UploadResponse response = uploadRestService.handleFileUpload(file, request);
		
		return ResponseEntity.ok(JsonResult.success("upload file success", response));
	}
}
