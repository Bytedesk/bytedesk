/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bytedesk.ai.springai.providers.moonshot.api;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.content.Media;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.metadata.*;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.*;
import org.springframework.ai.chat.observation.ChatModelObservationContext;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.chat.observation.ChatModelObservationDocumentation;
import org.springframework.ai.chat.observation.DefaultChatModelObservationConvention;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.tool.*;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.support.UsageCalculator;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.bytedesk.ai.springai.providers.moonshot.api.MoonshotApi.*;
import com.bytedesk.ai.springai.providers.moonshot.api.MoonshotApi.ChatCompletion.Choice;
import com.bytedesk.ai.springai.providers.moonshot.api.MoonshotApi.ChatCompletionMessage.ChatCompletionFunction;
import com.bytedesk.ai.springai.providers.moonshot.api.MoonshotApi.ChatCompletionMessage.ToolCall;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.bytedesk.ai.springai.providers.moonshot.api.MoonshotConstants.MOONSHOT_PROVIDER_NAME;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MoonshotChatModel is a {@link ChatModel} implementation that uses the Moonshot
 *
 * @author Geng Rong
 * @author Alexandros Pappas
 * @author Ilayaperumal Gopinathan
 */
public class MoonshotChatModel implements ChatModel {

	private static final Logger logger = LoggerFactory.getLogger(MoonshotChatModel.class);

	private static final ChatModelObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DefaultChatModelObservationConvention();

	private static final ToolCallingManager DEFAULT_TOOL_CALLING_MANAGER = ToolCallingManager.builder().build();

	/**
	 * The default options used for the chat completion requests.
	 */
	private final MoonshotChatOptions defaultOptions;

	/**
	 * Low-level access to the Moonshot API.
	 */
	private final MoonshotApi moonshotApi;

	private final RetryTemplate retryTemplate;

	/**
	 * Observation registry used for instrumentation.
	 */
	private final ObservationRegistry observationRegistry;

	private final ToolCallingManager toolCallingManager;

	/**
	 * Conventions to use for generating observations.
	 */
	private ChatModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

	public MoonshotChatModel(MoonshotApi moonshotApi, MoonshotChatOptions defaultOptions,
			ToolCallingManager toolCallingManager, RetryTemplate retryTemplate,
			ObservationRegistry observationRegistry) {
		Assert.notNull(moonshotApi, "moonshotApi cannot be null");
		Assert.notNull(defaultOptions, "defaultOptions cannot be null");
		Assert.notNull(toolCallingManager, "toolCallingManager cannot be null");
		Assert.notNull(retryTemplate, "retryTemplate cannot be null");
		Assert.notNull(observationRegistry, "observationRegistry cannot be null");
		this.moonshotApi = moonshotApi;
		this.defaultOptions = defaultOptions;
		this.toolCallingManager = toolCallingManager;
		this.retryTemplate = retryTemplate;
		this.observationRegistry = observationRegistry;
	}

	private static Generation buildGeneration(Choice choice, Map<String, Object> metadata) {
		List<AssistantMessage.ToolCall> toolCalls = choice.message().toolCalls() == null ? List.of()
				: choice.message()
					.toolCalls()
					.stream()
					.map(toolCall -> new AssistantMessage.ToolCall(toolCall.id(), "function",
							toolCall.function().name(), toolCall.function().arguments()))
					.toList();

		var assistantMessage = AssistantMessage.builder()
			.content(choice.message().content())
			.properties(metadata)
			.toolCalls(toolCalls)
			.media(List.<Media>of())
			.build();
		String finishReason = (choice.finishReason() != null ? choice.finishReason().name() : "");
		var generationMetadata = ChatGenerationMetadata.builder().finishReason(finishReason).build();
		return new Generation(assistantMessage, generationMetadata);
	}

	@Override
	public ChatResponse call(Prompt prompt) {
		Prompt requestPrompt = buildRequestPrompt(prompt);
		return this.internalCall(requestPrompt, null);
	}

	public ChatResponse internalCall(Prompt prompt, ChatResponse previousChatResponse) {

		ChatCompletionRequest request = createRequest(prompt, false);

		ChatModelObservationContext observationContext = ChatModelObservationContext.builder()
			.prompt(prompt)
			.provider(MOONSHOT_PROVIDER_NAME)
			.build();

		ChatResponse response = ChatModelObservationDocumentation.CHAT_MODEL_OPERATION
			.observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
					this.observationRegistry)
			.observe(() -> {

				ResponseEntity<ChatCompletion> completionEntity = this.retryTemplate
					.invoke(() -> this.moonshotApi.chatCompletionEntity(request));

				var chatCompletion = completionEntity.getBody();

				if (chatCompletion == null) {
					logger.warn("No chat completion returned for prompt: {}", prompt);
					return new ChatResponse(List.of());
				}

				List<Choice> choices = chatCompletion.choices();
				if (choices == null) {
					logger.warn("No choices returned for prompt: {}", prompt);
					return new ChatResponse(List.of());
				}

				List<Generation> generations = choices.stream().map(choice -> {
			// @formatter:off
						Map<String, Object> metadata = Map.of(
								"id", chatCompletion.id() != null ? chatCompletion.id() : "",
								"role", choice.message().role() != null ? choice.message().role().name() : "",
								"index", choice.index(),
								"finishReason", choice.finishReason() != null ? choice.finishReason().name() : "");
						// @formatter:on
					return buildGeneration(choice, metadata);
				}).toList();

				// Current usage
				MoonshotApi.Usage usage = completionEntity.getBody().usage();
				Usage currentChatResponseUsage = usage != null ? getDefaultUsage(usage) : new EmptyUsage();
				Usage accumulatedUsage = UsageCalculator.getCumulativeUsage(currentChatResponseUsage,
						previousChatResponse);
				ChatResponse chatResponse = new ChatResponse(generations,
						from(completionEntity.getBody(), accumulatedUsage));

				observationContext.setResponse(chatResponse);

				return chatResponse;

			});

		return response;
	}

	private DefaultUsage getDefaultUsage(MoonshotApi.Usage usage) {
		return new DefaultUsage(usage.promptTokens(), usage.completionTokens(), usage.totalTokens(), usage);
	}

	@Override
	public ChatOptions getOptions() {
		return MoonshotChatOptions.fromOptions(this.defaultOptions);
	}

	@Override
	public Flux<ChatResponse> stream(Prompt prompt) {
		Prompt requestPrompt = buildRequestPrompt(prompt);
		return internalStream(requestPrompt, null);
	}

	public Flux<ChatResponse> internalStream(Prompt prompt, ChatResponse previousChatResponse) {
		return Flux.deferContextual(contextView -> {
			ChatCompletionRequest request = createRequest(prompt, true);

			Flux<ChatCompletionChunk> completionChunks = this.moonshotApi.chatCompletionStream(request);

			// For chunked responses, only the first chunk contains the choice role.
			// The rest of the chunks with same ID share the same role.
			ConcurrentHashMap<String, String> roleMap = new ConcurrentHashMap<>();

			final ChatModelObservationContext observationContext = ChatModelObservationContext.builder()
				.prompt(prompt)
				.provider(MOONSHOT_PROVIDER_NAME)
				.build();

			Observation observation = ChatModelObservationDocumentation.CHAT_MODEL_OPERATION.observation(
					this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
					this.observationRegistry);

			observation.parentObservation(contextView.getOrDefault(ObservationThreadLocalAccessor.KEY, null)).start();

			Flux<ChatResponse> chatResponse = completionChunks.map(this::chunkToChatCompletion)
				.switchMap(chatCompletion -> Mono.just(chatCompletion).map(chatCompletion2 -> {
					try {
						String id = chatCompletion2.id();

						List<Generation> generations = chatCompletion2.choices().stream().map(choice -> {
							if (choice.message().role() != null) {
								roleMap.putIfAbsent(id, choice.message().role().name());
							}

				// @formatter:off
								Map<String, Object> metadata = Map.of(
										"id", chatCompletion2.id(),
										"role", roleMap.getOrDefault(id, ""),
										"finishReason", choice.finishReason() != null ? choice.finishReason().name() : ""
								);
								// @formatter:on
							return buildGeneration(choice, metadata);
						}).toList();
						MoonshotApi.Usage usage = chatCompletion2.usage();
						Usage currentUsage = (usage != null) ? getDefaultUsage(usage) : new EmptyUsage();
						Usage cumulativeUsage = UsageCalculator.getCumulativeUsage(currentUsage, previousChatResponse);

						return new ChatResponse(generations, from(chatCompletion2, cumulativeUsage));
					}
					catch (Exception e) {
						logger.error("Error processing chat completion", e);
						return new ChatResponse(List.of());
					}

				}));

			// @formatter:off
			Flux<ChatResponse> flux = chatResponse
					.doOnError(observation::error)
					.doFinally(s -> observation.stop())
					.contextWrite(ctx -> ctx.put(ObservationThreadLocalAccessor.KEY, observation));
			// @formatter:on

			return new MessageAggregator().aggregate(flux, observationContext::setResponse);

		});
	}

	// private ChatResponseMetadata from(ChatCompletion result) {
	// 	Assert.notNull(result, "Moonshot ChatCompletionResult must not be null");
	// 	return ChatResponseMetadata.builder()
	// 		.id(result.id() != null ? result.id() : "")
	// 		.usage(result.usage() != null ? getDefaultUsage(result.usage()) : new EmptyUsage())
	// 		.model(result.model() != null ? result.model() : "")
	// 		.keyValue("created", result.created() != null ? result.created() : 0L)
	// 		.build();
	// }

	private ChatResponseMetadata from(ChatCompletion result, Usage usage) {
		Assert.notNull(result, "Moonshot ChatCompletionResult must not be null");
		return ChatResponseMetadata.builder()
			.id(result.id() != null ? result.id() : "")
			.usage(usage)
			.model(result.model() != null ? result.model() : "")
			.keyValue("created", result.created() != null ? result.created() : 0L)
			.build();
	}

	/**
	 * Convert the ChatCompletionChunk into a ChatCompletion. The Usage is set to null.
	 * @param chunk the ChatCompletionChunk to convert
	 * @return the ChatCompletion
	 */
	private ChatCompletion chunkToChatCompletion(ChatCompletionChunk chunk) {
		List<ChatCompletion.Choice> choices = chunk.choices().stream().map(cc -> {
			ChatCompletionMessage delta = cc.delta();
			if (delta == null) {
				delta = new ChatCompletionMessage("", ChatCompletionMessage.Role.ASSISTANT);
			}
			return new ChatCompletion.Choice(cc.finishReason(), cc.index(), delta, cc.usage());
		}).toList();
		// Get the usage from the latest choice
		MoonshotApi.Usage usage = choices.get(choices.size() - 1).usage();
		return new ChatCompletion(chunk.id(), choices, chunk.created(), chunk.model(), "chat.completion", usage);
	}

	Prompt buildRequestPrompt(Prompt prompt) {
		MoonshotChatOptions requestOptions = MoonshotChatOptions.fromOptions(this.defaultOptions);

		if (prompt.getOptions() != null) {
			ChatOptions runtimeOptions = prompt.getOptions();
			requestOptions.setModel(ModelOptionsUtils.mergeOption(runtimeOptions.getModel(), requestOptions.getModel()));
			requestOptions.setFrequencyPenalty(ModelOptionsUtils.mergeOption(runtimeOptions.getFrequencyPenalty(),
					requestOptions.getFrequencyPenalty()));
			requestOptions.setMaxTokens(ModelOptionsUtils.mergeOption(runtimeOptions.getMaxTokens(),
					requestOptions.getMaxTokens()));
			requestOptions.setPresencePenalty(ModelOptionsUtils.mergeOption(runtimeOptions.getPresencePenalty(),
					requestOptions.getPresencePenalty()));
			requestOptions.setStop(ModelOptionsUtils.mergeOption(runtimeOptions.getStopSequences(), requestOptions.getStop()));
			requestOptions.setTemperature(ModelOptionsUtils.mergeOption(runtimeOptions.getTemperature(),
					requestOptions.getTemperature()));
			requestOptions.setTopP(ModelOptionsUtils.mergeOption(runtimeOptions.getTopP(), requestOptions.getTopP()));

			if (runtimeOptions instanceof ToolCallingChatOptions toolCallingChatOptions) {
				requestOptions.setToolCallbacks(ToolCallingChatOptions.mergeToolCallbacks(
						toolCallingChatOptions.getToolCallbacks(), requestOptions.getToolCallbacks()));
				requestOptions.setToolContext(ToolCallingChatOptions.mergeToolContext(
						toolCallingChatOptions.getToolContext(), requestOptions.getToolContext()));
			}

			if (runtimeOptions instanceof MoonshotChatOptions moonshotRuntimeOptions) {
				requestOptions.setMaxCompletionTokens(ModelOptionsUtils.mergeOption(
						moonshotRuntimeOptions.getMaxCompletionTokens(), requestOptions.getMaxCompletionTokens()));
				requestOptions.setN(ModelOptionsUtils.mergeOption(moonshotRuntimeOptions.getN(), requestOptions.getN()));
				requestOptions.setTools(ModelOptionsUtils.mergeOption(moonshotRuntimeOptions.getTools(), requestOptions.getTools()));
				requestOptions.setToolChoice(ModelOptionsUtils.mergeOption(moonshotRuntimeOptions.getToolChoice(),
						requestOptions.getToolChoice()));
				requestOptions.setUser(ModelOptionsUtils.mergeOption(moonshotRuntimeOptions.getUser(), requestOptions.getUser()));
				requestOptions.setThinking(ModelOptionsUtils.mergeOption(moonshotRuntimeOptions.getThinking(),
						requestOptions.getThinking()));
				requestOptions.setToolNames(mergeToolNames(moonshotRuntimeOptions.getToolNames(), requestOptions.getToolNames()));
			}
		}

		ToolCallingChatOptions.validateToolCallbacks(requestOptions.getToolCallbacks());

		return new Prompt(prompt.getInstructions(), requestOptions);
	}

	private Set<String> mergeToolNames(Set<String> runtimeToolNames, Set<String> defaultToolNames) {
		if (CollectionUtils.isEmpty(runtimeToolNames)) {
			return defaultToolNames != null ? Set.copyOf(defaultToolNames) : Set.of();
		}
		return Set.copyOf(runtimeToolNames);
	}

	/**
	 * Accessible for testing.
	 */
	ChatCompletionRequest createRequest(Prompt prompt, boolean stream) {
		List<ChatCompletionMessage> chatCompletionMessages = prompt.getInstructions().stream().map(message -> {
			if (message.getMessageType() == MessageType.USER || message.getMessageType() == MessageType.SYSTEM) {
				return List.of(new ChatCompletionMessage(message.getText(),
						ChatCompletionMessage.Role.valueOf(message.getMessageType().name())));
			}
			else if (message.getMessageType() == MessageType.ASSISTANT) {
				var assistantMessage = (AssistantMessage) message;
				List<ToolCall> toolCalls = null;
				if (!CollectionUtils.isEmpty(assistantMessage.getToolCalls())) {
					toolCalls = assistantMessage.getToolCalls().stream().map(toolCall -> {
						var function = new ChatCompletionFunction(toolCall.name(), toolCall.arguments());
						return new ToolCall(toolCall.id(), toolCall.type(), function);
					}).toList();
				}
				return List.of(new ChatCompletionMessage(assistantMessage.getText(),
						ChatCompletionMessage.Role.ASSISTANT, null, null, toolCalls));
			}
			else if (message.getMessageType() == MessageType.TOOL) {
				ToolResponseMessage toolMessage = (ToolResponseMessage) message;

				toolMessage.getResponses()
					.forEach(response -> Assert.isTrue(response.id() != null, "ToolResponseMessage must have an id"));
				return toolMessage.getResponses()
					.stream()
					.map(tr -> new ChatCompletionMessage(tr.responseData(), ChatCompletionMessage.Role.TOOL, tr.name(),
							tr.id(), null))
					.toList();
			}
			else {
				throw new IllegalArgumentException("Unsupported message type: " + message.getMessageType());
			}
		}).flatMap(List::stream).toList();

		MoonshotChatOptions requestOptions = (MoonshotChatOptions) prompt.getOptions();
		ChatCompletionRequest request = new ChatCompletionRequest(chatCompletionMessages, requestOptions.getModel(),
				requestOptions.getMaxTokens(), requestOptions.getMaxCompletionTokens(), requestOptions.getTemperature(),
				requestOptions.getTopP(), requestOptions.getN(), requestOptions.getFrequencyPenalty(),
				requestOptions.getPresencePenalty(), requestOptions.getStop(), stream, requestOptions.getTools(),
				requestOptions.getToolChoice(), requestOptions.getThinking());

		// Add the tool definitions to the request's tools parameter.
		List<ToolDefinition> toolDefinitions = this.toolCallingManager.resolveToolDefinitions(requestOptions);
		if (!CollectionUtils.isEmpty(toolDefinitions)) {
			request = new ChatCompletionRequest(request.messages(), request.model(), request.maxTokens(),
					request.maxCompletionTokens(), request.temperature(), request.topP(), request.n(),
					request.frequencyPenalty(), request.presencePenalty(), request.stop(), request.stream(),
					this.getFunctionTools(toolDefinitions), request.toolChoice(), request.thinking());
		}

		return request;
	}

	// private ChatOptions buildRequestOptions(MoonshotApi.ChatCompletionRequest request) {
	// 	return ChatOptions.builder()
	// 		.model(request.model())
	// 		.frequencyPenalty(request.frequencyPenalty())
	// 		.maxTokens(request.maxCompletionTokens() != null ? request.maxCompletionTokens() : request.maxTokens())
	// 		.presencePenalty(request.presencePenalty())
	// 		.stopSequences(request.stop())
	// 		.temperature(request.temperature())
	// 		.topP(request.topP())
	// 		.build();
	// }

	private List<FunctionTool> getFunctionTools(List<ToolDefinition> toolDefinitions) {
		return toolDefinitions.stream().map(toolDefinition -> {
			var function = new FunctionTool.Function(toolDefinition.description(), toolDefinition.name(),
					toolDefinition.inputSchema());
			return new FunctionTool(function);
		}).toList();
	}

	public void setObservationConvention(ChatModelObservationConvention observationConvention) {
		this.observationConvention = observationConvention;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private MoonshotApi moonshotApi;

		private MoonshotChatOptions defaultOptions = MoonshotChatOptions.builder()
			.model(MoonshotApi.DEFAULT_CHAT_MODEL)
			.temperature(0.7)
			.build();

		private ToolCallingManager toolCallingManager;

		private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

		private ObservationRegistry observationRegistry = ObservationRegistry.NOOP;

		private Builder() {
		}

		public Builder moonshotApi(MoonshotApi moonshotApi) {
			this.moonshotApi = moonshotApi;
			return this;
		}

		public Builder defaultOptions(MoonshotChatOptions defaultOptions) {
			this.defaultOptions = defaultOptions;
			return this;
		}

		public Builder toolCallingManager(ToolCallingManager toolCallingManager) {
			this.toolCallingManager = toolCallingManager;
			return this;
		}

		public Builder retryTemplate(RetryTemplate retryTemplate) {
			this.retryTemplate = retryTemplate;
			return this;
		}

		public Builder observationRegistry(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
			return this;
		}

		public MoonshotChatModel build() {
			if (this.toolCallingManager != null) {
				return new MoonshotChatModel(this.moonshotApi, this.defaultOptions, this.toolCallingManager,
						this.retryTemplate, this.observationRegistry);
			}
			return new MoonshotChatModel(this.moonshotApi, this.defaultOptions, DEFAULT_TOOL_CALLING_MANAGER,
					this.retryTemplate, this.observationRegistry);
		}

	}

}
