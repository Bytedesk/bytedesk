/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:39:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 08:51:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.dashscope;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.annotation.UserIp;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
// import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

import java.util.Optional;

/**
 * Spring AI Alibaba
 * https://java2ai.com/docs/dev/get-started/
 * https://github.com/alibaba/spring-ai-alibaba
 * Examples:
 * https://github.com/springaialibaba/spring-ai-alibaba-examples
 * 阿里云百炼大模型获取api key：
 * https://bailian.console.aliyun.com/?apiKey=1#/api-key
 */
@RestController
@RequestMapping("/springai/dashscope")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true")
public class SpringAIDashscopeController {

	@Qualifier("bytedeskDashScopeChatClient")
	private final ChatClient bytedeskDashScopeChatClient;

	private final Optional<SpringAIDashscopeImageService> dashScopeImageService;

	// @ConditionalOnProperty(name =
	// {"spring.ai.dashscope.audio.transcription.enabled",
	// "spring.ai.dashscope.audio.synthesis.enabled"}, havingValue = "true")
	// private final Optional<SpringAIDashscopeAudioService> audioService;

	/**
	 * ChatClient 简单调用
	 * http://127.0.0.1:9003/springai/dashscope/simple/chat?query=
	 */
	@GetMapping("/simple/chat")
	public ResponseEntity<?> simpleChat(
			@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String query) {

		String result = bytedeskDashScopeChatClient.prompt(query).call().content();

		return ResponseEntity.ok(JsonResult.success(result));
	}

	/**
	 * ChatClient 流式调用
	 * http://127.0.0.1:9003/springai/dashscope/stream/chat?query=
	 */
	@GetMapping("/stream/chat")
	public Flux<String> streamChat(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String query,
			HttpServletResponse response) {

		response.setCharacterEncoding("UTF-8");

		return bytedeskDashScopeChatClient.prompt(query).stream().content();
	}

	/**
	 * ChatClient 使用自定义的 Advisor 实现功能增强.
	 * eg:
	 * http://127.0.0.1:9003/springai/dashscope/advisor/chat/123?query=你好，我叫牧生，之后的会话中都带上我的名字
	 * 你好，牧生！很高兴认识你。在接下来的对话中，我会记得带上你的名字。有什么想聊的吗？
	 * http://127.0.0.1:9003/springai/dashscope/advisor/chat/123?query=我叫什么名字？
	 * 你叫牧生呀。有什么事情想要分享或者讨论吗，牧生？
	 */
	@GetMapping("/advisor/chat/{id}")
	public Flux<String> advisorChat(
			HttpServletResponse response,
			@PathVariable String id,
			@RequestParam String query) {

		response.setCharacterEncoding("UTF-8");

		return this.bytedeskDashScopeChatClient.prompt(query)
				.advisors(
						a -> a
								.param(CHAT_MEMORY_CONVERSATION_ID_KEY, id)
								.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
				.stream().content();
	}

	// http://127.0.0.1:9003/springai/image/image2text
	@UserIp
	@PostMapping("/image2text")
	@Operation(summary = "DashScope Image Recognition")
	public Flux<JsonResult<?>> image2text(@RequestParam("image") MultipartFile image) {

		if (image.isEmpty()) {
			return Flux.just(JsonResult.error("No image file provided"));
		}

		if (dashScopeImageService.isPresent()) {
			return dashScopeImageService.get().image2Text(image).map(JsonResult::success);
		} else {
			return Flux.just(JsonResult.error("Image service not enabled"));
		}

	}

	// http://127.0.0.1:9003/springai/image/text2Image?prompt=A beautiful sunset
	// over a calm ocean
	@UserIp
	@GetMapping("/text2Image")
	@Operation(summary = "DashScope Image Generation")
	public JsonResult<?> text2Image(
			@RequestParam(value = "prompt", defaultValue = "A beautiful sunset over a calm ocean") String prompt,
			HttpServletResponse response) {

		if (prompt == null || prompt.isEmpty()) {
			return JsonResult.error("Prompt is required");
		}

		if (dashScopeImageService.isPresent()) {
			dashScopeImageService.get().text2Image(prompt, response);
		} else {
			return JsonResult.error("Image service not enabled");
		}

		return JsonResult.success();
	}

	/**
	 * audio2text
	 * http://127.0.0.1:9003/springai/audio/audio2text
	 * 用于将音频转换为文本输出
	 */
	// @UserIp
	// @PostMapping("/audio2text")
	// @Operation(summary = "DashScope Audio Transcription")
	// public Flux<JsonResult<?>> audioToText(@RequestParam("audio") MultipartFile audio) {

	// 	if (audio.isEmpty()) {
	// 		return Flux.just(JsonResult.error("No audio file provided"));
	// 	}

	// 	if (audioService.isPresent()) {
	// 		return audioService.get().audio2text(audio).map(JsonResult::success);
	// 	} else {
	// 		return Flux.just(JsonResult.error("Audio service not enabled"));
	// 	}
	// }

	/**
	 * text2audio
	 * http://127.0.0.1:9003/springai/audio/text2audio?prompt=Hello, how are you?
	 * 用于将文本转换为语音输出
	 */
	// @UserIp
	// @GetMapping("/text2audio")
	// @Operation(summary = "DashScope Speech Synthesis")
	// public JsonResult<?> textToAudio(
	// 		@RequestParam(value = "prompt", defaultValue = "Hello, how are you?") String prompt) {

	// 	if (prompt == null || prompt.isEmpty()) {
	// 		return JsonResult.error("Prompt is required");
	// 	}

	// 	if (audioService.isPresent()) {
	// 		byte[] audioData = audioService.get().text2audio(prompt);

	// 		// 测试验证音频数据是否为空
	// 		try (FileOutputStream fos = new FileOutputStream("audio.wav")) {
	// 			fos.write(audioData);
	// 		} catch (IOException e) {
	// 			return JsonResult.error("Failed to save audio file: " + e.getMessage());
	// 		}

	// 		return JsonResult.success(audioData);
	// 	} else {
	// 		return JsonResult.error("Audio service not enabled");
	// 	}
	// }

}