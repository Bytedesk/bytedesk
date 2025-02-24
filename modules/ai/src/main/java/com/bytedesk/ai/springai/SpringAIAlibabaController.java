/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:39:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 11:17:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
// import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

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
@RequestMapping("/springai/alibaba")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true")
public class SpringAIAlibabaController {

	// @Qualifier("dashScopeChatClient") // 不起作用？，只能重命名变量名
	private final ChatClient dashScopeChatClient;

	/**
	 * ChatClient 简单调用
	 * http://127.0.0.1:9003/springai/alibaba/simple/chat?query=
	 */
	@GetMapping("/simple/chat")
	public ResponseEntity<?> simpleChat(
			@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String query) {

		String result = dashScopeChatClient.prompt(query).call().content();

		return ResponseEntity.ok(JsonResult.success(result));
	}

	/**
	 * ChatClient 流式调用
	 * http://127.0.0.1:9003/springai/alibaba/stream/chat?query=
	 */
	@GetMapping("/stream/chat")
	public Flux<String> streamChat(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String query,
			HttpServletResponse response) {

		response.setCharacterEncoding("UTF-8");

		return dashScopeChatClient.prompt(query).stream().content();
	}

	/**
	 * ChatClient 使用自定义的 Advisor 实现功能增强.
	 * eg:
	 * http://127.0.0.1:9003/springai/alibaba/advisor/chat/123?query=你好，我叫牧生，之后的会话中都带上我的名字
	 * 你好，牧生！很高兴认识你。在接下来的对话中，我会记得带上你的名字。有什么想聊的吗？
	 * http://127.0.0.1:9003/springai/alibaba/advisor/chat/123?query=我叫什么名字？
	 * 你叫牧生呀。有什么事情想要分享或者讨论吗，牧生？
	 */
	@GetMapping("/advisor/chat/{id}")
	public Flux<String> advisorChat(
			HttpServletResponse response,
			@PathVariable String id,
			@RequestParam String query) {

		response.setCharacterEncoding("UTF-8");

		return this.dashScopeChatClient.prompt(query)
				.advisors(
						a -> a
								.param(CHAT_MEMORY_CONVERSATION_ID_KEY, id)
								.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
				.stream().content();
	}

}