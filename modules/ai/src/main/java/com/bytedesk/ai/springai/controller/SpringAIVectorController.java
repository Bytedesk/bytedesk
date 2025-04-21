/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-24 09:54:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 13:07:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRequest;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/vector")
@RequiredArgsConstructor
public class SpringAIVectorController {

    private final UploadRestService uploadService;
    
    @ActionAnnotation(title = "vector", action = "process", description = "process vector")
	@PostMapping("/process")
	public ResponseEntity<?> process(@RequestBody UploadRequest request) {

		Optional<UploadEntity> uploadOptional = uploadService.findByUid(request.getUid());
		if (uploadOptional.isPresent()) {
			// UploadEntity upload = uploadOptional.get();
			// springAIVectorService.readSplitWriteToVectorStore(upload);
			//
			return ResponseEntity.ok(JsonResult.success("process success"));
		} else {
			log.error("upload not found");
			return ResponseEntity.badRequest().body(JsonResult.error("upload not found"));
		}
	}

}
