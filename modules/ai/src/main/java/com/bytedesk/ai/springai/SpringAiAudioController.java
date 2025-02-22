/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 13:51:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 14:06:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.annotation.UserIp;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/springai/audio")
@RequiredArgsConstructor
public class SpringAiAudioController {

    private final SpringAiAudioService audioService;

	/**
	 * audio2text
	 * 用于将音频转换为文本输出
	 */
	@UserIp
	@PostMapping("/audio2text")
	@Operation(summary = "DashScope Audio Transcription")
	public Flux<JsonResult<?>> audioToText(@RequestParam("audio") MultipartFile audio) {

		if (audio.isEmpty()) {
			return Flux.just(JsonResult.error("No audio file provided"));
		}

		return audioService.audio2text(audio).map(JsonResult::success);
	}

	/**
	 * text2audio
	 * 用于将文本转换为语音输出
	 */
	@UserIp
	@GetMapping("/text2audio/{prompt}")
	@Operation(summary = "DashScope Speech Synthesis")
	public JsonResult<?> textToAudio(@PathVariable("prompt") String prompt) {

		if (prompt == null || prompt.isEmpty()) {
			return JsonResult.error("Prompt is required");
		}

		byte[] audioData = audioService.text2audio(prompt);

		// 测试验证音频数据是否为空
		try (FileOutputStream fos = new FileOutputStream("audio.wav")) {
			fos.write(audioData);
		} catch (IOException e) {
			return JsonResult.error("Failed to save audio file: " + e.getMessage());
		}

		return JsonResult.success(audioData);
	}
    
}
