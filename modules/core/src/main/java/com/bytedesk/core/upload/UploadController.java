/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 11:35:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-21 15:17:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

/**
 * @Description: file upload - 文件上传
 *               https://spring.io/guides/gs/uploading-files
 * 
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/upload")
public class UploadController {

	private final UploadService uploadService;

	@PostMapping("/")
	public JsonResult<?> upload(
			@RequestParam("file") MultipartFile file,
			@RequestParam("file_name") String fileName,
			@RequestParam("file_type") String type) {

		// TODO: image/avatar/file/video/voice

		// http://localhost:9003/file/20240319162820_img-service2.png
		String uploadPath = uploadService.store(file, fileName);

		// TODO: format
		return JsonResult.success(String.format("http://localhost:9003/file/%s", uploadPath));
	}
	
	
	@GetMapping("/query")
	public JsonResult<?> query() {
		return JsonResult.success();
	}


}
