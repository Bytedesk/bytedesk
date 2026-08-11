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
package com.bytedesk.ai.springai.controller;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.springai.service.AdviserTestService;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring AI Advisors 测试控制器（HTTP 薄层）
 * <p>
 * 仅负责 HTTP 参数绑定与 {@code bytedesk.debug} 开关校验，真正的 Advisor 编排、ChatClient
 * 构建与 LLM 调用均委托给 {@link AdviserTestService}。
 * </p>
 *
 * <p>演示 Spring AI 2.0 Advisors API 的各类用法：日志、Re-Reading(Re2)、Chat Memory、
 * 内容安全(SafeGuard)、Tool Calling、多 Advisor 编排与执行顺序等。
 * </p>
 *
 * <p>注意：</p>
 * <ul>
 *   <li>所有接口受 {@code bytedesk.debug=true} 保护，生产环境返回 unavailable。</li>
 *   <li>使用 Spring AI 原生 {@code deepseekChatModel}（内置 function calling），
 *       通过 {@code @ConditionalOnBean(name="deepseekChatModel")} 守护。</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/adviser")
@RequiredArgsConstructor
@ConditionalOnBean(name = "deepseekChatModel")
public class AdviserTestController {

	private final AdviserTestService adviserTestService;

	private final BytedeskProperties bytedeskProperties;

	// ============================================================
	// 1. SimpleLoggerAdvisor —— 框架内置日志 Advisor
	// ============================================================

	/**
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/logging?message=你好
	 */
	@GetMapping("/logging")
	public ResponseEntity<JsonResult<?>> logging(
			@RequestParam(value = "message", defaultValue = "用一句话介绍 Spring AI") String message) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(adviserTestService.logging(message)));
		}
		catch (Exception e) {
			log.error("logging advisor failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 2. 自定义 MyLoggingAdvisor —— 打印 SYSTEM/TOOLS/TEXT 等明细
	// ============================================================

	/**
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/my-logging?message=你好
	 */
	@GetMapping("/my-logging")
	public ResponseEntity<JsonResult<?>> myLogging(
			@RequestParam(value = "message", defaultValue = "用一句话介绍 Spring AI") String message) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(adviserTestService.myLogging(message)));
		}
		catch (Exception e) {
			log.error("my-logging advisor failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 3. Re-Reading (Re2) Advisor —— 通过“重读问题”增强推理
	// ============================================================

	/**
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/re-reading?message=一个房间里有3个开关...
	 */
	@GetMapping("/re-reading")
	public ResponseEntity<JsonResult<?>> reReading(
			@RequestParam(value = "message",
					defaultValue = "小明有5个苹果，吃了2个，又买了3个，现在有几个？") String message) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(adviserTestService.reReading(message)));
		}
		catch (Exception e) {
			log.error("re-reading advisor failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 4. Chat Memory Advisor —— 多轮对话记忆
	// ============================================================

	/**
	 * <p>调用示例（同一 conversationId 内多轮）：</p>
	 * <pre>
	 *   GET .../memory?message=我叫张三&amp;conversationId=u1
	 *   GET .../memory?message=我刚才说叫什么名字？&amp;conversationId=u1
	 * </pre>
	 *
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/memory?message=你好&conversationId=demo
	 */
	@GetMapping("/memory")
	public ResponseEntity<JsonResult<?>> memory(
			@RequestParam(value = "message", defaultValue = "你好，请记住我叫张三") String message,
			@RequestParam(value = "conversationId", defaultValue = "bytedesk-demo") String conversationId) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(adviserTestService.memory(message, conversationId)));
		}
		catch (Exception e) {
			log.error("memory advisor failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 5. SafeGuardAdvisor —— 内容安全/敏感词拦截
	// ============================================================

	/**
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/safe-guard?message=hello
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/safe-guard?message=敏感词1
	 */
	@GetMapping("/safe-guard")
	public ResponseEntity<JsonResult<?>> safeGuard(
			@RequestParam(value = "message", defaultValue = "用一句话介绍猫") String message) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(adviserTestService.safeGuard(message)));
		}
		catch (Exception e) {
			log.error("safe-guard advisor failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 6. Tool Calling —— 通过 .defaultTools() 自动注册 ToolCallingAdvisor
	// ============================================================

	/**
	 * <p>双重保障：预取真实天气（Open-Meteo）注入 prompt + 注册 WeatherTools 工具。</p>
	 *
	 * <p>无需手动传 city：未传时自动用大模型从 message 中抽取城市名</p>
	 *
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/tool-calling?message=北京今天天气怎么样？
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/tool-calling?message=杭州明天会下雨吗&city=杭州
	 */
	@GetMapping("/tool-calling")
	public ResponseEntity<JsonResult<?>> toolCalling(
			@RequestParam(value = "message", defaultValue = "北京今天天气怎么样？") String message,
			@RequestParam(value = "city", required = false) String city) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(adviserTestService.toolCalling(message, city)));
		}
		catch (Exception e) {
			log.error("tool-calling failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 7. 多 Advisor 编排 + 执行顺序演示
	// ============================================================

	/**
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/chain?message=你好
	 */
	@GetMapping("/chain")
	public ResponseEntity<JsonResult<?>> chain(
			@RequestParam(value = "message", defaultValue = "用一句话介绍 Spring AI") String message,
			@RequestParam(value = "conversationId", defaultValue = "chain-demo") String conversationId) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(adviserTestService.chain(message, conversationId)));
		}
		catch (Exception e) {
			log.error("chain advisor failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	/**
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/order?message=你好
	 */
	@GetMapping("/order")
	public ResponseEntity<JsonResult<?>> order(
			@RequestParam(value = "message", defaultValue = "用一句话介绍 Spring AI") String message) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(adviserTestService.order(message)));
		}
		catch (Exception e) {
			log.error("order advisor failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 8. 自定义 BaseAdvisor 完整示例（POST + 结构化输入）
	// ============================================================

	/**
	 * <p>请求体：</p>
	 * <pre>
	 * POST http://127.0.0.1:9003/spring/ai/api/v1/adviser/custom
	 * {
	 *   "message": "介绍 Python",
	 *   "prefix": "请用中文回答：",
	 *   "uppercase": true
	 * }
	 * </pre>
	 */
	@PostMapping("/custom")
	public ResponseEntity<JsonResult<?>> custom(@RequestBody Map<String, Object> body) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			String message = str(body.get("message"), "用一句话介绍 Python");
			String prefix = str(body.get("prefix"), "请用中文回答：");
			boolean uppercase = Boolean.TRUE.equals(body.get("uppercase"));
			return ResponseEntity.ok(JsonResult.success(adviserTestService.custom(message, prefix, uppercase)));
		}
		catch (Exception e) {
			log.error("custom advisor failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 9. QuestionAnswerAdvisor —— RAG 即 Advisor（向量库检索 + 问答）
	// ============================================================

	/**
	 * <p>演示 {@code QuestionAnswerAdvisor}：把 RAG 包装成 Advisor，
	 * 自动检索向量库相关文档并拼到用户问题中，再交给 LLM 作答。</p>
	 *
	 * <p>对应 retrieval-augmented-generation.adoc 中 Advisors 一节的 QuestionAnswerAdvisor 示例。
	 * 需要运行环境配置了 VectorStore，否则返回 unavailable。</p>
	 *
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/qa?message=什么时间考试？
	 */
	@GetMapping("/qa")
	public ResponseEntity<JsonResult<?>> qa(
			@RequestParam(value = "message", defaultValue = "什么时间考试？") String message) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(adviserTestService.qa(message)));
		}
		catch (Exception e) {
			log.error("qa advisor failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 10. VectorStoreChatMemoryAdvisor —— 基于向量库的长期对话记忆
	// ============================================================

	/**
	 * <p>演示 {@code VectorStoreChatMemoryAdvisor}：把对话历史存入向量库，
	 * 每轮对话按语义相似度召回相关历史，适合超长对话与跨会话记忆。</p>
	 *
	 * <p>对比 {@code /memory} 使用的 {@code MessageChatMemoryAdvisor}（按窗口保留全部历史），
	 * 本 Advisor 只召回语义相关的历史片段。需要运行环境配置了 VectorStore。</p>
	 *
	 * <p>调用示例（同一 conversationId 内多轮）：</p>
	 * <pre>
	 *   GET .../vector-memory?message=我叫李四&amp;conversationId=vm1
	 *   GET .../vector-memory?message=我刚才说叫什么名字？&amp;conversationId=vm1
	 * </pre>
	 *
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/vector-memory?message=你好&conversationId=vector-memory-demo
	 */
	@GetMapping("/vector-memory")
	public ResponseEntity<JsonResult<?>> vectorMemory(
			@RequestParam(value = "message", defaultValue = "你好，请记住我叫李四") String message,
			@RequestParam(value = "conversationId", defaultValue = "vector-memory-demo") String conversationId) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity
				.ok(JsonResult.success(adviserTestService.vectorMemory(message, conversationId)));
		}
		catch (Exception e) {
			log.error("vector-memory advisor failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 11. SelfRefineEvaluationAdvisor —— LLM-as-a-Judge 自我精炼
	// ============================================================

	/**
	 * <p>演示 LLM-as-a-Judge 模式：评判模型（Ollama）对主模型回复打分，
	 * 不通过则带反馈重试直到评分达标。需要 Ollama 评判模型可用。</p>
	 *
	 * <p>提问示例：巴黎天气怎么样？会触发 UnstableWeatherTools 返回随机温度，
	 * 部分回答会被评判模型否决并触发重试。</p>
	 *
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/self-refine-evaluation?message=巴黎天气怎么样？
	 */
	@GetMapping("/self-refine-evaluation")
	public ResponseEntity<JsonResult<?>> selfRefineEvaluation(
			@RequestParam(value = "message", defaultValue = "巴黎天气怎么样？") String message) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(adviserTestService.selfRefineEvaluation(message)));
		}
		catch (Exception e) {
			log.error("self-refine-evaluation advisor failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	// ============================================================
	// 12. AugmentedToolCallbackProvider —— 工具参数增强
	// ============================================================

	/**
	 * <p>演示 {@code AugmentedToolCallbackProvider}：透明注入 AgentThinking 到工具调用 schema，
	 * 让 LLM 在调用工具时同步记录推理过程、置信度和记忆笔记。</p>
	 *
	 * <p>查看服务端 TOOL-AUGMENT-DEMO 日志观察 LLM 推理输出。</p>
	 *
	 * GET http://127.0.0.1:9003/spring/ai/api/v1/adviser/tool-argument-augment?message=北京今天天气怎么样？
	 */
	@GetMapping("/tool-argument-augment")
	public ResponseEntity<JsonResult<?>> toolArgumentAugment(
			@RequestParam(value = "message", defaultValue = "北京今天天气怎么样？") String message) {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.ok(JsonResult.error("Service is not available"));
		}
		try {
			return ResponseEntity.ok(JsonResult.success(adviserTestService.toolArgumentAugment(message)));
		}
		catch (Exception e) {
			log.error("tool-argument-augment failed", e);
			return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
		}
	}

	private static String str(Object obj, String defaultValue) {
		if (obj == null) {
			return defaultValue;
		}
		String s = String.valueOf(obj).trim();
		return s.isEmpty() ? defaultValue : s;
	}

}
