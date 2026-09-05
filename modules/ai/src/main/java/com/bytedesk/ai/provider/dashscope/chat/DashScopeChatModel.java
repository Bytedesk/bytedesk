package com.bytedesk.ai.provider.dashscope.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.chat.metadata.Usage;
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

import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.GenerationUsage;
import com.alibaba.dashscope.tools.FunctionDefinition;
import com.alibaba.dashscope.tools.ToolBase;
import com.alibaba.dashscope.tools.ToolCallBase;
import com.alibaba.dashscope.tools.ToolCallFunction;
import com.alibaba.dashscope.tools.ToolFunction;
import com.bytedesk.ai.provider.dashscope.DashScopeBaseUrlSupport;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import reactor.core.publisher.Flux;

/**
 * DashScope ChatModel 适配层。
 *
 * <p>内置手动观测（{@link ChatModelObservationDocumentation#CHAT_MODEL_OPERATION}），
 * 使 dashscope 调用产生 {@code gen_ai_client_operation_seconds} 与
 * {@code gen_ai_client_token_usage_total} 指标，模式与 {@code MoonshotChatModel}、
 * {@code DashScopeEmbeddingModel} 保持一致。</p>
 */
public class DashScopeChatModel implements ChatModel {

    private static final String DEFAULT_MODEL = "qwen-max";

    private static final String PROVIDER = "dashscope";

    private static final ChatModelObservationConvention DEFAULT_OBSERVATION_CONVENTION =
            new DefaultChatModelObservationConvention();

    /** 工具调用管理器缺省值（同 MoonshotChatModel，仅用于解析工具定义，执行由框架 ToolCallingAdvisor 外部化）。 */
    private static final ToolCallingManager DEFAULT_TOOL_CALLING_MANAGER = ToolCallingManager.builder().build();

    private final String baseUrl;

    private final String apiKey;

    private final DashScopeChatOptions defaultOptions;

    private final ObservationRegistry observationRegistry;

    private final ToolCallingManager toolCallingManager;

    private ChatModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

    public DashScopeChatModel(String baseUrl, String apiKey, DashScopeChatOptions defaultOptions) {
        this(baseUrl, apiKey, defaultOptions, ObservationRegistry.NOOP);
    }

    public DashScopeChatModel(String baseUrl, String apiKey, DashScopeChatOptions defaultOptions,
            ObservationRegistry observationRegistry) {
        this(baseUrl, apiKey, defaultOptions, observationRegistry, DEFAULT_TOOL_CALLING_MANAGER);
    }

    public DashScopeChatModel(String baseUrl, String apiKey, DashScopeChatOptions defaultOptions,
            ObservationRegistry observationRegistry, ToolCallingManager toolCallingManager) {
        Assert.notNull(observationRegistry, "observationRegistry cannot be null");
        Assert.notNull(toolCallingManager, "toolCallingManager cannot be null");
        this.baseUrl = DashScopeBaseUrlSupport.normalize(baseUrl);
        this.apiKey = apiKey;
        this.defaultOptions = defaultOptions != null ? defaultOptions
                : DashScopeChatOptions.builder().model(DEFAULT_MODEL).build();
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
                        GenerationResult result = createGeneration().call(createParam(prompt, false));
                        ChatResponse chatResponse = toChatResponse(result);
                        observationContext.setResponse(chatResponse);
                        return chatResponse;
                    } catch (Exception e) {
                        observationContext.setError(e);
                        throw new IllegalStateException("DashScope chat call failed", e);
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
                return Flux.from(createGeneration().streamCall(createParam(prompt, true)))
                        .map(this::toChatResponse)
                        .doOnNext(observationContext::setResponse)
                        .doOnError(observation::error)
                        .doFinally(signal -> observation.stop());
            } catch (Exception e) {
                observation.error(e);
                observation.stop();
                return Flux.error(new IllegalStateException("DashScope chat stream failed", e));
            }
        });
    }

    @Override
    public ChatOptions getOptions() {
        return this.defaultOptions.mutate().build();
    }

    private com.alibaba.dashscope.aigc.generation.Generation createGeneration() {
        return new com.alibaba.dashscope.aigc.generation.Generation("http", this.baseUrl);
    }

    private GenerationParam createParam(Prompt prompt, boolean stream) {
        DashScopeChatOptions options = mergeOptions(prompt.getOptions());
        GenerationParam.GenerationParamBuilder<?, ?> builder = GenerationParam.builder()
                .apiKey(this.apiKey)
                .model(resolveModel(options))
                .messages(toDashScopeMessages(prompt.getInstructions()))
                .incrementalOutput(stream || Boolean.TRUE.equals(options.getIncrementalOutput()));
        if (options.getTemperature() != null) {
            builder.temperature(options.getTemperature().floatValue());
        }
        if (options.getMaxTokens() != null) {
            builder.maxTokens(options.getMaxTokens());
        }
        if (options.getTopP() != null) {
            builder.topP(options.getTopP());
        }
        if (options.getTopK() != null) {
            builder.topK(options.getTopK());
        }
        if (options.getStopSequences() != null && !options.getStopSequences().isEmpty()) {
            builder.stopStrings(options.getStopSequences());
        }
        // 思考模型参数：仅显式设置时透传（默认不设置，遵守「思考模式由模型名决定」约束，
        // 对非思考模型设置 enableThinking=true 会导致 400 Bad Request）
        if (options.getEnableThinking() != null) {
            builder.enableThinking(options.getEnableThinking());
        }
        if (options.getThinkingBudget() != null) {
            builder.thinkingBudget(options.getThinkingBudget());
        }
        // 工具定义：toolCallbacks/toolNames → ToolDefinition → dashscope ToolFunction
        List<ToolDefinition> toolDefinitions = this.toolCallingManager.resolveToolDefinitions(options);
        if (!CollectionUtils.isEmpty(toolDefinitions)) {
            builder.tools(toDashScopeTools(toolDefinitions));
            // 工具选择模式（规划 G8）：auto/none/required 透传 GenerationParam.toolChoice
            if (StringUtils.hasText(options.getToolChoice())) {
                builder.toolChoice(options.getToolChoice());
            }
        }
        return builder.build();
    }

    /**
     * Spring AI {@link ToolDefinition} → dashscope-sdk-java {@link ToolBase}。
     * 映射对齐 {@code EnterpriseDashScopeToolService#buildXxxToolFunction()} 已验证模式。
     */
    private List<ToolBase> toDashScopeTools(List<ToolDefinition> toolDefinitions) {
        List<ToolBase> tools = new ArrayList<>();
        for (ToolDefinition toolDefinition : toolDefinitions) {
            JsonObject parameters = StringUtils.hasText(toolDefinition.inputSchema())
                    ? JsonParser.parseString(toolDefinition.inputSchema()).getAsJsonObject()
                    : null;
            FunctionDefinition function = FunctionDefinition.builder()
                    .name(toolDefinition.name())
                    .description(toolDefinition.description())
                    .parameters(parameters)
                    .build();
            tools.add(ToolFunction.builder().function(function).build());
        }
        return tools;
    }

    /**
     * 合并运行时 options 与默认 options。
     *
     * <p>2026-09-04 Phase 2：新增工具字段合并（{@code mergeToolCallbacks/mergeToolContext}，
     * 修复 {@code applyRobotToolCallbacks} 注入的工具回调被静默丢弃的问题——2026-08-10 Skills
     * 调试发现的同款根因）与思考参数合并。工具执行不在此处（同 MoonshotChatModel，
     * 由框架自动注册的 ToolCallingAdvisor 外部执行）。</p>
     */
    private DashScopeChatOptions mergeOptions(ChatOptions runtimeOptions) {
        DashScopeChatOptions merged = DashScopeChatOptions.fromOptions(this.defaultOptions);
        if (runtimeOptions == null) {
            ToolCallingChatOptions.validateToolCallbacks(merged.getToolCallbacks());
            return merged;
        }
        if (StringUtils.hasText(runtimeOptions.getModel())) {
            merged.setModel(runtimeOptions.getModel());
        }
        if (runtimeOptions.getTemperature() != null) {
            merged.setTemperature(runtimeOptions.getTemperature());
        }
        if (runtimeOptions.getMaxTokens() != null) {
            merged.setMaxTokens(runtimeOptions.getMaxTokens());
        }
        if (runtimeOptions.getTopP() != null) {
            merged.setTopP(runtimeOptions.getTopP());
        }
        if (runtimeOptions.getTopK() != null) {
            merged.setTopK(runtimeOptions.getTopK());
        }
        if (runtimeOptions.getStopSequences() != null) {
            merged.setStopSequences(runtimeOptions.getStopSequences());
        }
        // 思考参数与 toolNames：仅 DashScopeChatOptions 透传
        if (runtimeOptions instanceof DashScopeChatOptions runtimeDashscope) {
            if (runtimeDashscope.getEnableThinking() != null) {
                merged.setEnableThinking(runtimeDashscope.getEnableThinking());
            }
            if (runtimeDashscope.getThinkingBudget() != null) {
                merged.setThinkingBudget(runtimeDashscope.getThinkingBudget());
            }
            if (StringUtils.hasText(runtimeDashscope.getToolChoice())) {
                merged.setToolChoice(runtimeDashscope.getToolChoice());
            }
            if (!CollectionUtils.isEmpty(runtimeDashscope.getToolNames())) {
                merged.setToolNames(new HashSet<>(runtimeDashscope.getToolNames()));
            }
        }
        // 工具回调/上下文：runtime 与 default 合并（忽略 runtime 会丢 applyRobotToolCallbacks 注入）
        if (runtimeOptions instanceof ToolCallingChatOptions runtimeToolOptions) {
            merged.setToolCallbacks(ToolCallingChatOptions.mergeToolCallbacks(
                    runtimeToolOptions.getToolCallbacks(), merged.getToolCallbacks()));
            merged.setToolContext(ToolCallingChatOptions.mergeToolContext(
                    runtimeToolOptions.getToolContext(), merged.getToolContext()));
        }
        ToolCallingChatOptions.validateToolCallbacks(merged.getToolCallbacks());
        return merged;
    }

    private String resolveModel(DashScopeChatOptions options) {
        return StringUtils.hasText(options.getModel()) ? options.getModel() : DEFAULT_MODEL;
    }

    private List<com.alibaba.dashscope.common.Message> toDashScopeMessages(List<Message> messages) {
        List<com.alibaba.dashscope.common.Message> result = new ArrayList<>();
        for (Message message : messages) {
            // 工具结果消息：每个 ToolResponse 一条 role=tool 消息（带 toolCallId/name）
            if (message.getMessageType() == MessageType.TOOL
                    && message instanceof ToolResponseMessage toolResponseMessage) {
                for (ToolResponseMessage.ToolResponse toolResponse : toolResponseMessage.getResponses()) {
                    result.add(com.alibaba.dashscope.common.Message.builder()
                            .role("tool")
                            .toolCallId(toolResponse.id() != null ? toolResponse.id() : "")
                            .name(toolResponse.name() != null ? toolResponse.name() : "")
                            .content(toolResponse.responseData() != null
                                    ? String.valueOf(toolResponse.responseData()) : "")
                            .build());
                }
                continue;
            }
            com.alibaba.dashscope.common.Message.MessageBuilder<?, ?> builder = com.alibaba.dashscope.common.Message
                    .builder()
                    .role(message.getMessageType().getValue())
                    .content(message.getText());
            // assistant 工具调用消息：回传 toolCalls 供模型续轮
            if (message instanceof AssistantMessage assistantMessage
                    && !CollectionUtils.isEmpty(assistantMessage.getToolCalls())) {
                builder.toolCalls(toDashScopeToolCalls(assistantMessage.getToolCalls()));
            }
            result.add(builder.build());
        }
        return result;
    }

    /**
     * Spring AI {@link AssistantMessage.ToolCall} → dashscope-sdk-java {@link ToolCallFunction}。
     * SDK 该类无 builder，使用 setter + 内部类 CallFunction（已 javap 核实 2.22.28）。
     */
    private List<ToolCallBase> toDashScopeToolCalls(List<AssistantMessage.ToolCall> toolCalls) {
        List<ToolCallBase> result = new ArrayList<>();
        for (AssistantMessage.ToolCall toolCall : toolCalls) {
            ToolCallFunction callFunction = new ToolCallFunction();
            callFunction.setId(toolCall.id() != null ? toolCall.id() : "");
            callFunction.setType(toolCall.type() != null ? toolCall.type() : "function");
            ToolCallFunction.CallFunction function = callFunction.new CallFunction();
            function.setName(toolCall.name());
            function.setArguments(toolCall.arguments() != null ? toolCall.arguments() : "{}");
            callFunction.setFunction(function);
            result.add(callFunction);
        }
        return result;
    }

    private ChatResponse toChatResponse(GenerationResult result) {
        GenerationOutput output = result != null ? result.getOutput() : null;
        if (output == null) {
            return new ChatResponse(List.of(), toChatResponseMetadata(result));
        }
        List<Generation> generations = new ArrayList<>();
        if (output.getChoices() != null && !output.getChoices().isEmpty()) {
            for (GenerationOutput.Choice choice : output.getChoices()) {
                com.alibaba.dashscope.common.Message choiceMessage = choice.getMessage();
                String text = choiceMessage != null && choiceMessage.getContent() != null
                        ? choiceMessage.getContent() : "";
                String finishReason = choice.getFinishReason() != null ? choice.getFinishReason()
                        : output.getFinishReason();
                List<AssistantMessage.ToolCall> toolCalls = toSpringAiToolCalls(choiceMessage);
                if (!toolCalls.isEmpty() && !StringUtils.hasText(finishReason)) {
                    finishReason = "tool_calls";
                }
                generations.add(new Generation(toAssistantMessage(choiceMessage, text, toolCalls),
                        ChatGenerationMetadata.builder().finishReason(finishReason).build()));
            }
        } else {
            String text = output.getText() != null ? output.getText() : "";
            generations.add(new Generation(new AssistantMessage(text),
                    ChatGenerationMetadata.builder().finishReason(output.getFinishReason()).build()));
        }
        return new ChatResponse(generations, toChatResponseMetadata(result));
    }

    /**
     * 构造 Spring AI {@link AssistantMessage}：透传 reasoning_content（metadata key
     * {@code reasoningContent}，契约见 {@code ReasoningContentHelper}，helper 零改动）与 toolCalls。
     */
    private AssistantMessage toAssistantMessage(com.alibaba.dashscope.common.Message choiceMessage,
            String text, List<AssistantMessage.ToolCall> toolCalls) {
        AssistantMessage.Builder<?> builder = AssistantMessage.builder()
                .content(text)
                .toolCalls(toolCalls)
                .media(List.of());
        if (choiceMessage != null && StringUtils.hasText(choiceMessage.getReasoningContent())) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("reasoningContent", choiceMessage.getReasoningContent());
            builder.properties(metadata);
        }
        return builder.build();
    }

    /**
     * dashscope-sdk-java {@link ToolCallBase}（实际为 {@link ToolCallFunction}）→
     * Spring AI {@link AssistantMessage.ToolCall}。name/arguments 经 getFunction() 取。
     */
    private List<AssistantMessage.ToolCall> toSpringAiToolCalls(
            com.alibaba.dashscope.common.Message choiceMessage) {
        if (choiceMessage == null || CollectionUtils.isEmpty(choiceMessage.getToolCalls())) {
            return List.of();
        }
        List<AssistantMessage.ToolCall> toolCalls = new ArrayList<>();
        for (ToolCallBase toolCallBase : choiceMessage.getToolCalls()) {
            if (toolCallBase instanceof ToolCallFunction toolCallFunction
                    && toolCallFunction.getFunction() != null) {
                String name = toolCallFunction.getFunction().getName();
                String arguments = toolCallFunction.getFunction().getArguments();
                toolCalls.add(new AssistantMessage.ToolCall(
                        toolCallFunction.getId() != null ? toolCallFunction.getId() : "",
                        toolCallFunction.getType() != null ? toolCallFunction.getType() : "function",
                        name != null ? name : "",
                        arguments != null ? arguments : "{}"));
            }
        }
        return toolCalls;
    }

    private ChatResponseMetadata toChatResponseMetadata(GenerationResult result) {
        if (result == null) {
            return ChatResponseMetadata.builder().usage(new DefaultUsage(0, 0, 0)).build();
        }
        Usage usage = toUsage(result.getUsage());
        return ChatResponseMetadata.builder()
                .id(result.getRequestId() != null ? result.getRequestId() : "")
                .usage(usage)
                .model(resolveModel(this.defaultOptions))
                .keyValue("prompt_tokens", usage.getPromptTokens())
                .keyValue("completion_tokens", usage.getCompletionTokens())
                .keyValue("total_tokens", usage.getTotalTokens())
                .keyValue("statusCode", result.getStatusCode() != null ? result.getStatusCode() : 0)
                .keyValue("code", result.getCode() != null ? result.getCode() : "")
                .keyValue("message", result.getMessage() != null ? result.getMessage() : "")
                .build();
    }

    private Usage toUsage(GenerationUsage usage) {
        if (usage == null) {
            return new DefaultUsage(0, 0, 0);
        }
        return new DefaultUsage(
                usage.getInputTokens() != null ? usage.getInputTokens() : 0,
                usage.getOutputTokens() != null ? usage.getOutputTokens() : 0,
                usage.getTotalTokens() != null ? usage.getTotalTokens() : 0,
                usage);
    }
}