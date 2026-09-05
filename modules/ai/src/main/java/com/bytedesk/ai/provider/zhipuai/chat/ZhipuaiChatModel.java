package com.bytedesk.ai.provider.zhipuai.chat;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatFunction;
import ai.z.openapi.service.model.ChatFunctionCall;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatThinking;
import ai.z.openapi.service.model.Choice;
import ai.z.openapi.service.model.ChatTool;
import ai.z.openapi.service.model.ModelData;
import ai.z.openapi.service.model.ToolCalls;
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
 * {@code gen_ai_client_token_usage_total} 指标。</p>
 *
 * <p>2026-09-04（规划 Phase 5）：补齐 ChatClient 链路所需能力——</p>
 * <ul>
 *   <li>工具调用：{@link ToolCallingManager#resolveToolDefinitions} → zai {@code ChatTool}；
 *       响应 {@code ToolCalls} → Spring AI {@code AssistantMessage.ToolCall}（执行由框架
 *       ToolCallingAdvisor 外部化，同 DashScopeChatModel 模式）</li>
 *   <li>思考模式：runtime options 为 {@link ZhipuaiChatOptions} 且 enableThinking 非 null 时
 *       设置 {@code ChatThinking}(enabled/disabled)</li>
 *   <li>多模态：USER 消息文本经 {@link ZhipuaiContentParser#parseUserContents} 解析
 *       图片/视频/文件/音频 JSON（本地回环 URL 自动转 base64）</li>
 *   <li>reasoning_content：message/delta 的 reasoningContent 写入 AssistantMessage
 *       metadata key {@code reasoningContent}（{@code ReasoningContentHelper} 契约）</li>
 *   <li>TOOL 消息回填：{@link ToolResponseMessage} → role=tool + toolCallId</li>
 * </ul>
 */
public class ZhipuaiChatModel implements ChatModel {

    private static final String DEFAULT_MODEL = "glm-4.5-flash";

    private static final String PROVIDER = "zhipuai";

    private static final ChatModelObservationConvention DEFAULT_OBSERVATION_CONVENTION =
            new DefaultChatModelObservationConvention();

    /** 工具调用管理器缺省值（同 MoonshotChatModel / DashScopeChatModel，仅用于解析工具定义）。 */
    private static final ToolCallingManager DEFAULT_TOOL_CALLING_MANAGER = ToolCallingManager.builder().build();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ZhipuAiClient client;

    private final ChatOptions defaultOptions;

    private final ObservationRegistry observationRegistry;

    private final ToolCallingManager toolCallingManager;

    private ChatModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

    public ZhipuaiChatModel(ZhipuAiClient client, ChatOptions defaultOptions) {
        this(client, defaultOptions, ObservationRegistry.NOOP);
    }

    public ZhipuaiChatModel(ZhipuAiClient client, ChatOptions defaultOptions,
            ObservationRegistry observationRegistry) {
        this(client, defaultOptions, observationRegistry, DEFAULT_TOOL_CALLING_MANAGER);
    }

    public ZhipuaiChatModel(ZhipuAiClient client, ChatOptions defaultOptions,
            ObservationRegistry observationRegistry, ToolCallingManager toolCallingManager) {
        Assert.notNull(observationRegistry, "observationRegistry cannot be null");
        Assert.notNull(toolCallingManager, "toolCallingManager cannot be null");
        this.client = client;
        this.defaultOptions = defaultOptions;
        this.observationRegistry = observationRegistry;
        this.toolCallingManager = toolCallingManager;
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
        ChatCompletionCreateParams.ChatCompletionCreateParamsBuilder<?, ?> builder = ChatCompletionCreateParams.builder()
                .model(StringUtils.hasText(options.getModel()) ? options.getModel() : DEFAULT_MODEL)
                .stream(stream)
                .messages(toMessages(prompt.getInstructions()))
                .temperature(options.getTemperature() != null ? options.getTemperature().floatValue() : null)
                .topP(options.getTopP() != null ? options.getTopP().floatValue() : null)
                .maxTokens(options.getMaxTokens());
        if (options.getStopSequences() != null && !options.getStopSequences().isEmpty()) {
            builder.stop(options.getStopSequences());
        }
        // 思考模式：ZhipuaiChatOptions.enableThinking 三态（null=不设置；true/false=enabled/disabled）
        if (prompt.getOptions() instanceof ZhipuaiChatOptions zhipuaiOptions
                && zhipuaiOptions.getEnableThinking() != null) {
            builder.thinking(ChatThinking.builder()
                    .type(Boolean.TRUE.equals(zhipuaiOptions.getEnableThinking()) ? "enabled" : "disabled")
                    .build());
        }
        // 工具定义：runtime ToolCallingChatOptions → ToolDefinition → zai ChatTool
        List<ToolDefinition> toolDefinitions = resolveToolDefinitions(prompt.getOptions());
        if (!CollectionUtils.isEmpty(toolDefinitions)) {
            builder.tools(toZaiTools(toolDefinitions));
            // 工具选择模式（规划 G8）：auto/none/required 透传 zai toolChoice
            if (prompt.getOptions() instanceof ZhipuaiChatOptions zhipuaiToolOptions
                    && StringUtils.hasText(zhipuaiToolOptions.getToolChoice())) {
                builder.toolChoice(zhipuaiToolOptions.getToolChoice());
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

    /** Spring AI {@link ToolDefinition} → zai-sdk {@link ChatTool}（parameters 为 JSON 字符串，转 JsonNode）。 */
    private List<ChatTool> toZaiTools(List<ToolDefinition> toolDefinitions) {
        List<ChatTool> tools = new ArrayList<>();
        for (ToolDefinition toolDefinition : toolDefinitions) {
            ChatFunction function = ChatFunction.builder()
                    .name(toolDefinition.name())
                    .description(toolDefinition.description())
                    .parameters(parseSchema(toolDefinition.inputSchema()))
                    .build();
            tools.add(ChatTool.builder().type("function").function(function).build());
        }
        return tools;
    }

    private JsonNode parseSchema(String inputSchema) {
        if (!StringUtils.hasText(inputSchema)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(inputSchema);
        } catch (Exception e) {
            return null;
        }
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
            switch (message.getMessageType()) {
                case SYSTEM -> result.add(ChatMessage.builder()
                        .role(ChatMessageRole.SYSTEM.value())
                        .content(List.of(ZhipuaiContentParser.textContent(message.getText())))
                        .build());
                case USER -> result.add(ChatMessage.builder()
                        .role(ChatMessageRole.USER.value())
                        .content((Object) ZhipuaiContentParser.parseUserContents(message.getText()))
                        .build());
                case ASSISTANT -> {
                    ChatMessage.ChatMessageBuilder builder = ChatMessage.builder()
                            .role(ChatMessageRole.ASSISTANT.value())
                            .content(List.of(ZhipuaiContentParser.textContent(message.getText())));
                    if (message instanceof AssistantMessage assistantMessage
                            && !CollectionUtils.isEmpty(assistantMessage.getToolCalls())) {
                        builder.toolCalls(toZaiToolCalls(assistantMessage.getToolCalls()));
                    }
                    result.add(builder.build());
                }
                case TOOL -> {
                    if (message instanceof ToolResponseMessage toolResponseMessage) {
                        for (ToolResponseMessage.ToolResponse toolResponse : toolResponseMessage.getResponses()) {
                            result.add(ChatMessage.builder()
                                    .role("tool")
                                    .toolCallId(toolResponse.id() != null ? toolResponse.id() : "")
                                    .name(toolResponse.name() != null ? toolResponse.name() : "")
                                    .content(toolResponse.responseData() != null
                                            ? String.valueOf(toolResponse.responseData()) : "")
                                    .build());
                        }
                    }
                }
                default -> result.add(ChatMessage.builder()
                        .role(ChatMessageRole.USER.value())
                        .content((Object) ZhipuaiContentParser.parseUserContents(message.getText()))
                        .build());
            }
        }
        return result;
    }

    /** Spring AI {@link AssistantMessage.ToolCall} → zai-sdk {@link ToolCalls}（arguments 转 JsonNode）。 */
    private List<ToolCalls> toZaiToolCalls(List<AssistantMessage.ToolCall> toolCalls) {
        List<ToolCalls> result = new ArrayList<>();
        for (AssistantMessage.ToolCall toolCall : toolCalls) {
            ChatFunctionCall function = ChatFunctionCall.builder()
                    .name(toolCall.name())
                    .arguments(parseSchema(toolCall.arguments() != null ? toolCall.arguments() : "{}"))
                    .build();
            result.add(ToolCalls.builder()
                    .id(toolCall.id() != null ? toolCall.id() : "")
                    .type(toolCall.type() != null ? toolCall.type() : "function")
                    .function(function)
                    .build());
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
        String reasoning = null;
        List<AssistantMessage.ToolCall> toolCalls = List.of();
        if (choice.getMessage() != null) {
            ChatMessage message = choice.getMessage();
            text = ZhipuaiContentParser.extractTextFromContent(message.getContent());
            reasoning = filterLiteralNull(message.getReasoningContent());
            toolCalls = toSpringAiToolCalls(message.getToolCalls());
        } else if (choice.getDelta() != null) {
            text = filterLiteralNull(choice.getDelta().getContent());
            reasoning = filterLiteralNull(choice.getDelta().getReasoningContent());
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
                ChatGenerationMetadata.builder().finishReason(choice.getFinishReason()).build());
    }

    /** 过滤字面量 "null"（部分模型在 reasoning-only chunk 下发字面量 null，沿用 ZhipuaiService 既有守卫）。 */
    private String filterLiteralNull(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        String trimmed = s.trim();
        return "null".equalsIgnoreCase(trimmed) ? null : trimmed;
    }

    /** zai-sdk {@link ToolCalls} → Spring AI {@link AssistantMessage.ToolCall}（arguments 为 JsonNode）。 */
    private List<AssistantMessage.ToolCall> toSpringAiToolCalls(List<ToolCalls> toolCalls) {
        if (CollectionUtils.isEmpty(toolCalls)) {
            return List.of();
        }
        List<AssistantMessage.ToolCall> result = new ArrayList<>();
        for (ToolCalls toolCall : toolCalls) {
            if (toolCall.getFunction() == null) {
                continue;
            }
            String name = toolCall.getFunction().getName();
            String arguments = toolCall.getFunction().getArguments() != null
                    ? toolCall.getFunction().getArguments().toString() : "{}";
            result.add(new AssistantMessage.ToolCall(
                    toolCall.getId() != null ? toolCall.getId() : "",
                    toolCall.getType() != null ? toolCall.getType() : "function",
                    name != null ? name : "",
                    arguments));
        }
        return result;
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