/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-10 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-11-24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 * 
 */
package com.bytedesk.ai.springai.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.MemoryAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.session.DefaultSessionService;
import org.springframework.ai.session.EventFilter;
import org.springframework.ai.session.InMemorySessionRepository;
import org.springframework.ai.session.SessionAttributes;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.SessionRepository;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.springai.controller.ChatMemoryTestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring AI Session Memory 测试服务（ChatMemory 升级替代）
 * <p>
 * 封装 session.adoc 中各类用法的实现细节：SessionMemoryAdvisor 多轮对话、在 ChatModel 中
 * 手动维护会话日志、EventFilter.lastN turn-aware 滑动窗口、会话 ID 隔离、
 * 以及基于 {@link SessionService} 的会话管理。
 * </p>
 *
 * <p>{@link ChatMemoryTestController} 仅负责 HTTP 参数绑定与 {@code bytedesk.debug} 开关校验，
 * 真正的记忆编排、ChatClient 构建与 LLM 调用均在本服务中完成。</p>
 *
 * <p>注意：</p>
 * <ul>
 *   <li>使用 Spring AI 原生 {@code deepseekChatModel}，保证功能调用 / 记忆 Advisor 可用。</li>
 *   <li>共享的 {@link SessionService} 在类初始化时创建一次，所有请求共享同一实例，
 *       才能让同一 {@code sessionId} 的多轮消息跨请求被记住。
 *       （若在请求方法内新建 SessionService，每次请求都会得到一个全新的空仓库，"多轮记忆失效"。）</li>
 *   <li>默认使用进程内 {@link InMemorySessionRepository}（线程安全，可安全共享）；
 *       生产环境使用 JDBC 持久化（AI_SESSION 三表，见 BytedeskSessionRepositoryConfig）。</li>
 *   <li>旧 API（ChatMemory/MessageWindowChatMemory/MessageChatMemoryAdvisor）自
 *       Spring AI 2.1.0 起废弃，3.0.0 移除；本服务已全部迁移到 Session API。</li>
 * </ul>
 *
 * @see ChatMemoryTestController
 */
@Slf4j
@Service
public class ChatMemoryTestService {

	/**
	 * 使用 Spring AI 原生的 deepseekChatModel。
	 */
	private final ChatModel chatModel;

	/**
	 * 共享的会话记忆服务（单例，进程内存储）。
	 * <p>
	 * 关键点：{@link SessionService} 的会话日志保存在其内部 repository 中。
	 * 必须在类初始化时创建一次、所有请求共享同一个实例，
	 * 才能让同一 {@code sessionId} 的多轮消息跨请求被记住。
	 * </p>
	 * <p>
	 * 读取窗口：{@link EventFilter#lastN(int)} 为 turn-aware 窗口
	 * （等价旧 MessageWindowChatMemory.maxMessages，切口向前对齐到 UserMessage 起始的完整轮次）。
	 * </p>
	 * <p>
	 * 默认使用进程内 {@link InMemorySessionRepository}；生产环境由
	 * BytedeskSessionRepositoryConfig 提供 JDBC 实现（AI_SESSION 三表）。
	 * </p>
	 */
	private final SessionService sessionService = DefaultSessionService.builder()
		.sessionRepository(InMemorySessionRepository.builder().build())
		.build();

	/**
	 * 用于「会话管理」演示的独立仓库（与 {@link #sessionService} 隔离，避免污染 Advisor 演示链路）。
	 */
	private final SessionRepository demoRepository = InMemorySessionRepository.builder().build();

	/**
	 * 绑定 {@link #demoRepository} 的独立 SessionService，演示会话级 API
	 * （create/findById/delete，取代旧 findConversationIds/findByConversationId/deleteByConversationId）。
	 */
	private final SessionService demoSessionService = DefaultSessionService.builder()
		.sessionRepository(demoRepository)
		.build();

	/**
	 * 新 Session API 无「列出全部会话」接口（旧 findConversationIds 已移除），
	 * 演示用追加集合记录本服务创建过的会话 ID；生产环境请查 AI_SESSION 表
	 * 或用 findByMetadata(PRINCIPAL_NAME, ...) 按属主检索。
	 */
	private final java.util.Set<String> demoSessionIds = ConcurrentHashMap.newKeySet();

	public ChatMemoryTestService(
			@Qualifier("deepseekChatModel") ObjectProvider<ChatModel> deepseekChatModelProvider) {
		this.chatModel = deepseekChatModelProvider.getIfAvailable();
	}

	// ============================================================
	// 1. Memory in ChatClient —— SessionMemoryAdvisor（多轮对话）
	// ============================================================

	/**
	 * 演示在 {@link ChatClient} 中使用 {@link SessionMemoryAdvisor} 维护多轮对话记忆。
	 *
	 * <p>对应 session.adoc 「Memory in Chat Client」一节（取代旧 MessageChatMemoryAdvisor）：
	 * Advisor 在每次交互时自动从 SessionService 取出会话 active view 并注入 prompt，
	 * 无需手动拼历史消息。</p>
	 *
	 * <p>注意：{@code MemoryAdvisor.CONVERSATION_ID} 是必需参数
	 * （{@code SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY} 与之同值），省略会抛
	 * {@link IllegalStateException}。</p>
	 *
	 * @param message        用户输入
	 * @param conversationId 会话 ID，同一 ID 的多轮消息会被关联
	 * @return 结果 map（含 advisor / message / response / conversationId / history）
	 */
	public Map<String, Object> chatClientMemory(String message, String conversationId) {
		log.info("[session-memory] chat-client memory: conversationId={}, message={}", conversationId, message);

		ChatClient chatClient = ChatClient.builder(chatModel)
			.defaultAdvisors(SessionMemoryAdvisor.builder(sessionService)
				.eventFilter(EventFilter.lastN(20))
				.build())
			.build();

		String content = chatClient.prompt()
			.user(message)
			.advisors(a -> a.param(MemoryAdvisor.CONVERSATION_ID, conversationId))
			.call()
			.content();

		Map<String, Object> result = buildResult("SessionMemoryAdvisor", message, content);
		result.put("conversationId", conversationId);
		// 返回当前会话 active view 中的全部消息，便于观察“多轮记忆”效果
		result.put("history", formatMessages(sessionService.getActiveMessages(conversationId)));
		result.put("historyCount", sessionService.getActiveMessages(conversationId).size());
		return result;
	}

	// ============================================================
	// 2. Memory in ChatModel —— 手动维护记忆（无 Advisor）
	// ============================================================

	/**
	 * 演示直接操作 {@link ChatModel} 时手动维护会话日志：
	 * 把用户消息与模型回复显式 {@code appendMessage} 进 SessionService，
	 * 下次调用前 {@code getMessages} 出全部历史一起发给模型。
	 *
	 * <p>对应 session.adoc 「Memory in Chat Model」一节的 James Bond 示例。</p>
	 *
	 * @param message        用户输入
	 * @param conversationId 会话 ID
	 * @return 结果 map（含 mode / message / response / conversationId / history）
	 */
	public Map<String, Object> chatModelMemory(String message, String conversationId) {
		log.info("[session-memory] chat-model memory: conversationId={}, message={}", conversationId, message);

		// 1. 记录用户消息
		sessionService.appendMessage(conversationId, new UserMessage(message));
		// 2. 取出会话全部历史一起发给模型
		ChatResponse chatResponse = chatModel.call(new Prompt(sessionService.getMessages(conversationId)));
		// 3. 记录模型回复
		sessionService.appendMessage(conversationId, chatResponse.getResult().getOutput());

		Map<String, Object> result = buildResult("Manual (ChatModel + SessionService)", message,
				chatResponse.getResult().getOutput().getText());
		result.put("mode", "manual");
		result.put("conversationId", conversationId);
		result.put("history", formatMessages(sessionService.getMessages(conversationId)));
		result.put("historyCount", sessionService.getMessages(conversationId).size());
		return result;
	}

	// ============================================================
	// 3. EventFilter.lastN —— turn-aware 滑动窗口
	// ============================================================

	/**
	 * 演示 {@link EventFilter#lastN(int)} 的 turn-aware 滑动窗口
	 * （取代旧 MessageWindowChatMemory.maxMessages）。
	 *
	 * <p>对应 session.adoc 「Event Filters」一节。</p>
	 *
	 * <p>这里用一个小窗口（默认 3）演示淘汰效果：连续写入若干轮消息后观察保留情况。
	 * 注意：窗口淘汰以「完整一轮（UserMessage 起始）」为边界，不会截断半轮对话。</p>
	 *
	 * @param maxMessages   窗口大小（上限）
	 * @param turns         要写入的轮次数（每轮一条 UserMessage）
	 * @return 结果 map（含 maxMessages / turns / remainingMessages / messages）
	 */
	public Map<String, Object> messageWindow(int maxMessages, int turns) {
		log.info("[session-memory] message window: maxMessages={}, turns={}", maxMessages, turns);

		// 独立 SessionService，避免污染共享演示会话
		SessionService window = DefaultSessionService.builder()
			.sessionRepository(InMemorySessionRepository.builder().build())
			.build();
		String sessionId = "window-demo";

		// 写入 turns 条用户消息（不调用模型，纯演示窗口淘汰）
		for (int i = 1; i <= turns; i++) {
			window.appendMessage(sessionId, new UserMessage("第 " + i + " 条消息"));
		}

		// 用 lambda 而非 SessionEvent::hasMessage / SessionEvent::getMessage：
		// 方法引用会把接收者作为未标注的 legacy 参数传入 @Nonnull 的函数式接口，触发 null type safety 警告
		List<Message> remaining = window.getEvents(sessionId, EventFilter.lastN(maxMessages))
				.stream()
				.filter(event -> event.hasMessage())
				.map(event -> event.getMessage())
				.toList();
		Map<String, Object> result = new HashMap<>();
		result.put("memoryType", "EventFilter.lastN");
		result.put("maxMessages", maxMessages);
		result.put("writtenCount", turns);
		result.put("remainingCount", remaining.size());
		result.put("remainingMessages", formatMessages(remaining));
		result.put("explain", "超过 lastN(n) 的旧事件被窗口淘汰（读取侧截断，完整日志仍保留在仓库，"
				+ "可供 searchEvents 关键词检索召回）；淘汰以完整一轮（UserMessage 起始）为边界，"
				+ "因此实际保留数可能略小于 n。");
		result.put("timestamp", System.currentTimeMillis());
		return result;
	}

	// ============================================================
	// 4. 会话 ID 隔离 —— 多用户互不干扰
	// ============================================================

	/**
	 * 演示「会话 ID 是会话的唯一作用域」：不同 sessionId 的消息互不可见。
	 *
	 * <p>对应 session.adoc 「Working with Session IDs」一节：
	 * 多用户应用中应为每个用户（及每个会话）分配唯一 sessionId。</p>
	 *
	 * <p>本演示不调用模型，只往两个会话各写一条消息，再分别读取，展示隔离效果。</p>
	 *
	 * @param userA 用户 A 的标识
	 * @param userB 用户 B 的标识
	 * @return 结果 map（含 userA / userB 的会话内容）
	 */
	public Map<String, Object> conversationIsolation(String userA, String userB) {
		log.info("[session-memory] conversation isolation: userA={}, userB={}", userA, userB);

		// 模拟从用户/会话派生 sessionId（生产环境应在服务端按当前用户/会话生成）
		String sessionA = userA + ":session-1";
		String sessionB = userB + ":session-1";

		SessionService memory = DefaultSessionService.builder()
			.sessionRepository(InMemorySessionRepository.builder().build())
			.build();
		memory.appendMessage(sessionA, new UserMessage("我是用户 A，我说的是秘密 A"));
		memory.appendMessage(sessionB, new UserMessage("我是用户 B，我说的是秘密 B"));

		Map<String, Object> result = new HashMap<>();
		result.put("scenario", "Session ID Isolation");
		result.put("conversationIdA", sessionA);
		result.put("conversationIdB", sessionB);
		result.put("messagesA", formatMessages(memory.getMessages(sessionA)));
		result.put("messagesB", formatMessages(memory.getMessages(sessionB)));
		result.put("explain", "会话 ID 是会话的唯一作用域：不同 sessionId 的消息互不可见。"
				+ "多用户应用中应为每个用户/会话分配唯一 sessionId，避免串话。");
		result.put("timestamp", System.currentTimeMillis());
		return result;
	}

	// ============================================================
	// 5. SessionService 会话管理
	// ============================================================

	/**
	 * 演示 {@link SessionService} 的会话管理 API：列出会话、读取会话、删除会话。
	 *
	 * <p>对应 session.adoc 「Session Storage」一节（取代旧 ChatMemoryRepository）。
	 * 新 API 无 findConversationIds；演示用 {@link SessionAttributes#PRINCIPAL_NAME}
	 * 索引检索替代——按属主（principalName）列出其全部会话。</p>
	 *
	 * @param action       操作：list / get / delete
	 * @param conversationId 目标会话 ID（get/delete 必填）
	 * @param message      写入演示消息（用于先制造数据，可选）
	 * @return 结果 map（随 action 不同返回不同字段）
	 */
	public Map<String, Object> repository(String action, String conversationId, String message) {
		log.info("[session-memory] repository action: {}, conversationId={}", action, conversationId);
		Map<String, Object> result = new HashMap<>();
		result.put("repositoryType", demoRepository.getClass().getSimpleName());
		result.put("action", action);

		switch (action == null ? "" : action.toLowerCase()) {
			case "list" -> {
				// findByMetadata 是新 API 唯一的会话检索入口（按索引属性，如 principalName）；
				// 此处列出 demo 用户名下的全部会话。JDBC 仓库默认索引 principalName。
				List<String> ids = demoRepository
					.findByMetadata(SessionAttributes.PRINCIPAL_NAME, "demo-user")
					.stream()
					.map(s -> s.id())
					.toList();
				result.put("conversationIds", ids);
				result.put("conversationCount", ids.size());
				result.put("knownSessionIds", List.copyOf(demoSessionIds));
				result.put("explain", "新 API 以 findByMetadata(principalName, ...) 按属主检索会话"
						+ "（取代旧 findConversationIds 全量列表）；未建索引的全量列表需直查 AI_SESSION 表。");
			}
			case "get" -> {
				if (message != null && !message.isEmpty()) {
					ensureDemoSession(conversationId);
					demoSessionService.appendMessage(conversationId, new UserMessage(message));
				}
				List<Message> msgs = demoSessionService.getMessages(conversationId);
				result.put("conversationId", conversationId);
				result.put("messages", formatMessages(msgs));
				result.put("messageCount", msgs.size());
			}
			case "delete" -> {
				demoSessionService.delete(conversationId);
				demoSessionIds.remove(conversationId);
				result.put("conversationId", conversationId);
				result.put("deleted", true);
				result.put("explain", "delete(sessionId) 删除会话及其全部事件（JDBC 下级联删 AI_SESSION_EVENT）。");
			}
			default -> {
				result.put("error", "Unknown action: " + action + ". Supported: list / get / delete");
			}
		}
		result.put("timestamp", System.currentTimeMillis());
		return result;
	}

	/**
	 * 确保演示会话存在（带 principalName 索引，供 list 演示按属主检索）。
	 */
	private void ensureDemoSession(String conversationId) {
		if (demoRepository.findById(conversationId) == null) {
			demoSessionService.create(org.springframework.ai.session.CreateSessionRequest.builder()
				.id(conversationId)
				.metadata(SessionAttributes.PRINCIPAL_NAME, "demo-user")
				.build());
			demoSessionIds.add(conversationId);
		}
	}

	// ============================================================
	// 辅助方法
	// ============================================================

	/**
	 * 统一构造结果 map。
	 * @param memory   记忆类型/描述
	 * @param message  原始用户输入
	 * @param response LLM 响应文本
	 * @return 结果 map
	 */
	private Map<String, Object> buildResult(String memory, String message, String response) {
		Map<String, Object> result = new HashMap<>();
		result.put("memory", memory);
		result.put("message", message);
		result.put("response", response);
		result.put("responseLength", response == null ? 0 : response.length());
		result.put("timestamp", System.currentTimeMillis());
		return result;
	}

	/**
	 * 把消息列表格式化为可读的结构（role + text 摘要），避免直接序列化大对象。
	 */
	private List<Map<String, Object>> formatMessages(List<Message> messages) {
		if (messages == null) {
			return List.of();
		}
		return messages.stream()
			.map(m -> {
				Map<String, Object> item = new HashMap<>();
				item.put("role", m.getMessageType() == null ? null : m.getMessageType().name());
				String text = m.getText();
				item.put("text", text == null ? "" : (text.length() > 200 ? text.substring(0, 200) + "..." : text));
				return item;
			})
			.toList();
	}

}
