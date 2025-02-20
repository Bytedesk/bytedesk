/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 10:42:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 10:53:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/prompt")
@RequiredArgsConstructor
public class SpringAiPromptController {

	private final ChatClient defaultChatClient;

	@Value("classpath:/prompts/joke-prompt.st")
	private Resource jokeResource;

	@Value("classpath:/prompts/system-message.st")
	private Resource systemResource;

	// http://127.0.0.1:9003/prompt/joke?adjective=funny&topic=cows
	@GetMapping("/joke")
	public ResponseEntity<JsonResult<?>> joke(
			@RequestParam(value = "adjective", defaultValue = "funny") String adjective,
			@RequestParam(value = "topic", defaultValue = "cows") String topic) {
		// 使用prompt模板
		PromptTemplate promptTemplate = new PromptTemplate(jokeResource);
		Prompt prompt = promptTemplate.create(Map.of("adjective", adjective, "topic", topic));

		ChatResponse response = defaultChatClient.prompt(prompt)
				.call()
				.chatResponse();

		return ResponseEntity.ok(JsonResult.success(response));
	}

	// http://127.0.0.1:9003/prompt/roles?message=&name=&voice=pirate
	@GetMapping("/roles")
	public ResponseEntity<JsonResult<?>> roles(@RequestParam(value = "message",
			defaultValue = "Tell me about three famous pirates from the Golden Age of Piracy and why they did.  Write at least a sentence for each pirate.") String message,
			@RequestParam(value = "name", defaultValue = "Bob") String name,
			@RequestParam(value = "voice", defaultValue = "pirate") String voice) {

		UserMessage userMessage = new UserMessage(message);
		SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));
		Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

		ChatResponse response = defaultChatClient.prompt(prompt)
				.call()
				.chatResponse();

		return ResponseEntity.ok(JsonResult.success(response));
	}

}
