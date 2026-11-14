package com.bytedesk.ai.provider.volcengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 火山引擎方舟聊天选项。
 *
 * <p>2026-11 迁移方舟官方 SDK（com.volcengine:ark-runtime）时新建，
 * 蓝本 {@code ZhipuaiChatOptions} / {@code DashScopeChatOptions}，用于
 * 回接 Spring AI ChatClient 链路（多轮记忆 / 工具调用 / Advisor）。</p>
 *
 * <p>特有字段：</p>
 * <ul>
 *   <li>{@code enableThinking}：方舟 {@code Thinking} type=enabled/disabled。
 *       三态：null=不设置该参数（由模型名决定，非思考模型设置会导致 400），
 *       true/false=显式开启/关闭。</li>
 *   <li>{@code toolChoice}：auto/none/required，透传至方舟 tool_choice。</li>
 * </ul>
 */
@Data
public class ArkChatOptions implements ToolCallingChatOptions {

    private String model;

    private Double temperature;

    private Integer maxTokens;

    private Double topP;

    // ChatOptions 契约字段（Spring AI 2.0 抽象方法；方舟 API 本身不支持，仅透传）
    private Integer topK;

    private Double frequencyPenalty;

    private Double presencePenalty;

    private List<String> stopSequences;

    // ---- 思考模式（ark-runtime Thinking type=enabled/disabled）----
    // 三态：null=不设置 thinking 参数；true=enabled；false=disabled。
    private Boolean enableThinking;

    /**
     * 工具选择模式：auto / none / required，
     * 透传至方舟 tool_choice；null=不设置（默认 auto）。
     */
    private String toolChoice;

    // ---- 工具调用（ToolCallingChatOptions 契约）----
    @JsonIgnore
    private List<ToolCallback> toolCallbacks = new ArrayList<>();

    @JsonIgnore
    private Set<String> toolNames = new HashSet<>();

    @JsonIgnore
    private Map<String, Object> toolContext = new HashMap<>();

    public static Builder builder() {
        return new Builder();
    }

    public static ArkChatOptions fromOptions(ArkChatOptions from) {
        return ArkChatOptions.builder()
                .model(from.getModel())
                .temperature(from.getTemperature())
                .maxTokens(from.getMaxTokens())
                .topP(from.getTopP())
                .stopSequences(from.getStopSequences() != null ? new ArrayList<>(from.getStopSequences()) : null)
                .enableThinking(from.getEnableThinking())
                .toolChoice(from.getToolChoice())
                .toolCallbacks(from.getToolCallbacks() != null ? new ArrayList<>(from.getToolCallbacks()) : null)
                .toolNames(from.getToolNames() != null ? new HashSet<>(from.getToolNames()) : null)
                .toolContext(from.getToolContext() != null ? new HashMap<>(from.getToolContext()) : null)
                .build();
    }

    @JsonIgnore
    public void setToolCallbacks(List<ToolCallback> toolCallbacks) {
        if (toolCallbacks == null) {
            this.toolCallbacks = new ArrayList<>();
            return;
        }
        Assert.noNullElements(toolCallbacks, "toolCallbacks cannot contain null elements");
        this.toolCallbacks = new ArrayList<>(toolCallbacks);
    }

    @JsonIgnore
    public void setToolNames(Set<String> toolNames) {
        if (toolNames == null) {
            this.toolNames = new HashSet<>();
            return;
        }
        Assert.noNullElements(toolNames, "toolNames cannot contain null elements");
        toolNames.forEach(tool -> Assert.hasText(tool, "toolNames cannot contain empty elements"));
        this.toolNames = new HashSet<>(toolNames);
    }

    public void setToolContext(Map<String, Object> toolContext) {
        this.toolContext = toolContext != null ? new HashMap<>(toolContext) : new HashMap<>();
    }

    @Override
    public Builder mutate() {
        return builder()
                .model(this.model)
                .temperature(this.temperature)
                .maxTokens(this.maxTokens)
                .topP(this.topP)
                .stopSequences(this.stopSequences != null ? new ArrayList<>(this.stopSequences) : null)
                .enableThinking(this.enableThinking)
                .toolChoice(this.toolChoice)
                .toolCallbacks(this.toolCallbacks != null ? new ArrayList<>(this.toolCallbacks) : null)
                .toolNames(this.toolNames != null ? new HashSet<>(this.toolNames) : null)
                .toolContext(this.toolContext != null ? new HashMap<>(this.toolContext) : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArkChatOptions other = (ArkChatOptions) o;
        return Objects.equals(this.model, other.model)
                && Objects.equals(this.temperature, other.temperature)
                && Objects.equals(this.maxTokens, other.maxTokens)
                && Objects.equals(this.topP, other.topP)
                && Objects.equals(this.stopSequences, other.stopSequences)
                && Objects.equals(this.enableThinking, other.enableThinking)
                && Objects.equals(this.toolChoice, other.toolChoice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.model, this.temperature, this.maxTokens, this.topP,
                this.stopSequences, this.enableThinking, this.toolChoice);
    }

    @Override
    public String toString() {
        return "ArkChatOptions{model='" + this.model + "', temperature=" + this.temperature
                + ", maxTokens=" + this.maxTokens + ", topP=" + this.topP
                + ", enableThinking=" + this.enableThinking + ", toolChoice=" + this.toolChoice + "}";
    }

    public static final class Builder implements ToolCallingChatOptions.Builder<Builder> {

        private final ArkChatOptions options = new ArkChatOptions();

        @Override
        public Builder clone() {
            return ArkChatOptions.fromOptions(this.options).mutate();
        }

        @Override
        public Builder model(String model) {
            options.setModel(model);
            return this;
        }

        @Override
        public Builder maxTokens(Integer maxTokens) {
            options.setMaxTokens(maxTokens);
            return this;
        }

        @Override
        public Builder stopSequences(List<String> stopSequences) {
            options.setStopSequences(stopSequences != null ? new ArrayList<>(stopSequences) : null);
            return this;
        }

        @Override
        public Builder temperature(Double temperature) {
            options.setTemperature(temperature);
            return this;
        }

        @Override
        public Builder topP(Double topP) {
            options.setTopP(topP);
            return this;
        }

        @Override
        public Builder topK(Integer topK) {
            options.setTopK(topK);
            return this;
        }

        @Override
        public Builder frequencyPenalty(Double frequencyPenalty) {
            options.setFrequencyPenalty(frequencyPenalty);
            return this;
        }

        @Override
        public Builder presencePenalty(Double presencePenalty) {
            options.setPresencePenalty(presencePenalty);
            return this;
        }

        public Builder enableThinking(Boolean enableThinking) {
            options.setEnableThinking(enableThinking);
            return this;
        }

        public Builder toolChoice(String toolChoice) {
            options.setToolChoice(toolChoice);
            return this;
        }

        @Override
        public Builder toolCallbacks(List<ToolCallback> toolCallbacks) {
            options.setToolCallbacks(toolCallbacks);
            return this;
        }

        @Override
        public Builder toolCallbacks(ToolCallback... toolCallbacks) {
            Assert.notNull(toolCallbacks, "toolCallbacks cannot be null");
            options.getToolCallbacks().addAll(Arrays.asList(toolCallbacks));
            return this;
        }

        public Builder toolNames(Set<String> toolNames) {
            Assert.notNull(toolNames, "toolNames cannot be null");
            options.setToolNames(toolNames);
            return this;
        }

        public Builder toolNames(String... toolNames) {
            Assert.notNull(toolNames, "toolNames cannot be null");
            options.getToolNames().addAll(Set.of(toolNames));
            return this;
        }

        @Override
        public Builder toolContext(Map<String, Object> toolContext) {
            if (options.getToolContext() == null) {
                options.setToolContext(toolContext);
            } else if (toolContext != null) {
                options.getToolContext().putAll(toolContext);
            }
            return this;
        }

        @Override
        public Builder toolContext(String key, Object value) {
            Assert.hasText(key, "key cannot be null");
            Assert.notNull(value, "value cannot be null");
            options.getToolContext().put(key, value);
            return this;
        }

        @Override
        public ArkChatOptions build() {
            return options;
        }

        @Override
        public Builder combineWith(ChatOptions.Builder<?> other) {
            if (other == null) {
                return this;
            }
            ChatOptions otherOptions = other.build();
            if (otherOptions instanceof ArkChatOptions arkOptions) {
                this.options.setModel(arkOptions.getModel());
                this.options.setTemperature(arkOptions.getTemperature());
                this.options.setMaxTokens(arkOptions.getMaxTokens());
                this.options.setTopP(arkOptions.getTopP());
                this.options.setStopSequences(arkOptions.getStopSequences() != null
                        ? new ArrayList<>(arkOptions.getStopSequences()) : null);
                this.options.setEnableThinking(arkOptions.getEnableThinking());
                this.options.setToolChoice(arkOptions.getToolChoice());
                this.options.setToolCallbacks(arkOptions.getToolCallbacks());
                this.options.setToolNames(arkOptions.getToolNames());
                this.options.setToolContext(arkOptions.getToolContext());
                return this;
            }
            if (otherOptions.getModel() != null) {
                options.setModel(otherOptions.getModel());
            }
            if (otherOptions.getMaxTokens() != null) {
                options.setMaxTokens(otherOptions.getMaxTokens());
            }
            if (otherOptions.getStopSequences() != null) {
                options.setStopSequences(new ArrayList<>(otherOptions.getStopSequences()));
            }
            if (otherOptions.getTemperature() != null) {
                options.setTemperature(otherOptions.getTemperature());
            }
            if (otherOptions.getTopP() != null) {
                options.setTopP(otherOptions.getTopP());
            }
            if (otherOptions instanceof ToolCallingChatOptions toolCallingChatOptions) {
                this.toolCallbacks(toolCallingChatOptions.getToolCallbacks());
                this.toolContext(toolCallingChatOptions.getToolContext());
            }
            return this;
        }
    }
}
