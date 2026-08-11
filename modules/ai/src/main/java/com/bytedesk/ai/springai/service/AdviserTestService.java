/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-10 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-10 22:30:00
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.augment.AugmentedToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.springai.adviser.MyLoggingAdvisor;
import com.bytedesk.ai.springai.adviser.PrefixUppercaseAdvisor;
import com.bytedesk.ai.springai.adviser.ReReadingAdvisor;
import com.bytedesk.ai.springai.adviser.SelfRefineEvaluationAdvisor;
import com.bytedesk.ai.springai.adviser.TagAdvisor;
import com.bytedesk.ai.springai.adviser.WeatherTools;
import com.bytedesk.ai.springai.controller.AdviserTestController;
import com.bytedesk.ai.tool.test.WeatherService;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring AI Advisors 测试服务
 * <p>
 * 封装 Spring AI 2.0 Advisors API 的各类用法实现细节：日志、Re-Reading(Re2)、Chat Memory、
 * 内容安全(SafeGuard)、Tool Calling、多 Advisor 编排与执行顺序等。
 * </p>
 *
 * <p>{@link AdviserTestController} 仅负责 HTTP 参数绑定与 {@code bytedesk.debug} 开关校验，
 * 真正的 Advisor 编排、ChatClient 构建与 LLM 调用均在本服务中完成。</p>
 *
 * <p>注意：</p>
 * <ul>
 *   <li>使用 Spring AI 原生 {@code deepseekChatModel}（而非自研 DashScopeChatModel），
 *       因为后者 {@code DashScopeChatOptions} 未实现 {@code ToolCallingChatOptions}，
 *       无法把 {@code defaultTools(...)} 注册的工具传给 LLM。</li>
 *   <li>DeepSeekChatModel 是 Spring AI 原生实现，内置 function calling 支持。</li>
 * </ul>
 *
 * @see AdviserTestController
 */
@Slf4j
@Service
public class AdviserTestService {

	/**
	 * 使用 Spring AI 原生的 deepseekChatModel（而非自研 DashScopeChatModel），
	 * 因为后者 {@code DashScopeChatOptions} 未实现 {@code ToolCallingChatOptions}，
	 * 无法把 {@code defaultTools(...)} 注册的工具传给 LLM。
	 * DeepSeekChatModel 是 Spring AI 原生实现，内置 function calling 支持。
	 */
	private final ChatModel chatModel;

	/**
	 * 评判模型（用于 SelfRefineEvaluationAdvisor 的 LLM-as-a-Judge）。
	 * 优先使用 Ollama 本地模型（低成本高吞吐），若未配置则回退到主模型。
	 */
	private final ChatModel judgeModel;

	/**
	 * 统一的真实天气工具服务，提供 Open-Meteo 真实气温查询。
	 * 原先散落在本类的 getRealWeatherByCity/resolveCity 以及 {@link WeatherTools}、
	 * {@code ToolWeatherService} 中的假数据，均已收敛到此服务。
	 */
	private final WeatherService weatherService;

	/**
	 * 可选的向量库，用于演示 RAG 类 Advisor（{@link QuestionAnswerAdvisor}、
	 * {@link VectorStoreChatMemoryAdvisor}）。
	 * <p>
	 * 通过 {@link ObjectProvider} 注入，若运行环境未配置向量库则为 {@code null}，
	 * 相关接口会返回提示信息而不是启动失败。
	 * </p>
	 */
	private final VectorStore vectorStore;

	/**
	 * 共享的会话记忆存储（单例）。
	 * <p>
	 * 关键点：{@link MessageWindowChatMemory} 的历史消息保存在其内部 repository 中。
	 * 必须在类初始化时创建一次、所有请求共享同一个实例，
	 * 才能让同一 {@code conversationId} 的多轮消息跨请求被记住。
	 * </p>
	 * <p>
	 * 若在请求方法内 {@code new} 一个新的 {@link ChatMemory}（如原先的写法），
	 * 每次请求都会得到一个全新的空仓库，第 N 次请求读不到前 N-1 次写入的历史，
	 * 表现为"多轮记忆失效"（第二次请求不记得第一次说过的话）。
	 * </p>
	 * <p>
	 * 默认使用进程内存储（基于 {@code ConcurrentHashMap}，线程安全，可安全共享），
	 * 应用重启后丢失；生产环境建议替换为持久化的 {@code ChatMemoryRepository}
	 * （JDBC / Redis 等），并以 {@code @Bean} 形式注入。
	 * </p>
	 */
	private final ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(20).build();

	public AdviserTestService(
			@Qualifier("deepseekChatModel") ObjectProvider<ChatModel> deepseekChatModelProvider,
			@Qualifier("bytedeskOllamaChatModel") ObjectProvider<ChatModel> ollamaChatModelProvider,
			WeatherService weatherService,
			ObjectProvider<VectorStore> vectorStoreProvider) {
		this.chatModel = deepseekChatModelProvider.getIfAvailable();
		this.judgeModel = ollamaChatModelProvider.getIfAvailable();
		this.weatherService = weatherService;
		this.vectorStore = vectorStoreProvider.getIfAvailable();
	}

	// ============================================================
	// 1. SimpleLoggerAdvisor —— 框架内置日志 Advisor
	// ============================================================

	/**
	 * 演示框架内置的 {@link SimpleLoggerAdvisor}：在请求前后打印日志。
	 *
	 * @param message 用户输入
	 * @return 结果 map（含 advisor / message / response / responseLength / timestamp）
	 */
	public Map<String, Object> logging(String message) {
		log.info("[advisor] logging request: {}", message);
		ChatClient chatClient = ChatClient.builder(chatModel)
			.defaultAdvisors(new SimpleLoggerAdvisor())
			.build();

		String content = chatClient.prompt().user(message).call().content();
		return buildResult("SimpleLoggerAdvisor", message, content);
	}

	// ============================================================
	// 2. 自定义 MyLoggingAdvisor —— 项目自带，打印 SYSTEM/TOOLS/TEXT 等明细
	// ============================================================

	/**
	 * 演示项目自带的 {@link MyLoggingAdvisor}（实现 {@link BaseAdvisor}），
	 * 可观察请求中的 system message、可用 tools、用户文本以及响应内容。
	 */
	public Map<String, Object> myLogging(String message) {
		log.info("[advisor] my-logging request: {}", message);
		ChatClient chatClient = ChatClient.builder(chatModel)
			.defaultAdvisors(MyLoggingAdvisor.builder().order(0).showSystemMessage(true).build())
			.build();

		String content = chatClient.prompt().user(message).call().content();
		return buildResult("MyLoggingAdvisor", message, content);
	}

	// ============================================================
	// 3. Re-Reading (Re2) Advisor —— 通过"重读问题"增强推理
	// ============================================================

	/**
	 * 演示 {@link ReReadingAdvisor}：将用户问题改写为"问题 + 再读一遍问题"，
	 * 以提升 LLM 对复杂问题的理解（Re2 技术）。
	 */
	public Map<String, Object> reReading(String message) {
		log.info("[advisor] re-reading request: {}", message);
		ChatClient chatClient = ChatClient.builder(chatModel)
			.defaultAdvisors(new ReReadingAdvisor())
			.build();

		String content = chatClient.prompt().user(message).call().content();
		Map<String, Object> result = buildResult("ReReadingAdvisor", message, content);
		result.put("augmented", message + System.lineSeparator() + "Read the question again: " + message);
		return result;
	}

	// ============================================================
	// 4. Chat Memory Advisor —— 多轮对话记忆
	// ============================================================

	/**
	 * 演示 {@link MessageChatMemoryAdvisor} + {@link MessageWindowChatMemory}，
	 * 使用 {@code conversationId} 区分不同会话，演示跨请求的多轮记忆。
	 */
	public Map<String, Object> memory(String message, String conversationId) {
		log.info("[advisor] memory request: conversationId={}, message={}", conversationId, message);

		// 复用类级别的单例 chatMemory：同一 conversationId 的历史才能跨请求被读取
		ChatClient chatClient = ChatClient.builder(chatModel)
			.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory)
				.order(Ordered.HIGHEST_PRECEDENCE + 1000)
				.build())
			.build();

		String content = chatClient.prompt()
			.user(message)
			.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
			.call()
			.content();

		Map<String, Object> result = buildResult("MessageChatMemoryAdvisor", message, content);
		result.put("conversationId", conversationId);
		result.put("hint", "同一 conversationId 内的消息会被记住，换一个 conversationId 即开启全新会话");
		return result;
	}

	// ============================================================
	// 5. SafeGuardAdvisor —— 内容安全/敏感词拦截
	// ============================================================

	/**
	 * 演示 {@link SafeGuardAdvisor}：当用户输入命中敏感词时，直接返回拦截文案，
	 * 不会把请求转发给 LLM。
	 */
	public Map<String, Object> safeGuard(String message) {
		log.info("[advisor] safe-guard request: {}", message);
		Advisor safeGuardAdvisor = SafeGuardAdvisor.builder()
			.sensitiveWords(List.of("敏感词1", "敏感词2", "forbidden"))
			.failureResponse("抱歉，该请求包含敏感内容，已被拦截。")
			.order(Ordered.HIGHEST_PRECEDENCE)
			.build();

		ChatClient chatClient = ChatClient.builder(chatModel).defaultAdvisors(safeGuardAdvisor).build();

		String content = chatClient.prompt().user(message).call().content();
		Map<String, Object> result = buildResult("SafeGuardAdvisor", message, content);
		result.put("sensitiveWords", List.of("敏感词1", "敏感词2", "forbidden"));
		return result;
	}

	// ============================================================
	// 6. Tool Calling —— 通过 .defaultTools() 自动注册 ToolCallingAdvisor
	// ============================================================

	/**
	 * 演示 Tool Calling + 路线 A（预取真实天气数据注入 prompt）。
	 *
	 * <p>双重保障：</p>
	 * <ol>
	 *   <li>预取真实天气（Open-Meteo）→ 注入 prompt（路线 A，可靠性）</li>
	 *   <li>注册 {@code WeatherTools} 工具 → DeepSeek 可主动调用（工具调用演示）</li>
	 * </ol>
	 *
	 * <p>城市名称解析优先级：</p>
	 * <ol>
	 *   <li>显式传入的 {@code city} 参数（非空非 blank）</li>
	 *   <li>未传 {@code city} 时，用大模型从 {@code message} 中自动抽取城市名
	 *       （如「北京今天天气怎么样」→「北京」）</li>
	 *   <li>抽取失败时回退到默认「北京」</li>
	 * </ol>
	 *
	 * @param message 用户输入
	 * @param city    城市名称（可选；为空时自动从 message 中抽取）
	 * @return 结果 map（含 advisor / message / response / city / extractedCity / realWeather / availableTools）
	 */
	public Map<String, Object> toolCalling(String message, String city) {
		log.info("[advisor] tool-calling request: city={}, message={}", city, message);

		// 城市解析：优先用显式传入的 city，否则用大模型从自然句中抽取
		boolean cityFromExtraction = false;
		String resolvedCity = city;
		if (resolvedCity == null || resolvedCity.isBlank()) {
			resolvedCity = extractCity(message);
			cityFromExtraction = true;
			log.info("[advisor] tool-calling extracted city from message: {}", resolvedCity);
		}

		// 路线 A：预取真实天气数据（Open-Meteo），注入 prompt 作为可靠上下文
		String realWeatherText = weatherService.getWeatherText(resolvedCity);
		log.info("[advisor] tool-calling pre-fetched real weather: {}", realWeatherText);
		String prompt = message + "\n\n以下是" + resolvedCity + "的真实天气数据（Open-Meteo），请据此回答：\n" + realWeatherText;

		ChatClient chatClient = ChatClient.builder(chatModel)
			.defaultTools(new WeatherTools(weatherService))
			// 显式打印工具调用过程
			.defaultAdvisors(MyLoggingAdvisor.builder().order(0).showAvailableTools(true).build())
			.build();

		String content = chatClient.prompt().user(prompt).call().content();
		Map<String, Object> result = buildResult("ToolCalling(deepseek + WeatherTools + real-weather-injection)",
				message, content);
		result.put("availableTools", List.of("getWeather"));
		result.put("city", resolvedCity);
		result.put("cityFromExtraction", cityFromExtraction);
		result.put("realWeather", realWeatherText);
		return result;
	}

	/**
	 * 用大模型从自然语言句子中抽取城市名称。
	 * <p>
	 * 例如「北京今天天气怎么样」→「北京」，「杭州明天会下雨吗」→「杭州」。
	 * 通过一段结构化提示词约束模型只输出城市名本身，便于直接传入 {@link WeatherService}。
	 * </p>
	 *
	 * @param message 用户输入的自然语言句子
	 * @return 抽取到的城市名称；抽取失败或无法识别时回退到 {@code "北京"}
	 */
	public String extractCity(String message) {
		if (message == null || message.isBlank()) {
			return "北京";
		}
		String extractionPrompt = "从下面的用户问题中提取出所查询的城市名称，只返回城市名称本身（不要加任何解释、标点或引号）。"
				+ "如果无法识别城市，返回“北京”。\n用户问题：" + message;
		try {
			String extracted = ChatClient.builder(chatModel).build()
				.prompt()
				.user(extractionPrompt)
				.call()
				.content();
			if (extracted == null || extracted.isBlank()) {
				return "北京";
			}
			// 清理模型可能误加的标点/空白（去除首尾的引号、句号、逗号等）
			String cleaned = extracted.trim();
			cleaned = cleaned.replaceAll("^[\"'”。，,\\s]+|[\"'”。，,\\s]+$", "").trim();
			return cleaned.isEmpty() ? "北京" : cleaned;
		}
		catch (Exception e) {
			log.warn("[advisor] extract city failed, fallback to 北京: {}", e.getMessage());
			return "北京";
		}
	}

	// ============================================================
	// 7. 多 Advisor 编排 + 执行顺序演示
	// ============================================================

	/**
	 * 演示把多个 Advisor 组成链，并展示 order 如何影响执行顺序。
	 *
	 * <p>本方法把以下 Advisor 组合在一起：</p>
	 * <ol>
	 *   <li>{@link SafeGuardAdvisor}（HIGHEST_PRECEDENCE，最先拦截敏感词）</li>
	 *   <li>{@link ReReadingAdvisor}（order=0，增强输入）</li>
	 *   <li>{@link MyLoggingAdvisor}（order=1，打印明细）</li>
	 *   <li>{@link MessageChatMemoryAdvisor}（靠后，注入历史记忆）</li>
	 * </ol>
	 */
	public Map<String, Object> chain(String message, String conversationId) {
		log.info("[advisor] chain request: conversationId={}, message={}", conversationId, message);

		// 复用类级别的单例 chatMemory，避免每请求新建空仓库导致 chain 模式下同样失忆
		Advisor safeGuardAdvisor = SafeGuardAdvisor.builder()
			.sensitiveWords(List.of("敏感词1", "forbidden"))
			.order(Ordered.HIGHEST_PRECEDENCE)
			.build();

		ChatClient chatClient = ChatClient.builder(chatModel)
			.defaultAdvisors(safeGuardAdvisor, new ReReadingAdvisor(), MyLoggingAdvisor.builder().order(1).build(),
					MessageChatMemoryAdvisor.builder(chatMemory).order(Ordered.HIGHEST_PRECEDENCE + 1000).build())
			.build();

		String content = chatClient.prompt()
			.user(message)
			.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
			.call()
			.content();

		Map<String, Object> result = buildResult("AdvisorChain[SafeGuard + ReReading + MyLogging + Memory]", message,
				content);
		result.put("chain",
				List.of("SafeGuardAdvisor(HIGHEST_PRECEDENCE)", "ReReadingAdvisor(order=0)", "MyLoggingAdvisor(order=1)",
						"MessageChatMemoryAdvisor(HIGHEST_PRECEDENCE+1000)"));
		result.put("conversationId", conversationId);
		return result;
	}

	/**
	 * 直观演示 Advisor 执行顺序：通过两个自定义 Advisor 在控制台输出 before/after 顺序，
	 * 说明"请求侧 order 小的先执行，响应侧 order 小的后执行（栈式）"。
	 */
	public Map<String, Object> order(String message) {
		log.info("[advisor] order request: {}", message);
		ChatClient chatClient = ChatClient.builder(chatModel)
			.defaultAdvisors(new TagAdvisor("A", 0), new TagAdvisor("B", 1))
			.build();

		String content = chatClient.prompt().user(message).call().content();
		Map<String, Object> result = buildResult("Order[A(order=0), B(order=1)]", message, content);
		result.put("explain", "请求阶段执行顺序：A.before → B.before（order 小的先）；"
				+ "响应阶段执行顺序：B.after → A.after（栈式回溯）。观察服务端控制台日志可见。");
		return result;
	}

	// ============================================================
	// 8. 自定义 BaseAdvisor 完整示例
	// ============================================================

	/**
	 * 自定义 Advisor 演示：实现 {@link BaseAdvisor}，在 before 阶段为用户输入加前缀，
	 * 在 after 阶段记录响应信息（演示 Advisor 可同时改写请求与观察响应的能力）。
	 *
	 * @param message   用户输入
	 * @param prefix    前缀
	 * @param uppercase 是否在 after 阶段记录大写标记
	 * @return 结果 map（含 advisor / message / response / prefix / uppercase）
	 */
	public Map<String, Object> custom(String message, String prefix, boolean uppercase) {
		log.info("[advisor] custom request: message={}, prefix={}, uppercase={}", message, prefix, uppercase);

		ChatClient chatClient = ChatClient.builder(chatModel)
			.defaultAdvisors(new PrefixUppercaseAdvisor(prefix, uppercase, 0))
			.build();

		ChatResponse chatResponse = chatClient.prompt().user(message).call().chatResponse();
		String content = chatResponse.getResult().getOutput().getText();

		Map<String, Object> result = buildResult("PrefixUppercaseAdvisor(custom BaseAdvisor)", message, content);
		result.put("prefix", prefix);
		result.put("uppercase", uppercase);
		return result;
	}

	// ============================================================
	// 9. QuestionAnswerAdvisor —— RAG 即 Advisor（来自 spring-ai-vector-store-advisor）
	// ============================================================

	/**
	 * 演示 {@link QuestionAnswerAdvisor}：把 RAG 包装成 Advisor，自动检索向量库相关文档
	 * 并拼到用户问题中，再交给 LLM 作答。
	 *
	 * <p>对应 Spring AI 文档 retrieval-augmented-generation.adoc 中 Advisors 一节的
	 * QuestionAnswerAdvisor 示例。</p>
	 *
	 * @param message 用户问题
	 * @return 结果 map（含 advisor / message / response / explain）
	 */
	public Map<String, Object> qa(String message) {
		log.info("[advisor] qa (QuestionAnswerAdvisor) request: {}", message);
		if (vectorStore == null) {
			Map<String, Object> unavailable = buildResult("QuestionAnswerAdvisor", message, null);
			unavailable.put("explain", "当前环境未配置 VectorStore，QuestionAnswerAdvisor 不可用");
			unavailable.put("available", false);
			return unavailable;
		}

		QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
			.searchRequest(SearchRequest.builder().similarityThreshold(0.8d).topK(6).build())
			.build();

		ChatClient chatClient = ChatClient.builder(chatModel).defaultAdvisors(qaAdvisor).build();

		String content = chatClient.prompt().user(message).call().content();
		Map<String, Object> result = buildResult("QuestionAnswerAdvisor", message, content);
		result.put("explain", "通过 Advisor API 实现 RAG：QuestionAnswerAdvisor 先查向量库，"
				+ "再把相关文档拼到用户问题中交给 LLM 作答");
		result.put("available", true);
		return result;
	}

	// ============================================================
	// 10. VectorStoreChatMemoryAdvisor —— 基于向量库的长期对话记忆
	// ============================================================

	/**
	 * 演示 {@link VectorStoreChatMemoryAdvisor}：把对话历史存入向量库，
	 * 每轮对话时按语义相似度召回相关历史，适合超长对话 / 跨会话记忆。
	 *
	 * <p>对比 {@link #memory(String, String)} 使用的 {@link MessageChatMemoryAdvisor}：
	 * 后者把全部历史按窗口（maxMessages）保留；前者按语义检索只召回相关片段。</p>
	 *
	 * <p>对应 Spring AI 文档 retrieval-augmented-generation.adoc 中提到的
	 * VectorStoreChatMemoryAdvisor（同样来自 spring-ai-vector-store-advisor）。</p>
	 *
	 * @param message        用户输入
	 * @param conversationId 会话 ID，同一 ID 的多轮消息会被关联
	 * @return 结果 map（含 advisor / message / response / conversationId / explain）
	 */
	public Map<String, Object> vectorMemory(String message, String conversationId) {
		log.info("[advisor] vector-memory request: conversationId={}, message={}", conversationId, message);
		if (vectorStore == null) {
			Map<String, Object> unavailable = buildResult("VectorStoreChatMemoryAdvisor", message, null);
			unavailable.put("explain", "当前环境未配置 VectorStore，VectorStoreChatMemoryAdvisor 不可用");
			unavailable.put("available", false);
			unavailable.put("conversationId", conversationId);
			return unavailable;
		}

		VectorStoreChatMemoryAdvisor advisor = VectorStoreChatMemoryAdvisor.builder(vectorStore)
			.defaultTopK(5)
			.build();

		ChatClient chatClient = ChatClient.builder(chatModel).defaultAdvisors(advisor).build();

		String content = chatClient.prompt()
			.user(message)
			.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
			.call()
			.content();

		Map<String, Object> result = buildResult("VectorStoreChatMemoryAdvisor", message, content);
		result.put("explain", "基于向量库的对话记忆：历史消息存入向量库，"
				+ "检索时按语义相似度召回相关片段，适合超长对话与跨会话记忆");
		result.put("available", true);
		result.put("conversationId", conversationId);
		return result;
	}

	// ============================================================
	// 11. SelfRefineEvaluationAdvisor —— LLM-as-a-Judge 自我精炼
	// ============================================================

	/**
	 * 演示 {@link SelfRefineEvaluationAdvisor}（LLM-as-a-Judge 模式）：
	 * 使用评判模型对主模型的回复打分，不通过则带反馈重试，直到评分达标或达到最大重试次数。
	 *
	 * <p>评判模型使用 Ollama（低成本高吞吐），主模型使用 DeepSeek。
	 * 若 Ollama 不可用，整个接口返回 unavailable。</p>
	 *
	 * <p>本演示注册了一个"质量不稳定的天气工具"（随机返回真实/异常温度），
	 * 以模拟 LLM 回答被评判模型否决再重试完整的 self-refine 循环。</p>
	 *
	 * @param message 用户输入
	 * @return 结果 map（含 advisor / message / response / rating / attempts / judgeAvailable 等）
	 */
	public Map<String, Object> selfRefineEvaluation(String message) {
		log.info("[advisor] self-refine-evaluation request: {}", message);

		if (judgeModel == null) {
			Map<String, Object> unavailable = buildResult("SelfRefineEvaluationAdvisor", message, null);
			unavailable.put("explain", "当前环境未配置 Ollama 评判模型（bytedeskOllamaChatModel），SelfRefineEvaluationAdvisor 不可用。" +
					"请在 application.properties 中配置 spring.ai.ollama.chat.* 并确保 Ollama 服务运行中。");
			unavailable.put("available", false);
			return unavailable;
		}

		// 注册一个"质量不稳定的天气工具"来演示 evaluate → retry → pass 循环
		ChatClient chatClient = ChatClient.builder(chatModel)
			.defaultTools(new UnstableWeatherTools())
			.defaultAdvisors(
				SelfRefineEvaluationAdvisor.builder()
					.chatClientBuilder(ChatClient.builder(judgeModel))
					.maxRepeatAttempts(5)
					.successRating(3)
					.order(0)
					.build(),
				MyLoggingAdvisor.builder().order(1).showAvailableTools(true).build())
			.build();

		String content = chatClient.prompt().user(message).call().content();

		Map<String, Object> result = buildResult("SelfRefineEvaluationAdvisor(LLM-as-a-Judge)", message, content);
		result.put("available", true);
		result.put("judgeModel", "Ollama (bytedeskOllamaChatModel)");
		result.put("maxRepeatAttempts", 5);
		result.put("successRating", 3);
		result.put("explain", "LLM-as-a-Judge 模式：评判模型（Ollama）评估回复质量（1-4分），" +
				"低于3分则附反馈重试。演示用 UnstableWeatherTools 模拟不稳定输出触发 retry。");
		return result;
	}

	// ============================================================
	// 12. AugmentedToolCallbackProvider —— 工具参数增强
	// ============================================================

	private static final Logger augmentLogger = LoggerFactory.getLogger("TOOL-AUGMENT-DEMO");

	/**
	 * Agent 思考记录，透明注入到工具调用 schema 中。
	 */
	public record AgentThinking(
			@ToolParam(description = "Your step-by-step reasoning for why you're calling this tool and what you expect",
					required = true) String innerThought,

			@ToolParam(description = "Confidence level (low, medium, high) in this tool choice",
					required = false) String confidence,

			@ToolParam(description = "Key insights to remember for future interactions",
					required = true) List<String> memoryNotes) {
	}

	/**
	 * 演示 {@link AugmentedToolCallbackProvider}（工具参数增强）：
	 * 透明地向工具 schema 注入额外参数（如 LLM 推理过程、置信度、记忆笔记），
	 * 消费后剥离，不修改原始工具实现。
	 *
	 * <p>核心价值：让 LLM 在调用工具时同步输出推理过程，增强可观测性和可解释性。</p>
	 *
	 * <p>用法场景：</p>
	 * <ul>
	 *   <li>调试 & 可观测性：记录 LLM 为什么选择某个工具</li>
	 *   <li>记忆增强：LLM 自主决定需要记住的关键信息</li>
	 *   <li>置信度评估：判断工具调用是否可靠</li>
	 * </ul>
	 *
	 * @param message 用户输入
	 * @return 结果 map（含 advisor / message / response / explain）
	 */
	public Map<String, Object> toolArgumentAugment(String message) {
		log.info("[advisor] tool-argument-augment request: {}", message);

		AugmentedToolCallbackProvider<AgentThinking> provider = AugmentedToolCallbackProvider
			.<AgentThinking>builder()
			.toolObject(new WeatherTools(weatherService))
			.argumentType(AgentThinking.class)
			.argumentConsumer(event -> {
				AgentThinking thinking = event.arguments();
				augmentLogger.info("🧠 LLM Reasoning: {}", thinking.innerThought());
				augmentLogger.info("📊 Confidence: {}", thinking.confidence());
				augmentLogger.info("📝 Memory Notes: {}", thinking.memoryNotes());
				augmentLogger.info("🔧 Tool: {}", event.toolDefinition().name());
			})
			.removeExtraArgumentsAfterProcessing(true)
			.build();

		ChatClient chatClient = ChatClient.builder(chatModel)
			.defaultTools(provider)
			.defaultAdvisors(MyLoggingAdvisor.builder().order(0).showAvailableTools(true).build())
			.build();

		String content = chatClient.prompt().user(message).call().content();

		Map<String, Object> result = buildResult("AugmentedToolCallbackProvider<ToolArgumentAugmenter>", message, content);
		result.put("explain", "工具参数增强：透明地向工具 schema 注入 AgentThinking（innerThought/confidence/memoryNotes），"
				+ "LLM 在调用工具时同步输出推理过程，消费后剥离再调实际工具。查看服务端 TOOL-AUGMENT-DEMO 日志。");
		result.put("available", true);
		return result;
	}

	/**
	 * 质量不稳定的天气工具 —— 用于演示 SelfRefineEvaluationAdvisor 的 evaluate→retry 循环。
	 * <p>
	 * 随机返回正常温度或异常温度（如 -255°C），让评判模型据此否决并触发重试。
	 * </p>
	 */
	static class UnstableWeatherTools {

		private final java.util.Random random = new java.util.Random();
		private final int[] temperatures = { -125, 25, -255, 18, 72, 30 };

		@Tool(description = "Get the current weather for a given location")
		public String getWeather(String location) {
			int temperature = temperatures[random.nextInt(temperatures.length)];
			log.info("[UnstableWeatherTools] getWeather({}) → {}°C", location, temperature);
			return "The current weather in " + location + " is sunny with a temperature of " + temperature + "°C.";
		}
	}

	// ============================================================
	// 辅助方法
	// ============================================================

	/**
	 * 统一构造结果 map。
	 * @param advisor  Advisor 名称/描述
	 * @param message  原始用户输入
	 * @param response LLM 响应文本
	 * @return 结果 map
	 */
	public Map<String, Object> buildResult(String advisor, String message, String response) {
		Map<String, Object> result = new HashMap<>();
		result.put("advisor", advisor);
		result.put("message", message);
		result.put("response", response);
		result.put("responseLength", response == null ? 0 : response.length());
		result.put("timestamp", System.currentTimeMillis());
		return result;
	}

}
