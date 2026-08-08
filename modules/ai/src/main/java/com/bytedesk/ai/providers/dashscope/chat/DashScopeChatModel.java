package com.bytedesk.ai.providers.dashscope.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.util.StringUtils;

import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.GenerationUsage;
import com.bytedesk.ai.providers.dashscope.DashScopeBaseUrlSupport;

import reactor.core.publisher.Flux;

public class DashScopeChatModel implements ChatModel {

    private static final String DEFAULT_MODEL = "qwen-max";

    private final String baseUrl;

    private final String apiKey;

    private final DashScopeChatOptions defaultOptions;

    public DashScopeChatModel(String baseUrl, String apiKey, DashScopeChatOptions defaultOptions) {
        this.baseUrl = DashScopeBaseUrlSupport.normalize(baseUrl);
        this.apiKey = apiKey;
        this.defaultOptions = defaultOptions != null ? defaultOptions : DashScopeChatOptions.builder().model(DEFAULT_MODEL).build();
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        try {
            GenerationResult result = createGeneration().call(createParam(prompt, false));
            return toChatResponse(result);
        } catch (Exception e) {
            throw new IllegalStateException("DashScope chat call failed", e);
        }
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        return Flux.defer(() -> {
            try {
                return Flux.from(createGeneration().streamCall(createParam(prompt, true))).map(this::toChatResponse);
            } catch (Exception e) {
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
        return builder.build();
    }

    private DashScopeChatOptions mergeOptions(ChatOptions runtimeOptions) {
        DashScopeChatOptions.Builder builder = this.defaultOptions.mutate();
        if (runtimeOptions == null) {
            return builder.build();
        }
        if (StringUtils.hasText(runtimeOptions.getModel())) {
            builder.model(runtimeOptions.getModel());
        }
        if (runtimeOptions.getTemperature() != null) {
            builder.temperature(runtimeOptions.getTemperature());
        }
        if (runtimeOptions.getMaxTokens() != null) {
            builder.maxTokens(runtimeOptions.getMaxTokens());
        }
        if (runtimeOptions.getTopP() != null) {
            builder.topP(runtimeOptions.getTopP());
        }
        if (runtimeOptions.getTopK() != null) {
            builder.topK(runtimeOptions.getTopK());
        }
        if (runtimeOptions.getStopSequences() != null) {
            builder.stopSequences(runtimeOptions.getStopSequences());
        }
        return builder.build();
    }

    private String resolveModel(DashScopeChatOptions options) {
        return StringUtils.hasText(options.getModel()) ? options.getModel() : DEFAULT_MODEL;
    }

    private List<com.alibaba.dashscope.common.Message> toDashScopeMessages(List<Message> messages) {
        List<com.alibaba.dashscope.common.Message> result = new ArrayList<>();
        for (Message message : messages) {
            result.add(com.alibaba.dashscope.common.Message.builder()
                    .role(message.getMessageType().getValue())
                    .content(message.getText())
                    .build());
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
                String text = choice.getMessage() != null ? choice.getMessage().getContent() : "";
                String finishReason = choice.getFinishReason() != null ? choice.getFinishReason() : output.getFinishReason();
                generations.add(new Generation(new AssistantMessage(text),
                        ChatGenerationMetadata.builder().finishReason(finishReason).build()));
            }
        } else {
            String text = output.getText() != null ? output.getText() : "";
            generations.add(new Generation(new AssistantMessage(text),
                    ChatGenerationMetadata.builder().finishReason(output.getFinishReason()).build()));
        }
        return new ChatResponse(generations, toChatResponseMetadata(result));
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