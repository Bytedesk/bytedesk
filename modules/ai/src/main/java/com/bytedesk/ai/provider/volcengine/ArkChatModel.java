package com.bytedesk.ai.provider.volcengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
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
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volcengine.ark.runtime.models.chat.ChatCompletionMessageContent;
import com.volcengine.ark.runtime.models.chat.ChatCompletionMessageToolCall;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequestAssistantMessage;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequestMessage;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequestMessageToolCall;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequestMessageToolCallFunction;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequestMessageType;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequestSystemMessage;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequestToolMessage;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequestUserMessage;
import com.volcengine.ark.runtime.models.chat.ChatCompletionResponse;
import com.volcengine.ark.runtime.models.chat.ChatCompletionResponseChoice;
import com.volcengine.ark.runtime.models.chat.ChatCompletionResponseMessage;
import com.volcengine.ark.runtime.models.chat.ChatCompletionStreamOptions;
import com.volcengine.ark.runtime.models.chat.ChatCompletionStreamResponse;
import com.volcengine.ark.runtime.models.chat.ChatCompletionTool;
import com.volcengine.ark.runtime.models.chat.ChatCompletionToolChoice;
import com.volcengine.ark.runtime.models.chat.CompletionUsage;
import com.volcengine.ark.runtime.models.chat.FunctionObject;
import com.volcengine.ark.runtime.models.chat.Thinking;
import com.volcengine.ark.runtime.models.chat.ThinkingMode;
import com.volcengine.ark.runtime.models.chat.ToolChoiceMode;
import com.volcengine.ark.runtime.models.chat.ToolType;
import com.volcengine.ark.runtime.service.ArkApi;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import io.reactivex.Flowable;
import reactor.core.publisher.Flux;

/**
 * 火山引擎方舟 ChatModel 适配层（ark-runtime 官方 SDK）。
 *
 * <p>2026-11 迁移自 Spring AI OpenAI 兼容客户端（OpenAiChatModel + 方舟 /api/v3 兼容端点），
 * 依据官方迁移指南 https://docs.volcengine.com/docs/ark/migrate-ark-runtime-sdk。</p>
 *
 * <p>注：官方 {@code ArkService} 便捷类在 Jackson 2.21 下因
 * {@code PropertyNamingStrategy.SNAKE_CASE} 移除而无法加载（SDK 已在
 * volcengine-java-sdk 2.0.0 修复，ark-runtime 尚未跟进），故经
 * {@link ArkServiceFactory} 使用官方 {@link ArkApi} + 模型类。</p>
 *
 * <p>内置手动观测（{@link ChatModelObservationDocumentation#CHAT_MODEL_OPERATION}），
 * 使方舟调用产生 {@code gen_ai_client_operation_seconds} 与
 * {@code gen_ai_client_token_usage_total} 指标（同 ZhipuaiChatModel 模式）。</p>
 *
 * <p>能力（对齐 ZhipuaiChatModel / DashScopeChatModel）：</p>
 * <ul>
 *   <li>同步/流式：{@code ArkServiceFactory.createChatCompletion} / {@code streamChatCompletion}
 *       （RxJava2 Flowable → Reactor Flux）</li>
 *   <li>工具调用：{@link ToolCallingManager#resolveToolDefinitions} → 方舟 {@code ChatCompletionTool}；
 *       响应 {@code tool_calls} → Spring AI {@code AssistantMessage.ToolCall}（执行由框架
 *       ToolCallingAdvisor 外部化）</li>
 *   <li>思考模式：runtime options 为 {@link ArkChatOptions} 且 enableThinking 非 null 时
 *       设置 {@code Thinking}(enabled/disabled)；null=不设置（思考模式由模型名决定，
 *       非思考模型设置会导致 400 Bad Request）</li>
 *   <li>reasoning_content：message/delta 的 reasoningContent 写入 AssistantMessage
 *       metadata key {@code reasoningContent}（{@code ReasoningContentHelper} 契约）</li>
 *   <li>TOOL 消息回填：{@link ToolResponseMessage} → role=tool + tool_call_id</li>
 * </ul>
 */
public class ArkChatModel implements ChatModel {

    private static final String DEFAULT_MODEL = "doubao-1-5-pro-32k-250115";

    private static final String PROVIDER = "volcengine";

    private static final ChatModelObservationConvention DEFAULT_OBSERVATION_CONVENTION =
            new DefaultChatModelObservationConvention();

    /** 工具调用管理器缺省值（同 ZhipuaiChatModel / DashScopeChatModel，仅用于解析工具定义）。 */
    private static final ToolCallingManager DEFAULT_TOOL_CALLING_MANAGER = ToolCallingManager.builder().build();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ArkApi arkApi;

    private final ChatOptions defaultOptions;

    private final ObservationRegistry observationRegistry;

    private final ToolCallingManager toolCallingManager;

    public ArkChatModel(ArkApi arkApi, ChatOptions defaultOptions) {
        this(arkApi, defaultOptions, ObservationRegistry.NOOP);
    }

    public ArkChatModel(ArkApi arkApi, ChatOptions defaultOptions,
            ObservationRegistry observationRegistry) {
        this(arkApi, defaultOptions, observationRegistry, DEFAULT_TOOL_CALLING_MANAGER);
    }

    public ArkChatModel(ArkApi arkApi, ChatOptions defaultOptions,
            ObservationRegistry observationRegistry, ToolCallingManager toolCallingManager) {
        Assert.notNull(arkApi, "arkApi cannot be null");
        Assert.notNull(observationRegistry, "observationRegistry cannot be null");
        Assert.notNull(toolCallingManager, "toolCallingManager cannot be null");
        this.arkApi = arkApi;
        this.defaultOptions = defaultOptions != null ? defaultOptions
                : ArkChatOptions.builder().model(DEFAULT_MODEL).build();
        this.observationRegistry = observationRegistry;
        this.toolCallingManager = toolCallingManager;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        ChatModelObservationContext observationContext = ChatModelObservationContext.builder()
                .prompt(prompt)
                .provider(PROVIDER)
                .build();

        return ChatModelObservationDocumentation.CHAT_MODEL_OPERATION
                .observation(DEFAULT_OBSERVATION_CONVENTION, DEFAULT_OBSERVATION_CONVENTION,
                        () -> observationContext, this.observationRegistry)
                .observe(() -> {
                    try {
                        ChatCompletionResponse response = ArkServiceFactory
                                .createChatCompletion(arkApi, createRequest(prompt, false));
                        ChatResponse chatResponse = toChatResponse(response);
                        observationContext.setResponse(chatResponse);
                        return chatResponse;
                    } catch (Exception e) {
                        observationContext.setError(e);
                        throw new IllegalStateException("Volcengine Ark chat call failed", e);
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
                    .observation(DEFAULT_OBSERVATION_CONVENTION, DEFAULT_OBSERVATION_CONVENTION,
                            () -> observationContext, this.observationRegistry)
                    .parentObservation(contextView.getOrDefault(ObservationThreadLocalAccessor.KEY, null))
                    .start();

            try {
                Flowable<ChatCompletionStreamResponse> flowable =
                        ArkServiceFactory.streamChatCompletion(arkApi, createRequest(prompt, true));
                if (flowable == null) {
                    observation.stop();
                    return Flux.empty();
                }
                return Flux.from(flowable)
                        .map(this::toChatResponse)
                        .doOnNext(observationContext::setResponse)
                        .doOnError(observation::error)
                        .doFinally(signal -> observation.stop());
            } catch (Exception e) {
                observation.error(e);
                observation.stop();
                return Flux.error(new IllegalStateException("Volcengine Ark chat stream failed", e));
            }
        });
    }

    @Override
    public ChatOptions getOptions() {
        if (this.defaultOptions instanceof ArkChatOptions arkOptions) {
            return arkOptions.mutate().build();
        }
        return this.defaultOptions;
    }

    private String resolveModel(ArkChatOptions options) {
        return StringUtils.hasText(options.getModel()) ? options.getModel() : resolveDefaultModel();
    }

    private ChatCompletionRequest createRequest(Prompt prompt, boolean stream) {
        ArkChatOptions options = mergeOptions(prompt.getOptions());
        ChatCompletionRequest.Builder builder = ChatCompletionRequest.builder()
                .model(resolveModel(options))
                .messages(toArkMessages(prompt.getInstructions()));
        if (stream) {
            // 流式按 chunk 返回 usage（最后一个 chunk），供 TokenUsageHelper 提取
            builder.stream(true)
                    .streamOptions(ChatCompletionStreamOptions.builder().includeUsage(true).build());
        }
        if (options.getTemperature() != null) {
            builder.temperature(options.getTemperature());
        }
        if (options.getMaxTokens() != null) {
            builder.maxTokens(options.getMaxTokens());
        }
        if (options.getTopP() != null) {
            builder.topP(options.getTopP());
        }
        if (options.getFrequencyPenalty() != null) {
            builder.frequencyPenalty(options.getFrequencyPenalty());
        }
        if (options.getPresencePenalty() != null) {
            builder.presencePenalty(options.getPresencePenalty());
        }
        if (options.getStopSequences() != null && !options.getStopSequences().isEmpty()) {
            builder.stop(com.volcengine.ark.runtime.models.chat.ChatCompletionStop
                    .ofList(new ArrayList<>(options.getStopSequences())));
        }
        // 思考模式：仅显式设置时透传（默认不设置，遵守「思考模式由模型名决定」约束，
        // 非思考模型设置 thinking 会导致 400 Bad Request）
        if (options.getEnableThinking() != null) {
            builder.thinking(Thinking.builder()
                    .type(Boolean.TRUE.equals(options.getEnableThinking())
                            ? ThinkingMode.ENABLED : ThinkingMode.DISABLED)
                    .build());
        }
        // 工具定义：runtime ToolCallingChatOptions → ToolDefinition → 方舟 ChatCompletionTool
        List<ToolDefinition> toolDefinitions = resolveToolDefinitions(prompt.getOptions());
        if (!CollectionUtils.isEmpty(toolDefinitions)) {
            builder.tools(toArkTools(toolDefinitions));
            // 工具选择模式：auto/none/required 透传方舟 tool_choice
            if (StringUtils.hasText(options.getToolChoice())) {
                builder.toolChoice(ChatCompletionToolChoice
                        .ofToolChoiceMode(ToolChoiceMode.fromValue(options.getToolChoice())));
            }
        }
        return builder.build();
    }

    /** 解析工具定义：runtime options 携带的 toolCallbacks/toolNames（默认 options 为泛型 ChatOptions，无工具）。 */
    private List<ToolDefinition> resolveToolDefinitions(ChatOptions runtimeOptions) {
        if (!(runtimeOptions instanceof ToolCallingChatOptions toolCallingOptions)) {
            return List.of();
        }
        ToolCallingChatOptions.validateToolCallbacks(toolCallingOptions.getToolCallbacks());
        List<ToolDefinition> definitions = this.toolCallingManager.resolveToolDefinitions(toolCallingOptions);
        return definitions != null ? definitions : List.of();
    }

    /** Spring AI {@link ToolDefinition} → 方舟 {@link ChatCompletionTool}（inputSchema JSON → Map）。 */
    private List<ChatCompletionTool> toArkTools(List<ToolDefinition> toolDefinitions) {
        List<ChatCompletionTool> tools = new ArrayList<>();
        for (ToolDefinition toolDefinition : toolDefinitions) {
            FunctionObject function = FunctionObject.builder()
                    .name(toolDefinition.name())
                    .description(toolDefinition.description())
                    .parameters(parseSchema(toolDefinition.inputSchema()))
                    .build();
            tools.add(ChatCompletionTool.builder()
                    .type(ToolType.FUNCTION)
                    .function(function)
                    .build());
        }
        return tools;
    }

    private Map<String, Object> parseSchema(String inputSchema) {
        if (!StringUtils.hasText(inputSchema)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(inputSchema, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    private ArkChatOptions mergeOptions(ChatOptions runtimeOptions) {
        ArkChatOptions merged = ArkChatOptions.fromOptions(
                this.defaultOptions instanceof ArkChatOptions arkOptions
                        ? arkOptions
                        : ArkChatOptions.builder().model(this.defaultOptions.getModel()).build());
        if (runtimeOptions == null) {
            ToolCallingChatOptions.validateToolCallbacks(merged.getToolCallbacks());
            return merged;
        }
        if (StringUtils.hasText(runtimeOptions.getModel())) {
            merged.setModel(runtimeOptions.getModel());
        }
        if (runtimeOptions.getMaxTokens() != null) {
            merged.setMaxTokens(runtimeOptions.getMaxTokens());
        }
        if (runtimeOptions.getStopSequences() != null) {
            merged.setStopSequences(new ArrayList<>(runtimeOptions.getStopSequences()));
        }
        if (runtimeOptions.getTemperature() != null) {
            merged.setTemperature(runtimeOptions.getTemperature());
        }
        if (runtimeOptions.getTopP() != null) {
            merged.setTopP(runtimeOptions.getTopP());
        }
        if (runtimeOptions.getFrequencyPenalty() != null) {
            merged.setFrequencyPenalty(runtimeOptions.getFrequencyPenalty());
        }
        if (runtimeOptions.getPresencePenalty() != null) {
            merged.setPresencePenalty(runtimeOptions.getPresencePenalty());
        }
        if (runtimeOptions instanceof ArkChatOptions arkRuntimeOptions) {
            if (arkRuntimeOptions.getEnableThinking() != null) {
                merged.setEnableThinking(arkRuntimeOptions.getEnableThinking());
            }
            if (StringUtils.hasText(arkRuntimeOptions.getToolChoice())) {
                merged.setToolChoice(arkRuntimeOptions.getToolChoice());
            }
            merged.setToolCallbacks(arkRuntimeOptions.getToolCallbacks());
            merged.setToolNames(arkRuntimeOptions.getToolNames());
            merged.setToolContext(arkRuntimeOptions.getToolContext());
        }
        return merged;
    }

    /** Spring AI Message → 方舟 ChatCompletionRequestMessage（联合类型按消息类型分派）。 */
    private List<ChatCompletionRequestMessage> toArkMessages(List<Message> messages) {
        List<ChatCompletionRequestMessage> result = new ArrayList<>();
        for (Message message : messages) {
            switch (message.getMessageType()) {
                case SYSTEM -> result.add(ChatCompletionRequestSystemMessage.builder()
                        .role(ChatCompletionRequestMessageType.SYSTEM)
                        .content(ChatCompletionMessageContent.ofString(message.getText()))
                        .build());
                case USER -> result.add(ChatCompletionRequestUserMessage.builder()
                        .role(ChatCompletionRequestMessageType.USER)
                        .content(toUserContent(message.getText()))
                        .build());
                case ASSISTANT -> {
                    ChatCompletionRequestAssistantMessage.Builder builder =
                            ChatCompletionRequestAssistantMessage.builder()
                                    .role(ChatCompletionRequestMessageType.ASSISTANT)
                                    .content(ChatCompletionMessageContent.ofString(
                                            message.getText() != null ? message.getText() : ""));
                    if (message instanceof AssistantMessage assistantMessage
                            && !CollectionUtils.isEmpty(assistantMessage.getToolCalls())) {
                        builder.toolCalls(toArkToolCalls(assistantMessage.getToolCalls()));
                    }
                    result.add(builder.build());
                }
                case TOOL -> {
                    if (message instanceof ToolResponseMessage toolResponseMessage) {
                        for (ToolResponseMessage.ToolResponse toolResponse : toolResponseMessage.getResponses()) {
                            result.add(ChatCompletionRequestToolMessage.builder()
                                    .role(ChatCompletionRequestMessageType.TOOL)
                                    .content(ChatCompletionMessageContent.ofString(toolResponse.responseData() != null
                                            ? String.valueOf(toolResponse.responseData()) : ""))
                                    .toolCallId(toolResponse.id() != null ? toolResponse.id() : "")
                                    .build());
                        }
                    }
                }
                default -> result.add(ChatCompletionRequestUserMessage.builder()
                        .role(ChatCompletionRequestMessageType.USER)
                        .content(toUserContent(message.getText()))
                        .build());
            }
        }
        return result;
    }

    /**
     * 用户消息内容：方舟支持 string 或 content parts 数组，
     * 此处以纯文本透传（多模态 parts 留待后续按需扩展）。
     */
    private ChatCompletionMessageContent toUserContent(String text) {
        return ChatCompletionMessageContent.ofString(text != null ? text : "");
    }

    /** Spring AI {@link AssistantMessage.ToolCall} → 方舟 {@link ChatCompletionRequestMessageToolCall}。 */
    private List<ChatCompletionRequestMessageToolCall> toArkToolCalls(
            List<AssistantMessage.ToolCall> toolCalls) {
        List<ChatCompletionRequestMessageToolCall> result = new ArrayList<>();
        for (AssistantMessage.ToolCall toolCall : toolCalls) {
            result.add(ChatCompletionRequestMessageToolCall.builder()
                    .id(toolCall.id() != null ? toolCall.id() : "")
                    .type(ToolType.FUNCTION)
                    .function(ChatCompletionRequestMessageToolCallFunction.builder()
                            .name(toolCall.name())
                            .arguments(toolCall.arguments() != null ? toolCall.arguments() : "{}")
                            .build())
                    .build());
        }
        return result;
    }

    private ChatResponse toChatResponse(ChatCompletionResponse response) {
        if (response == null || CollectionUtils.isEmpty(response.getChoices())) {
            return new ChatResponse(List.of(),
                    ChatResponseMetadata.builder().usage(new DefaultUsage(0, 0, 0)).build());
        }
        List<Generation> generations = response.getChoices().stream()
                .map(this::toGeneration)
                .toList();
        return new ChatResponse(generations, toMetadata(response.getId(), response.getModel(),
                response.getUsage()));
    }

    private ChatResponse toChatResponse(ChatCompletionStreamResponse response) {
        if (response == null || CollectionUtils.isEmpty(response.getChoices())) {
            // usage-only chunk（流末尾）：返回带 usage 的空结果
            if (response != null && response.getUsage() != null) {
                return new ChatResponse(List.of(), toMetadata(response.getId(), response.getModel(),
                        response.getUsage()));
            }
            return new ChatResponse(List.of(),
                    ChatResponseMetadata.builder().usage(new DefaultUsage(0, 0, 0)).build());
        }
        List<Generation> generations = response.getChoices().stream()
                .map(this::toStreamGeneration)
                .toList();
        return new ChatResponse(generations, toMetadata(response.getId(), response.getModel(),
                response.getUsage()));
    }

    private Generation toGeneration(ChatCompletionResponseChoice choice) {
        String text = "";
        String reasoning = null;
        List<AssistantMessage.ToolCall> toolCalls = List.of();
        if (choice.getMessage() != null) {
            ChatCompletionResponseMessage message = choice.getMessage();
            text = filterLiteralNull(message.getContent());
            reasoning = filterLiteralNull(message.getReasoningContent());
            toolCalls = toSpringAiToolCalls(message.getToolCalls());
        }
        AssistantMessage.Builder<?> builder = AssistantMessage.builder()
                .content(text != null ? text : "")
                .toolCalls(toolCalls)
                .media(List.of());
        if (StringUtils.hasText(reasoning)) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("reasoningContent", reasoning);
            builder.properties(metadata);
        }
        return new Generation(builder.build(),
                ChatGenerationMetadata.builder().finishReason(toFinishReason(choice.getFinishReason())).build());
    }

    private Generation toStreamGeneration(
            com.volcengine.ark.runtime.models.chat.ChatCompletionStreamResponseChoice choice) {
        String text = "";
        String reasoning = null;
        if (choice.getDelta() != null) {
            text = filterLiteralNull(choice.getDelta().getContent());
            reasoning = filterLiteralNull(choice.getDelta().getReasoningContent());
        }
        AssistantMessage.Builder<?> builder = AssistantMessage.builder()
                .content(text != null ? text : "")
                .toolCalls(List.of())
                .media(List.of());
        if (StringUtils.hasText(reasoning)) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("reasoningContent", reasoning);
            builder.properties(metadata);
        }
        return new Generation(builder.build(),
                ChatGenerationMetadata.builder().finishReason(toFinishReason(choice.getFinishReason())).build());
    }

    /** 过滤字面量 "null"（部分模型在 reasoning-only chunk 下发字面量 null，沿用既有守卫）。 */
    private String filterLiteralNull(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        String trimmed = s.trim();
        return "null".equalsIgnoreCase(trimmed) ? null : trimmed;
    }

    /** 方舟 FinishReason 枚举 → Spring AI finishReason 字符串。 */
    private String toFinishReason(
            com.volcengine.ark.runtime.models.chat.FinishReason finishReason) {
        return finishReason != null ? finishReason.getValue() : null;
    }

    /** 方舟 {@link ChatCompletionMessageToolCall} → Spring AI {@link AssistantMessage.ToolCall}。 */
    private List<AssistantMessage.ToolCall> toSpringAiToolCalls(
            List<ChatCompletionMessageToolCall> toolCalls) {
        if (CollectionUtils.isEmpty(toolCalls)) {
            return List.of();
        }
        List<AssistantMessage.ToolCall> result = new ArrayList<>();
        for (ChatCompletionMessageToolCall toolCall : toolCalls) {
            String type = toolCall.getType() != null ? toolCall.getType().getValue() : "function";
            result.add(new AssistantMessage.ToolCall(
                    toolCall.getId() != null ? toolCall.getId() : "",
                    type,
                    toolCall.getFunction().getName() != null ? toolCall.getFunction().getName() : "",
                    toolCall.getFunction().getArguments() != null
                            ? toolCall.getFunction().getArguments() : "{}"));
        }
        return result;
    }

    private ChatResponseMetadata toMetadata(String id, String model, CompletionUsage usage) {
        org.springframework.ai.chat.metadata.Usage springUsage = toUsage(usage);
        return ChatResponseMetadata.builder()
                .id(id != null ? id : "")
                .model(model != null ? model : resolveDefaultModel())
                .usage(springUsage)
                .keyValue("prompt_tokens", springUsage.getPromptTokens())
                .keyValue("completion_tokens", springUsage.getCompletionTokens())
                .keyValue("total_tokens", springUsage.getTotalTokens())
                .build();
    }

    private org.springframework.ai.chat.metadata.Usage toUsage(CompletionUsage usage) {
        if (usage == null) {
            return new DefaultUsage(0, 0, 0);
        }
        return new DefaultUsage(
                usage.getPromptTokens() != null ? usage.getPromptTokens() : 0,
                usage.getCompletionTokens() != null ? usage.getCompletionTokens() : 0,
                usage.getTotalTokens() != null ? usage.getTotalTokens() : 0);
    }

    private String resolveDefaultModel() {
        return StringUtils.hasText(this.defaultOptions.getModel()) ? this.defaultOptions.getModel() : DEFAULT_MODEL;
    }
}
