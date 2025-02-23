/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 13:52:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 14:17:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.annotation.UserIp;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/springai/image")
@RequiredArgsConstructor
public class SpringAIImageController {

    private final SpringAIImageService imageService;


	// http://127.0.0.1:9003/springai/image/image2text
	@UserIp
	@PostMapping("/image2text")
	@Operation(summary = "DashScope Image Recognition")
	public Flux<JsonResult<?>> image2text(@RequestParam("image") MultipartFile image) {

		if (image.isEmpty()) {
			return Flux.just(JsonResult.error("No image file provided"));
		}

		return imageService.image2Text(image).map(JsonResult::success);
	}


	// http://127.0.0.1:9003/springai/image/text2Image?prompt=A beautiful sunset over a calm ocean
	@UserIp
	@GetMapping("/text2Image")
	@Operation(summary = "DashScope Image Generation")
	public JsonResult<?> text2Image(
			@RequestParam(value = "prompt", defaultValue = "A beautiful sunset over a calm ocean") String prompt,
			HttpServletResponse response
	) {

		if (prompt == null || prompt.isEmpty()) {
			return JsonResult.error("Prompt is required");
		}

		imageService.text2Image(prompt, response);

		return JsonResult.success();
	}
    
}
