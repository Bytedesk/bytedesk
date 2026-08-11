package com.bytedesk.ai.springai.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.springai.service.ChatMemoryTestService;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring AI Chat Memory 测试控制器（HTTP 薄层）
 * <p>
 * 仅负责 HTTP 参数绑定与 {@code bytedesk.debug} 开关校验，真正的记忆编排、ChatClient 构建与 LLM
 * 调用均委托给 {@link ChatMemoryTestService}。
 * </p>
 *
 * <p>演示 chat-memory.adoc 中的各类用法：在 ChatClient 中使用记忆
 * （{@code MessageChatMemoryAdvisor}）、在 ChatModel 中手动维护记忆、{@code MessageWindowChatMemory}
 * 滑动窗口、会话 ID 隔离、以及 {@code ChatMemoryRepository} 的会话管理。</p>
 *
 * <p>注意：</p>
 * <ul>
 *   <li>所有接口受 {@code bytedesk.debug=true} 保护，生产环境返回 unavailable。</li>
 *   <li>使用 Spring AI 原生 {@code deepseekChatModel}，通过
 *       {@code @ConditionalOnBean(name="deepseekChatModel")} 守护。</li>
 *   <li>{@code ChatMemory.CONVERSATION_ID} 是必需参数，省略会抛 {@code IllegalArgumentException}。</li>
 * </ul>
 *
 * @see ChatMemoryTestService
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/chat-memory")
@RequiredArgsConstructor
@ConditionalOnBean(name = "deepseekChatModel")
public class ChatMemoryTestController {

	private final ChatMemoryTestService chatMemoryTestService;

	private final BytedeskProperties bytedeskProperties;

	// ============================================================
	// 1. Memory in ChatClient —— MessageChatMemoryAdvisor（多轮对话）
	// ============================================================

	/**
	 * <p>调用示例（同一 conversationId 内多轮）：</p>
	 * <pre>
	 *   GET .../chat-client?message=我叫张三&amp;conversationId=cm1
	 *   GET .../chat-client?message=我刚才说叫什么名字？&amp;conversationId=cm1
	 * </pre>
	 *
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-memory/chat-client?message=你好&conversationId=demo
	 */
	@GetMapping("/chat-client")
	public ResponseEntity<JsonResult<?>> chatClient(
			@RequestParam(value = "message", defaultValue = "你好，请记住我叫张三") String message,
			@RequestParam(value = "conversationId", defaultValue = "chat-memory-demo") String conversationId) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity
				.ok(JsonResult.success(chatMemoryTestService.chatClientMemory(message, conversationId)));
		}
		catch (Exception e) {
			log.error("chat-client memory failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 2. Memory in ChatModel —— 手动维护记忆（无 Advisor）
	// ============================================================

	/**
	 * <p>演示直接操作 ChatModel 时手动维护记忆（James Bond 式用法）：
	 * 显式把用户消息和模型回复 add 进 ChatMemory，下次调用前 get 出全部历史一起发给模型。</p>
	 *
	 * <p>调用示例：</p>
	 * <pre>
	 *   GET .../chat-model?message=My name is James Bond&amp;conversationId=bond
	 *   GET .../chat-model?message=What is my name?&amp;conversationId=bond
	 * </pre>
	 *
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-memory/chat-model?message=你好&conversationId=demo
	 */
	@GetMapping("/chat-model")
	public ResponseEntity<JsonResult<?>> chatModel(
			@RequestParam(value = "message", defaultValue = "你好，请记住我叫张三") String message,
			@RequestParam(value = "conversationId", defaultValue = "chat-memory-demo") String conversationId) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity
				.ok(JsonResult.success(chatMemoryTestService.chatModelMemory(message, conversationId)));
		}
		catch (Exception e) {
			log.error("chat-model memory failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 3. MessageWindowChatMemory —— 滑动窗口
	// ============================================================

	/**
	 * <p>演示 MessageWindowChatMemory 的滑动窗口：超过 maxMessages 时旧消息被淘汰，
	 * 但 SystemMessage 始终保留。淘汰以「完整一轮（UserMessage 起始）」为边界。</p>
	 *
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-memory/message-window?maxMessages=3&turns=5
	 */
	@GetMapping("/message-window")
	public ResponseEntity<JsonResult<?>> messageWindow(
			@RequestParam(value = "maxMessages", defaultValue = "3") int maxMessages,
			@RequestParam(value = "turns", defaultValue = "5") int turns) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(chatMemoryTestService.messageWindow(maxMessages, turns)));
		}
		catch (Exception e) {
			log.error("message-window failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 4. 会话 ID 隔离 —— 多用户互不干扰
	// ============================================================

	/**
	 * <p>演示会话 ID 是会话的唯一作用域：不同 conversationId 的消息互不可见。
	 * 多用户应用应为每个用户/会话分配唯一 conversationId。</p>
	 *
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-memory/isolation?userA=alice&userB=bob
	 */
	@GetMapping("/isolation")
	public ResponseEntity<JsonResult<?>> isolation(
			@RequestParam(value = "userA", defaultValue = "alice") String userA,
			@RequestParam(value = "userB", defaultValue = "bob") String userB) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(chatMemoryTestService.conversationIsolation(userA, userB)));
		}
		catch (Exception e) {
			log.error("isolation failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 5. ChatMemoryRepository 会话管理
	// ============================================================

	/**
	 * <p>演示 ChatMemoryRepository 的会话管理 API。</p>
	 * <ul>
	 *   <li>{@code action=list}：列出所有会话 ID（findConversationIds）</li>
	 *   <li>{@code action=get}：读取指定会话消息（findByConversationId），可带 message 先写入</li>
	 *   <li>{@code action=delete}：清空指定会话（deleteByConversationId）</li>
	 * </ul>
	 *
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-memory/repository?action=list
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-memory/repository?action=get&conversationId=demo&message=hello
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-memory/repository?action=delete&conversationId=demo
	 */
	@GetMapping("/repository")
	public ResponseEntity<JsonResult<?>> repository(
			@RequestParam(value = "action", defaultValue = "list") String action,
			@RequestParam(value = "conversationId", defaultValue = "repo-demo") String conversationId,
			@RequestParam(value = "message", required = false) String message) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity
				.ok(JsonResult.success(chatMemoryTestService.repository(action, conversationId, message)));
		}
		catch (Exception e) {
			log.error("repository failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

}

