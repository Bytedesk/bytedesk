/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.adviser;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;

/**
 * Re-Reading (Re2) Advisor — 通过“重读”用户问题来增强 LLM 推理能力.
 *
 * <p>参考：</p>
 * <ul>
 *   <li><a href="https://docs.spring.io/spring-ai/reference/api/advisors.html">Spring AI Advisors API</a></li>
 *   <li><a href="https://arxiv.org/pdf/2309.06275">Re-Reading Improves Reasoning in LLMs</a></li>
 * </ul>
 *
 * <p>实现 {@link BaseAdvisor}，在 {@code before} 阶段把用户输入改写为：</p>
 * <pre>
 *   {Input_Query}
 *   Read the question again: {Input_Query}
 * </pre>
 * <p>从而在不改变请求语义的情况下提升模型对问题的理解。order 越小越先执行（请求处理阶段先执行，
 * 响应处理阶段后执行）。</p>
 */
public class ReReadingAdvisor implements BaseAdvisor {

	/**
	 * 默认 Re2 改写模板：将原始问题打印两遍，第二遍加上“再读一遍问题”的提示。
	 */
	public static final String DEFAULT_RE2_ADVISE_TEMPLATE = """
			{re2_input_query}
			Read the question again: {re2_input_query}
			""";

	private final String re2AdviseTemplate;

	private final int order;

	public ReReadingAdvisor() {
		this(DEFAULT_RE2_ADVISE_TEMPLATE, 0);
	}

	public ReReadingAdvisor(String re2AdviseTemplate, int order) {
		this.re2AdviseTemplate = re2AdviseTemplate != null ? re2AdviseTemplate : DEFAULT_RE2_ADVISE_TEMPLATE;
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * 请求前处理：把用户输入按 Re2 模板增强后，替换原 Prompt 的 user message。
	 */
	@Override
	public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
		String userInput = chatClientRequest.prompt().getUserMessage().getText();

		String augmentedUserText = PromptTemplate.builder()
			.template(this.re2AdviseTemplate)
			.variables(Map.of("re2_input_query", userInput == null ? "" : userInput))
			.build()
			.render();

		return chatClientRequest.mutate()
			.prompt(chatClientRequest.prompt().augmentUserMessage(augmentedUserText))
			.build();
	}

	/**
	 * 响应后处理：Re2 只增强输入，对响应不做改动，直接透传。
	 */
	@Override
	public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
		return chatClientResponse;
	}

	/**
	 * 便捷构造：返回一个指定 order 的新实例。
	 */
	public ReReadingAdvisor withOrder(int order) {
		return new ReReadingAdvisor(this.re2AdviseTemplate, order);
	}

}
