/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-19 17:02:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-28 15:10:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.upload.UploadService;

import lombok.AllArgsConstructor;

/**
 * 
 */
@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class UploadDowner {

	private final UploadService uploadService;

	/**
	 * 浏览器下载文件，或放到 <img src> 标签中在线展示
	 * http://127.0.0.1:9003/file/20240319162820_img-service2.png
	 * 
	 * @param filename
	 * @return
	 */
	@GetMapping("/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> browser(@PathVariable String filename) {

		Resource file = uploadService.loadAsResource(filename);

		if (file == null)
			return ResponseEntity.notFound().build();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

}
