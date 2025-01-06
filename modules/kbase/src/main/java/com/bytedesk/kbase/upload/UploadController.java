/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 11:35:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-18 17:36:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.upload;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * file upload - 文件上传, https://spring.io/guides/gs/uploading-files
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/upload")
public class UploadController extends BaseRestController<UploadRequest> {

	private final AuthService authService;

	private final UploadService uploadService;
	
	private final UploadVectorStore uploadVectorStore;

	@ActionAnnotation(title = "upload", action = "uploadFile", description = "upload File")
	@PostMapping("/file")
	public ResponseEntity<?> uploadFile(
			@RequestParam("file") MultipartFile file,
			@RequestParam("file_name") String fileName,
			@RequestParam("file_type") String fileType,
			@RequestParam(name = "is_avatar", required = false, defaultValue = "false") Boolean isAvatar,
			@RequestParam(name = "kb_type", required = false) String kbType,
			@RequestParam(name = "category_uid", required = false) String categoryUid,
			@RequestParam(name = "kb_uid", required = false) String kbUid,
			@RequestParam(name = "client", required = false) String client) {
		log.info("upload fileName: {}, fileType: {}, kbType {}, categoryUid {}, kbUid {}",
				fileName, fileType, kbType, categoryUid, kbUid);

		// TODO: 检测是否同一文件，重复上传

		// http://localhost:9003/file/20240319162820_img-service2.png
		// String uploadPath = uploadService.store(file, fileName);
		// String fileUrl = String.format("%s/file/%s", bytedeskProperties.getUploadUrl(), uploadPath);
		String fileUrl = uploadService.store(file, fileName);
		//
		UserEntity user = authService.getUser();
		UserProtobuf userProtobuf = ConvertUtils.convertToUserProtobuf(user);
		//
		UploadRequest uploadRequest = UploadRequest.builder()
				.fileName(fileName)
				.fileSize(String.valueOf(file.getSize()))
				.fileUrl(fileUrl)
				.fileType(fileType)
				.client(client)
				.user(JSON.toJSONString(userProtobuf))
				.categoryUid(categoryUid)
				.kbUid(kbUid)
				.build();
		uploadRequest.setType(kbType);
		uploadRequest.setOrgUid(user.getOrgUid());
		uploadService.create(uploadRequest);

		return ResponseEntity.ok(JsonResult.success("upload file success", fileUrl));
	}

	@ActionAnnotation(title = "upload", action = "process", description = "process upload")
	@PostMapping("/process")
	public ResponseEntity<?> process(@RequestBody UploadRequest request) {

		Optional<UploadEntity> uploadOptional = uploadService.findByUid(request.getUid());
		if (uploadOptional.isPresent()) {
			UploadEntity upload = uploadOptional.get();
			uploadVectorStore.readSplitWriteToVectorStore(upload);
			//
			return ResponseEntity.ok(JsonResult.success("process success"));
		} else {
			log.error("upload not found");
			return ResponseEntity.badRequest().body(JsonResult.error("upload not found"));
		}
	}

	@PreAuthorize(RolePermissions.ROLE_ADMIN)
	@Override
	public ResponseEntity<?> queryByOrg(UploadRequest request) {

		Page<UploadResponse> page = uploadService.queryByOrg(request);

		return ResponseEntity.ok(JsonResult.success("query success", page));
	}

	@Override
	public ResponseEntity<?> queryByUser(UploadRequest request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'query'");
	}

	@Override
	public ResponseEntity<?> create(UploadRequest request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'create'");
	}

	@Override
	public ResponseEntity<?> update(UploadRequest request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'update'");
	}

	@ActionAnnotation(title = "upload", action = "delete", description = "delete upload")
	@Override
	public ResponseEntity<?> delete(UploadRequest request) {
		// 更新数据库
		uploadService.delete(request);
		// 删除文件
		uploadService.deleteFile(request.getFileName());
		
		return ResponseEntity.ok(JsonResult.success("delete success"));
	}

}
