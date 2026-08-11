/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-10 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-10 23:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.springai.controller.ChatMemoryTestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring AI Chat Memory 测试服务
 * <p>
 * 封装 chat-memory.adoc 中各类用法的实现细节：内存式存储、在 ChatClient 中使用记忆
 * （{@link MessageChatMemoryAdvisor}）、在 ChatModel 中手动维护记忆、自定义窗口大小、
 * 以及基于 {@link ChatMemoryRepository} 的会话管理。
 * </p>
 *
 * <p>{@link ChatMemoryTestController} 仅负责 HTTP 参数绑定与 {@code bytedesk.debug} 开关校验，
 * 真正的记忆编排、ChatClient 构建与 LLM 调用均在本服务中完成。</p>
 *
 * <p>注意：</p>
 * <ul>
 *   <li>使用 Spring AI 原生 {@code deepseekChatModel}，保证功能调用 / 记忆 Advisor 可用。</li>
 *   <li>共享的 {@link ChatMemory} 在类初始化时创建一次，所有请求共享同一实例，
 *       才能让同一 {@code conversationId} 的多轮消息跨请求被记住。
 *       （若在请求方法内新建 ChatMemory，每次请求都会得到一个全新的空仓库，"多轮记忆失效"。）</li>
 *   <li>默认使用进程内 {@link InMemoryChatMemoryRepository}（基于 {@code ConcurrentHashMap}，线程安全）；
 *       生产环境建议替换为 JDBC / Redis / Cassandra 等持久化仓库。</li>
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
	 * 共享的会话记忆存储（单例）。
	 * <p>
	 * 关键点：{@link MessageWindowChatMemory} 的历史消息保存在其内部 repository 中。
	 * 必须在类初始化时创建一次、所有请求共享同一个实例，才能让同一
	 * {@code conversationId} 的多轮消息跨请求被记住。
	 * </p>
	 * <p>
	 * 默认 maxMessages=20（即 Spring AI 自动配置 ChatMemory 时的默认窗口大小）。
	 * </p>
	 */
	private final ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(20).build();

	/**
	 * 底层仓库引用，用于演示 {@link ChatMemoryRepository} 的会话管理 API
	 * （{@code findConversationIds} / {@code findByConversationId} / {@code deleteByConversationId}）。
	 */
	private final ChatMemoryRepository chatMemoryRepository = new InMemoryChatMemoryRepository();

	/**
	 * 用于「仓库管理」演示的独立 ChatMemory（绑定上面的 chatMemoryRepository），
	 * 与 {@link #chatMemory} 隔离，避免污染 Advisor 演示链路。
	 */
	private final ChatMemory repositoryChatMemory = MessageWindowChatMemory.builder()
		.chatMemoryRepository(chatMemoryRepository)
		.maxMessages(20)
		.build();

	public ChatMemoryTestService(
			@Qualifier("deepseekChatModel") ObjectProvider<ChatModel> deepseekChatModelProvider) {
		this.chatModel = deepseekChatModelProvider.getIfAvailable();
	}

	// ============================================================
	// 1. Memory in ChatClient —— MessageChatMemoryAdvisor（多轮对话）
	// ============================================================

	/**
	 * 演示在 {@link ChatClient} 中使用 {@link MessageChatMemoryAdvisor} 维护多轮对话记忆。
	 *
	 * <p>对应 chat-memory.adoc 「Memory in Chat Client」一节：
	 * Advisor 在每次交互时自动从 ChatMemory 取出会话历史并注入 prompt，
	 * 无需手动拼历史消息。</p>
	 *
	 * <p>注意：{@code ChatMemory.CONVERSATION_ID} 是必需参数，省略会抛
	 * {@link IllegalArgumentException}。</p>
	 *
	 * @param message        用户输入
	 * @param conversationId 会话 ID，同一 ID 的多轮消息会被关联
	 * @return 结果 map（含 advisor / message / response / conversationId / history）
	 */
	public Map<String, Object> chatClientMemory(String message, String conversationId) {
		log.info("[chat-memory] chat-client memory: conversationId={}, message={}", conversationId, message);

		ChatClient chatClient = ChatClient.builder(chatModel)
			.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
			.build();

		String content = chatClient.prompt()
			.user(message)
			.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
			.call()
			.content();

		Map<String, Object> result = buildResult("MessageChatMemoryAdvisor", message, content);
		result.put("conversationId", conversationId);
		// 返回当前会话中记忆里的全部消息，便于观察“多轮记忆”效果
		result.put("history", formatMessages(chatMemory.get(conversationId)));
		result.put("historyCount", chatMemory.get(conversationId).size());
		return result;
	}

	// ============================================================
	// 2. Memory in ChatModel —— 手动维护记忆（无 Advisor）
	// ============================================================

	/**
	 * 演示直接操作 {@link ChatModel} 时手动维护记忆：
	 * 把用户消息与模型回复显式 {@code add} 进 ChatMemory，下次调用前 {@code get} 出全部历史一起发给模型。
	 *
	 * <p>对应 chat-memory.adoc 「Memory in Chat Model」一节的 James Bond 示例。</p>
	 *
	 * @param message        用户输入
	 * @param conversationId 会话 ID
	 * @return 结果 map（含 mode / message / response / conversationId / history）
	 */
	public Map<String, Object> chatModelMemory(String message, String conversationId) {
		log.info("[chat-memory] chat-model memory: conversationId={}, message={}", conversationId, message);

		// 1. 记录用户消息
		chatMemory.add(conversationId, new UserMessage(message));
		// 2. 取出会话历史一起发给模型
		ChatResponse chatResponse = chatModel.call(new Prompt(chatMemory.get(conversationId)));
		// 3. 记录模型回复
		chatMemory.add(conversationId, chatResponse.getResult().getOutput());

		Map<String, Object> result = buildResult("Manual (ChatModel + ChatMemory)", message,
				chatResponse.getResult().getOutput().getText());
		result.put("mode", "manual");
		result.put("conversationId", conversationId);
		result.put("history", formatMessages(chatMemory.get(conversationId)));
		result.put("historyCount", chatMemory.get(conversationId).size());
		return result;
	}

	// ============================================================
	// 3. MessageWindowChatMemory —— 滑动窗口
	// ============================================================

	/**
	 * 演示 {@link MessageWindowChatMemory} 的滑动窗口：超过 {@code maxMessages} 时，
	 * 旧消息被淘汰，但始终保留 {@code SystemMessage}。
	 *
	 * <p>对应 chat-memory.adoc 「Message Window Chat Memory」一节。</p>
	 *
	 * <p>这里用一个小窗口（默认 3）演示淘汰效果：连续写入若干消息后观察保留情况。
	 * 注意：窗口淘汰以「完整一轮（UserMessage 起始）」为边界。</p>
	 *
	 * @param maxMessages   窗口大小（上限）
	 * @param turns         要写入的轮次数（每轮一条 UserMessage）
	 * @return 结果 map（含 maxMessages / turns / remainingMessages / messages）
	 */
	public Map<String, Object> messageWindow(int maxMessages, int turns) {
		log.info("[chat-memory] message window: maxMessages={}, turns={}", maxMessages, turns);

		ChatMemory window = MessageWindowChatMemory.builder().maxMessages(maxMessages).build();
		String conversationId = "window-demo";

		// 写入 turns 条用户消息（不调用模型，纯演示窗口淘汰）
		for (int i = 1; i <= turns; i++) {
			window.add(conversationId, new UserMessage("第 " + i + " 条消息"));
		}

		List<Message> remaining = window.get(conversationId);
		Map<String, Object> result = new HashMap<>();
		result.put("memoryType", "MessageWindowChatMemory");
		result.put("maxMessages", maxMessages);
		result.put("writtenCount", turns);
		result.put("remainingCount", remaining.size());
		result.put("remainingMessages", formatMessages(remaining));
		result.put("explain", "超过 maxMessages 的旧消息会被淘汰；SystemMessage 始终保留。"
				+ "淘汰以完整一轮（UserMessage 起始）为边界，因此实际保留数可能略小于 maxMessages。");
		result.put("timestamp", System.currentTimeMillis());
		return result;
	}

	// ============================================================
	// 4. 会话 ID 隔离 —— 多用户互不干扰
	// ============================================================

	/**
	 * 演示「会话 ID 是会话的唯一作用域」：不同 conversationId 的消息互不可见。
	 *
	 * <p>对应 chat-memory.adoc 「Working with Conversation IDs」一节：
	 * 多用户应用中应为每个用户（及每个会话）分配唯一 conversationId。</p>
	 *
	 * <p>本演示不调用模型，只往两个会话各写一条消息，再分别读取，展示隔离效果。</p>
	 *
	 * @param userA 用户 A 的标识
	 * @param userB 用户 B 的标识
	 * @return 结果 map（含 userA / userB 的会话内容）
	 */
	public Map<String, Object> conversationIsolation(String userA, String userB) {
		log.info("[chat-memory] conversation isolation: userA={}, userB={}", userA, userB);

		// 模拟从用户/会话派生 conversationId（生产环境应在服务端按当前用户/会话生成）
		String convA = userA + ":session-1";
		String convB = userB + ":session-1";

		ChatMemory memory = MessageWindowChatMemory.builder().maxMessages(10).build();
		memory.add(convA, new UserMessage("我是用户 A，我说的是秘密 A"));
		memory.add(convB, new UserMessage("我是用户 B，我说的是秘密 B"));

		Map<String, Object> result = new HashMap<>();
		result.put("scenario", "Conversation ID Isolation");
		result.put("conversationIdA", convA);
		result.put("conversationIdB", convB);
		result.put("messagesA", formatMessages(memory.get(convA)));
		result.put("messagesB", formatMessages(memory.get(convB)));
		result.put("explain", "会话 ID 是会话的唯一作用域：不同 conversationId 的消息互不可见。"
				+ "多用户应用中应为每个用户/会话分配唯一 conversationId，避免串话。");
		result.put("timestamp", System.currentTimeMillis());
		return result;
	}

	// ============================================================
	// 5. ChatMemoryRepository 会话管理
	// ============================================================

	/**
	 * 演示 {@link ChatMemoryRepository} 的会话管理 API：列出会话、读取会话、删除会话。
	 *
	 * <p>对应 chat-memory.adoc 「Memory Storage」一节。</p>
	 *
	 * @param action       操作：list / get / delete
	 * @param conversationId 目标会话 ID（get/delete 必填）
	 * @param message      写入演示消息（用于先制造数据，可选）
	 * @return 结果 map（随 action 不同返回不同字段）
	 */
	public Map<String, Object> repository(String action, String conversationId, String message) {
		log.info("[chat-memory] repository action: {}, conversationId={}", action, conversationId);
		Map<String, Object> result = new HashMap<>();
		result.put("repositoryType", chatMemoryRepository.getClass().getSimpleName());
		result.put("action", action);

		switch (action == null ? "" : action.toLowerCase()) {
			case "list" -> {
				List<String> ids = chatMemoryRepository.findConversationIds();
				result.put("conversationIds", ids);
				result.put("conversationCount", ids.size());
				result.put("explain", "findConversationIds() 返回仓库中所有会话 ID。"
						+ "注意：列表/删除时应只操作属于当前用户的会话 ID。");
			}
			case "get" -> {
				if (message != null && !message.isEmpty()) {
					repositoryChatMemory.add(conversationId, new UserMessage(message));
				}
				List<Message> msgs = chatMemoryRepository.findByConversationId(conversationId);
				result.put("conversationId", conversationId);
				result.put("messages", formatMessages(msgs));
				result.put("messageCount", msgs.size());
			}
			case "delete" -> {
				chatMemoryRepository.deleteByConversationId(conversationId);
				result.put("conversationId", conversationId);
				result.put("deleted", true);
				result.put("explain", "deleteByConversationId() 清空指定会话的全部消息。");
			}
			default -> {
				result.put("error", "Unknown action: " + action + ". Supported: list / get / delete");
			}
		}
		result.put("timestamp", System.currentTimeMillis());
		return result;
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
