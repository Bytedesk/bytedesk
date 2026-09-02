package com.bytedesk.ai.provider.zhipuai.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.observation.ChatModelObservationContext;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.chat.observation.ChatModelObservationDocumentation;
import org.springframework.ai.chat.observation.DefaultChatModelObservationConvention;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.Choice;
import ai.z.openapi.service.model.ModelData;
import ai.z.openapi.service.model.Usage;
import reactor.core.publisher.Flux;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;

/**
 * 智谱 AI ChatModel 适配层。
 *
 * <p>内置手动观测（{@link ChatModelObservationDocumentation#CHAT_MODEL_OPERATION}），
 * 使 zhipuai 调用产生 {@code gen_ai_client_operation_seconds} 与
 * {@code gen_ai_client_token_usage_total} 指标，模式与 {@code MoonshotChatModel}、
 * {@code DashScopeEmbeddingModel} 保持一致。</p>
 */
public class ZhipuaiChatModel implements ChatModel {

    private static final String DEFAULT_MODEL = "glm-4.5-flash";

    private static final String PROVIDER = "zhipuai";

    private static final ChatModelObservationConvention DEFAULT_OBSERVATION_CONVENTION =
            new DefaultChatModelObservationConvention();

    private final ZhipuAiClient client;

    private final ChatOptions defaultOptions;

    private final ObservationRegistry observationRegistry;

    private ChatModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

    public ZhipuaiChatModel(ZhipuAiClient client, ChatOptions defaultOptions) {
        this(client, defaultOptions, ObservationRegistry.NOOP);
    }

    public ZhipuaiChatModel(ZhipuAiClient client, ChatOptions defaultOptions,
            ObservationRegistry observationRegistry) {
        Assert.notNull(observationRegistry, "observationRegistry cannot be null");
        this.client = client;
        this.defaultOptions = defaultOptions;
        this.observationRegistry = observationRegistry;
    }

    public void setObservationConvention(ChatModelObservationConvention observationConvention) {
        this.observationConvention = observationConvention;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        ChatModelObservationContext observationContext = ChatModelObservationContext.builder()
                .prompt(prompt)
                .provider(PROVIDER)
                .build();

        return ChatModelObservationDocumentation.CHAT_MODEL_OPERATION
                .observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION,
                        () -> observationContext, this.observationRegistry)
                .observe(() -> {
                    try {
                        ChatCompletionResponse response = client.chat().createChatCompletion(createRequest(prompt, false));
                        ChatResponse chatResponse = toChatResponse(response != null ? response.getData() : null);
                        observationContext.setResponse(chatResponse);
                        return chatResponse;
                    } catch (Exception e) {
                        observationContext.setError(e);
                        throw new IllegalStateException("ZhipuAI chat call failed", e);
                    }
                });
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        return Flux.deferContextual(contextView -> {
            ChatModelObservationContext observationContext = ChatModelObservationContext.builder()
                    .prompt(prompt)
                    .provider(PROVIDER)
                    .build();

            Observation observation = ChatModelObservationDocumentation.CHAT_MODEL_OPERATION
                    .observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION,
                            () -> observationContext, this.observationRegistry)
                    .parentObservation(contextView.getOrDefault(ObservationThreadLocalAccessor.KEY, null))
                    .start();

            try {
                ChatCompletionResponse response = client.chat().createChatCompletion(createRequest(prompt, true));
                if (response == null || response.getFlowable() == null) {
                    observation.stop();
                    return Flux.empty();
                }
                return Flux.from(response.getFlowable())
                        .map(this::toChatResponse)
                        .doOnNext(observationContext::setResponse)
                        .doOnError(observation::error)
                        .doFinally(signal -> observation.stop());
            } catch (Exception e) {
                observation.error(e);
                observation.stop();
                return Flux.error(new IllegalStateException("ZhipuAI chat stream failed", e));
            }
        });
    }

    @Override
    public ChatOptions getOptions() {
        return this.defaultOptions;
    }

    private ChatCompletionCreateParams createRequest(Prompt prompt, boolean stream) {
        ChatOptions options = mergeOptions(prompt.getOptions());
        return ChatCompletionCreateParams.builder()
                .model(StringUtils.hasText(options.getModel()) ? options.getModel() : DEFAULT_MODEL)
                .stream(stream)
                .messages(toMessages(prompt.getInstructions()))
                .temperature(options.getTemperature() != null ? options.getTemperature().floatValue() : null)
                .topP(options.getTopP() != null ? options.getTopP().floatValue() : null)
                .maxTokens(options.getMaxTokens())
                .stop(options.getStopSequences())
                .build();
    }

    private ChatOptions mergeOptions(ChatOptions runtimeOptions) {
        if (runtimeOptions == null) {
            return this.defaultOptions;
        }
        return ChatOptions.builder()
                .model(StringUtils.hasText(runtimeOptions.getModel()) ? runtimeOptions.getModel() : this.defaultOptions.getModel())
                .temperature(runtimeOptions.getTemperature() != null ? runtimeOptions.getTemperature() : this.defaultOptions.getTemperature())
                .topP(runtimeOptions.getTopP() != null ? runtimeOptions.getTopP() : this.defaultOptions.getTopP())
                .maxTokens(runtimeOptions.getMaxTokens() != null ? runtimeOptions.getMaxTokens() : this.defaultOptions.getMaxTokens())
                .stopSequences(runtimeOptions.getStopSequences() != null ? runtimeOptions.getStopSequences() : this.defaultOptions.getStopSequences())
                .build();
    }

    private List<ChatMessage> toMessages(List<Message> messages) {
        List<ChatMessage> result = new ArrayList<>();
        for (Message message : messages) {
            String role = ChatMessageRole.USER.value();
            switch (message.getMessageType()) {
                case SYSTEM -> role = ChatMessageRole.SYSTEM.value();
                case ASSISTANT -> role = ChatMessageRole.ASSISTANT.value();
                default -> role = ChatMessageRole.USER.value();
            }
            result.add(new ChatMessage(role, message.getText()));
        }
        return result;
    }

    private ChatResponse toChatResponse(ModelData data) {
        if (data == null || data.getChoices() == null || data.getChoices().isEmpty()) {
            return new ChatResponse(List.of(), ChatResponseMetadata.builder().usage(new DefaultUsage(0, 0, 0)).build());
        }
        List<Generation> generations = data.getChoices().stream()
                .map(this::toGeneration)
                .toList();
        return new ChatResponse(generations, toMetadata(data));
    }

    private Generation toGeneration(Choice choice) {
        String text = "";
        if (choice.getMessage() != null && choice.getMessage().getContent() != null) {
            text = String.valueOf(choice.getMessage().getContent());
        } else if (choice.getDelta() != null && choice.getDelta().getContent() != null) {
            text = choice.getDelta().getContent();
        }
        return new Generation(new AssistantMessage(text),
                ChatGenerationMetadata.builder().finishReason(choice.getFinishReason()).build());
    }

    private ChatResponseMetadata toMetadata(ModelData data) {
        org.springframework.ai.chat.metadata.Usage usage = toUsage(data.getUsage());
        return ChatResponseMetadata.builder()
                .id(data.getRequestId() != null ? data.getRequestId() : "")
                .model(data.getModel() != null ? data.getModel() : resolveDefaultModel())
                .usage(usage)
                .keyValue("prompt_tokens", usage.getPromptTokens())
                .keyValue("completion_tokens", usage.getCompletionTokens())
                .keyValue("total_tokens", usage.getTotalTokens())
                .build();
    }

    private org.springframework.ai.chat.metadata.Usage toUsage(Usage usage) {
        if (usage == null) {
            return new DefaultUsage(0, 0, 0);
        }
        return new DefaultUsage(usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
    }

    private String resolveDefaultModel() {
        return StringUtils.hasText(this.defaultOptions.getModel()) ? this.defaultOptions.getModel() : DEFAULT_MODEL;
    }
}