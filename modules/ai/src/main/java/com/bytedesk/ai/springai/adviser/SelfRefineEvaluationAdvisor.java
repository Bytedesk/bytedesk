/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-11 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-08-11 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.adviser;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.util.Assert;

/**
 * Self-Refine Evaluation Advisor (LLM-as-a-Judge)
 * <p>
 * 使用一个"评判模型"对主模型的回复进行打分评估，若评分未达到阈值则自动重试并附带改进反馈。
 * 灵感来自 Spring AI evaluation-recursive-advisor-demo。
 * </p>
 *
 * <p>工作流程：</p>
 * <ol>
 *   <li>主模型生成回复</li>
 *   <li>评判模型（通过独立 ChatClient）对回复评分（1-4分）</li>
 *   <li>若评分 ≥ successRating，直接返回</li>
 *   <li>若评分不足，将评判反馈附加到 prompt 中重新请求，直到通过或达到最大重试次数</li>
 * </ol>
 *
 * <p>Tool Call 响应自动跳过评估（only evaluates final text answers）。</p>
 *
 * <p>用法示例：</p>
 * <pre>{@code
 * ChatClient chatClient = ChatClient.builder(primaryModel)
 *     .defaultTools(new MyTools())
 *     .defaultAdvisors(
 *         SelfRefineEvaluationAdvisor.builder()
 *             .chatClientBuilder(ChatClient.builder(judgeModel))
 *             .maxRepeatAttempts(15)
 *             .successRating(4)
 *             .order(0)
 *             .build())
 *     .build();
 * }</pre>
 *
 * @author jackning 270580156@qq.com
 * @see <a href="https://docs.spring.io/spring-ai/reference/api/advisors.html">Spring AI Advisors</a>
 */
public final class SelfRefineEvaluationAdvisor implements CallAdvisor, StreamAdvisor {

	private static final Logger logger = LoggerFactory.getLogger(SelfRefineEvaluationAdvisor.class);

	private static final PromptTemplate DEFAULT_EVALUATION_PROMPT_TEMPLATE = new PromptTemplate(
			"""
						You will be given a user_question and assistant_answer couple.
						Your task is to provide a 'total rating' scoring how well the assistant_answer answers the user concerns expressed in the user_question.
						Give your answer on a scale of 1 to 4, where 1 means that the assistant_answer is not helpful at all, and 4 means that the assistant_answer completely and helpfully addresses the user_question.

						Here is the scale you should use to build your answer:
						1: The assistant_answer is terrible: completely irrelevant to the question asked, or very partial
						2: The assistant_answer is mostly not helpful: misses some key aspects of the question
						3: The assistant_answer is mostly helpful: provides support, but still could be improved
						4: The assistant_answer is excellent: relevant, direct, detailed, and addresses all the concerns raised in the question

						Provide your feedback as follows:

						\\{
					 		"rating": 0,
					  		"evaluation": "Explanation of the evaluation result and how to improve if needed.",
					  		"feedback": "Constructive and specific feedback on the assistant_answer."
						\\}

						Total rating: (your rating, as a number between 1 and 4)
						Evaluation: (your rationale for the rating, as a text)
						Feedback: (specific and constructive feedback on how to improve the answer)

						You MUST provide values for 'Evaluation:' and 'Total rating:' in your answer.

						Now here are the question and answer.

						Question: {question}
						Answer: {answer}

						Provide your feedback. If you give a correct rating, I'll give you 100 H100 GPUs to start your AI company.

						Evaluation:
					""");

	private final PromptTemplate evaluationPromptTemplate;
	private final int successRating;
	private final int advisorOrder;
	private final int maxRepeatAttempts;
	private final ChatClient chatClient;
	private final BiPredicate<ChatClientRequest, ChatClientResponse> skipEvaluationPredicate;

	/**
	 * 评估响应记录。
	 */
	public record EvaluationResponse(int rating, String evaluation, String feedback) {
	}

	private SelfRefineEvaluationAdvisor(int advisorOrder, int maxRepeatAttempts, ChatClient.Builder chatClientBuilder,
			PromptTemplate promptTemplate, int successRating,
			BiPredicate<ChatClientRequest, ChatClientResponse> skipEvaluationPredicate) {

		this.chatClient = chatClientBuilder.build();
		this.evaluationPromptTemplate = promptTemplate;
		this.advisorOrder = advisorOrder;
		this.maxRepeatAttempts = maxRepeatAttempts;
		this.skipEvaluationPredicate = skipEvaluationPredicate;
		this.successRating = successRating;
	}

	@Override
	public String getName() {
		return "SelfRefineEvaluationAdvisor";
	}

	@Override
	public int getOrder() {
		return this.advisorOrder;
	}

	@Override
	public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
		Assert.notNull(chatClientRequest, "chatClientRequest must not be null");
		Assert.notNull(callAdvisorChain, "callAdvisorChain must not be null");

		var request = chatClientRequest;
		ChatClientResponse response;

		for (int attempt = 1; attempt <= maxRepeatAttempts + 1; attempt++) {

			response = callAdvisorChain.copy(this).nextCall(request);

			// Early exit - skip evaluation (e.g., tool call response)
			if (this.skipEvaluationPredicate.test(chatClientRequest, response)) {
				logger.debug("[{}] Skipping evaluation (tool call / empty response)", getName());
				return response;
			}

			// Perform LLM-as-a-Judge evaluation
			EvaluationResponse evaluation = this.evaluate(chatClientRequest, response);

			if (evaluation.rating() >= this.successRating) {
				logger.info("[{}] ✅ Passed on attempt {}/{}, rating={}: {}",
						getName(), attempt, maxRepeatAttempts, evaluation.rating(), evaluation.evaluation());
				return response;
			}

			if (attempt > maxRepeatAttempts) {
				logger.warn("[{}] ❌ Max attempts ({}) reached. Last rating={}, feedback: {}",
						getName(), maxRepeatAttempts, evaluation.rating(), evaluation.feedback());
				return response;
			}

			logger.warn("[{}] 🔄 Retry attempt {}/{}, rating={}: {} | feedback: {}",
					getName(), attempt, maxRepeatAttempts, evaluation.rating(), evaluation.evaluation(),
					evaluation.feedback());

			request = this.addEvaluationFeedback(chatClientRequest, evaluation);
		}

		throw new IllegalStateException("Unexpected loop exit in adviseCall");
	}

	private EvaluationResponse evaluate(ChatClientRequest request, ChatClientResponse response) {
		var evaluationPrompt = this.evaluationPromptTemplate.render(
				Map.of("question", this.getPromptQuestion(request),
						"answer", this.getAssistantAnswer(response)));

		return chatClient.prompt(evaluationPrompt).call().entity(EvaluationResponse.class);
	}

	private String getPromptQuestion(ChatClientRequest chatClientRequest) {
		var messages = chatClientRequest.prompt().getInstructions();

		String conversationHistory = messages.stream()
				.filter(m -> m.getMessageType() == MessageType.USER || m.getMessageType() == MessageType.ASSISTANT)
				.map(m -> m.getMessageType() + ":" + m.getText())
				.collect(Collectors.joining(System.lineSeparator()));

		SystemMessage systemMessage = chatClientRequest.prompt().getSystemMessage();
		String systemText = (systemMessage != null) ? systemMessage.getMessageType() + ":" + systemMessage.getText()
				+ System.lineSeparator() : "";

		return systemText + conversationHistory;
	}

	private String getAssistantAnswer(ChatClientResponse chatClientResponse) {
		return chatClientResponse.chatResponse() != null
				&& chatClientResponse.chatResponse().getResult() != null
						? chatClientResponse.chatResponse().getResult().getOutput().getText()
						: "";
	}

	private ChatClientRequest addEvaluationFeedback(ChatClientRequest originalRequest,
			EvaluationResponse evaluationResponse) {

		Prompt augmentedPrompt = originalRequest.prompt()
				.augmentUserMessage(userMessage -> userMessage.mutate()
						.text(String.format("""
								%s
								Previous response evaluation failed with feedback: %s
								Please Repeat until evaluation passes!
								""", userMessage.getText(), evaluationResponse.feedback()))
						.build());

		return originalRequest.mutate().prompt(augmentedPrompt).build();
	}

	@Override
	public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest,
			StreamAdvisorChain streamAdvisorChain) {
		return Flux.error(new UnsupportedOperationException(
				"The SelfRefineEvaluationAdvisor does not support streaming."));
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private int successRating = 3;
		private int advisorOrder = BaseAdvisor.LOWEST_PRECEDENCE - 2000;
		private int maxRepeatAttempts = 3;
		private ChatClient.Builder chatClientBuilder;
		private PromptTemplate promptTemplate = DEFAULT_EVALUATION_PROMPT_TEMPLATE;

		private BiPredicate<ChatClientRequest, ChatClientResponse> skipEvaluationPredicate = (request,
				response) -> response.chatResponse() == null || response.chatResponse().hasToolCalls();

		private Builder() {
		}

		public Builder successRating(int successRating) {
			Assert.isTrue(successRating >= 1 && successRating <= 4, "successRating must be between 1 and 4");
			this.successRating = successRating;
			return this;
		}

		public Builder order(int advisorOrder) {
			Assert.isTrue(advisorOrder > BaseAdvisor.HIGHEST_PRECEDENCE && advisorOrder < BaseAdvisor.LOWEST_PRECEDENCE,
					"advisorOrder must be between HIGHEST_PRECEDENCE and LOWEST_PRECEDENCE");
			this.advisorOrder = advisorOrder;
			return this;
		}

		public Builder chatClientBuilder(ChatClient.Builder chatClientBuilder) {
			Assert.notNull(chatClientBuilder, "chatClientBuilder must not be null");
			this.chatClientBuilder = chatClientBuilder;
			return this;
		}

		public Builder maxRepeatAttempts(int repeatAttempts) {
			Assert.isTrue(repeatAttempts >= 1, "repeatAttempts must be >= 1");
			this.maxRepeatAttempts = repeatAttempts;
			return this;
		}

		public Builder promptTemplate(PromptTemplate promptTemplate) {
			Assert.notNull(promptTemplate, "promptTemplate must not be null");
			this.promptTemplate = promptTemplate;
			return this;
		}

		public Builder skipEvaluationPredicate(
				BiPredicate<ChatClientRequest, ChatClientResponse> skipEvaluationPredicate) {
			Assert.notNull(skipEvaluationPredicate, "skipEvaluationPredicate must not be null");
			this.skipEvaluationPredicate = skipEvaluationPredicate;
			return this;
		}

		public SelfRefineEvaluationAdvisor build() {
			if (this.chatClientBuilder == null) {
				throw new IllegalArgumentException("chatClientBuilder must be set");
			}
			return new SelfRefineEvaluationAdvisor(this.advisorOrder, this.maxRepeatAttempts, this.chatClientBuilder,
					this.promptTemplate, this.successRating, this.skipEvaluationPredicate);
		}
	}
}
