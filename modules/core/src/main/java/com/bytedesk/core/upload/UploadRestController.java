/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 11:35:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-12 09:31:12
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

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * file upload - 文件上传, https://spring.io/guides/gs/uploading-files
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/upload")
public class UploadRestController extends BaseRestController<UploadRequest> {

	private final UploadRestService uploadRestService;
	
	@Override
	public ResponseEntity<?> queryByOrg(UploadRequest request) {

		Page<UploadResponse> page = uploadRestService.queryByOrg(request);

		return ResponseEntity.ok(JsonResult.success(page));
	}

	@Override
	public ResponseEntity<?> queryByUser(UploadRequest request) {
		
		Page<UploadResponse> page = uploadRestService.queryByUser(request);

		return ResponseEntity.ok(JsonResult.success(page));
	}

	@Override
	public ResponseEntity<?> queryByUid(UploadRequest request) {
		
		UploadResponse response = uploadRestService.queryByUid(request);

		return ResponseEntity.ok(JsonResult.success(response));
	}

	@Override
	public ResponseEntity<?> create(UploadRequest request) {
		
		UploadResponse response = uploadRestService.create(request);

		return ResponseEntity.ok(JsonResult.success(response));
	}

	@Override
	public ResponseEntity<?> update(UploadRequest request) {
		
		UploadResponse response = uploadRestService.update(request);
		
		return ResponseEntity.ok(JsonResult.success(response));
	}

	@ActionAnnotation(title = "上传", action = "删除", description = "delete upload")
	@Override
	public ResponseEntity<?> delete(UploadRequest request) {
		// 更新数据库
		uploadRestService.delete(request);
		// 删除文件
		uploadRestService.deleteFile(request.getFileName());
		
		return ResponseEntity.ok(JsonResult.success("delete success"));
	}

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

	@ActionAnnotation(title = "上传", action = "新建", description = "upload File")
	@PostMapping("/file")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, UploadRequest request) {
	
		UploadResponse response = uploadRestService.handleFileUpload(file, request);
		
		return ResponseEntity.ok(JsonResult.success("upload file success", response));
	}


}
