package com.bytedesk.ai.provider.dashscope.chat;

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
 * DashScope 聊天选项。
 *
 * <p>2026-09-04：升级为 {@link ToolCallingChatOptions}（规划
 * {@code docs/plans/2026-09-04-dashscope-springai-chatmodel-chatmemory-plan.md} Phase 1），
 * 蓝本为 in-repo 的 {@code MoonshotChatOptions}。升级后：</p>
 * <ul>
 *   <li>{@code applyRobotToolCallbacks(options, llm)} 注入的工具回调不再被
 *       {@code DefaultChatClientUtils} 静默丢弃（此前 {@code implements ChatOptions} 时
 *       工具定义不会写入 prompt）；</li>
 *   <li>新增思考模型参数 {@code enableThinking}/{@code thinkingBudget}（仅透传、默认不设置，
 *       遵守「思考模式由模型名决定，不盲目开启」约束——对非思考模型设置会导致 400）。</li>
 * </ul>
 */
@Data
public class DashScopeChatOptions implements ToolCallingChatOptions {

    private String model;

    private Double temperature;

    private Integer maxTokens;

    private Double topP;

    private Integer topK;

    private Double frequencyPenalty;

    private Double presencePenalty;

    private List<String> stopSequences;

    private Boolean incrementalOutput;

    // ---- 思考模型参数（预留透传，默认不设置）----
    // 仅 qwen3 等思考模型有效；非思考模型设置 enableThinking=true 会导致 400 Bad Request，
    // 故默认不设置，由 ChatModel 侧条件透传（非 null 才写入 GenerationParam）。
    private Boolean enableThinking;

    private Integer thinkingBudget;

    // ---- 工具调用（ToolCallingChatOptions 契约）----
    // @JsonIgnore：非 API 字段，仅框架内部使用，不参与序列化（同 MoonshotChatOptions）。
    @JsonIgnore
    private List<ToolCallback> toolCallbacks = new ArrayList<>();

    /**
     * 工具选择模式（规划 G8）：auto / none / required，
     * 透传至 DashScope GenerationParam.toolChoice；null=不设置（默认 auto）。
     */
    private String toolChoice;

    @JsonIgnore
    private Set<String> toolNames = new HashSet<>();

    @JsonIgnore
    private Map<String, Object> toolContext = new HashMap<>();

    public static Builder builder() {
        return new Builder();
    }

    public static DashScopeChatOptions fromOptions(DashScopeChatOptions from) {
        return DashScopeChatOptions.builder()
                .model(from.getModel())
                .temperature(from.getTemperature())
                .maxTokens(from.getMaxTokens())
                .topP(from.getTopP())
                .topK(from.getTopK())
                .frequencyPenalty(from.getFrequencyPenalty())
                .presencePenalty(from.getPresencePenalty())
                .stopSequences(from.getStopSequences() != null ? new ArrayList<>(from.getStopSequences()) : null)
                .incrementalOutput(from.getIncrementalOutput())
                .enableThinking(from.getEnableThinking())
                .thinkingBudget(from.getThinkingBudget())
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
                .topK(this.topK)
                .frequencyPenalty(this.frequencyPenalty)
                .presencePenalty(this.presencePenalty)
                .stopSequences(this.stopSequences != null ? new ArrayList<>(this.stopSequences) : null)
                .incrementalOutput(this.incrementalOutput)
                .enableThinking(this.enableThinking)
                .thinkingBudget(this.thinkingBudget)
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
        DashScopeChatOptions other = (DashScopeChatOptions) o;
        return Objects.equals(this.model, other.model)
                && Objects.equals(this.temperature, other.temperature)
                && Objects.equals(this.maxTokens, other.maxTokens)
                && Objects.equals(this.topP, other.topP)
                && Objects.equals(this.topK, other.topK)
                && Objects.equals(this.frequencyPenalty, other.frequencyPenalty)
                && Objects.equals(this.presencePenalty, other.presencePenalty)
                && Objects.equals(this.stopSequences, other.stopSequences)
                && Objects.equals(this.incrementalOutput, other.incrementalOutput)
                && Objects.equals(this.enableThinking, other.enableThinking)
                && Objects.equals(this.thinkingBudget, other.thinkingBudget)
                && Objects.equals(this.toolChoice, other.toolChoice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.model, this.temperature, this.maxTokens, this.topP, this.topK,
                this.frequencyPenalty, this.presencePenalty, this.stopSequences, this.incrementalOutput,
                this.enableThinking, this.thinkingBudget, this.toolChoice);
    }

    @Override
    public String toString() {
        return "DashScopeChatOptions{model='" + this.model + "', temperature=" + this.temperature
                + ", maxTokens=" + this.maxTokens + ", topP=" + this.topP + ", topK=" + this.topK
                + ", enableThinking=" + this.enableThinking + ", thinkingBudget=" + this.thinkingBudget
                + ", toolChoice=" + this.toolChoice + "}";
    }

    public static final class Builder implements ToolCallingChatOptions.Builder<Builder> {

        private final DashScopeChatOptions options = new DashScopeChatOptions();

        @Override
        public Builder clone() {
            return DashScopeChatOptions.fromOptions(this.options).mutate();
        }

        @Override
        public Builder model(String model) {
            options.setModel(model);
            return this;
        }

        @Override
        public Builder frequencyPenalty(Double frequencyPenalty) {
            options.setFrequencyPenalty(frequencyPenalty);
            return this;
        }

        @Override
        public Builder maxTokens(Integer maxTokens) {
            options.setMaxTokens(maxTokens);
            return this;
        }

        @Override
        public Builder presencePenalty(Double presencePenalty) {
            options.setPresencePenalty(presencePenalty);
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
        public Builder topK(Integer topK) {
            options.setTopK(topK);
            return this;
        }

        @Override
        public Builder topP(Double topP) {
            options.setTopP(topP);
            return this;
        }

        public Builder incrementalOutput(Boolean incrementalOutput) {
            options.setIncrementalOutput(incrementalOutput);
            return this;
        }

        public Builder enableThinking(Boolean enableThinking) {
            options.setEnableThinking(enableThinking);
            return this;
        }

        public Builder thinkingBudget(Integer thinkingBudget) {
            options.setThinkingBudget(thinkingBudget);
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
        public DashScopeChatOptions build() {
            return options;
        }

        @Override
        public Builder combineWith(ChatOptions.Builder<?> other) {
            if (other == null) {
                return this;
            }
            ChatOptions otherOptions = other.build();
            if (otherOptions instanceof DashScopeChatOptions dashscopeOptions) {
                this.options.setModel(dashscopeOptions.getModel());
                this.options.setTemperature(dashscopeOptions.getTemperature());
                this.options.setMaxTokens(dashscopeOptions.getMaxTokens());
                this.options.setTopP(dashscopeOptions.getTopP());
                this.options.setTopK(dashscopeOptions.getTopK());
                this.options.setFrequencyPenalty(dashscopeOptions.getFrequencyPenalty());
                this.options.setPresencePenalty(dashscopeOptions.getPresencePenalty());
                this.options.setStopSequences(dashscopeOptions.getStopSequences() != null
                        ? new ArrayList<>(dashscopeOptions.getStopSequences()) : null);
                this.options.setIncrementalOutput(dashscopeOptions.getIncrementalOutput());
                this.options.setEnableThinking(dashscopeOptions.getEnableThinking());
                this.options.setThinkingBudget(dashscopeOptions.getThinkingBudget());
                this.options.setToolChoice(dashscopeOptions.getToolChoice());
                this.options.setToolCallbacks(dashscopeOptions.getToolCallbacks());
                this.options.setToolNames(dashscopeOptions.getToolNames());
                this.options.setToolContext(dashscopeOptions.getToolContext());
                return this;
            }
            if (otherOptions.getModel() != null) {
                options.setModel(otherOptions.getModel());
            }
            if (otherOptions.getFrequencyPenalty() != null) {
                options.setFrequencyPenalty(otherOptions.getFrequencyPenalty());
            }
            if (otherOptions.getMaxTokens() != null) {
                options.setMaxTokens(otherOptions.getMaxTokens());
            }
            if (otherOptions.getPresencePenalty() != null) {
                options.setPresencePenalty(otherOptions.getPresencePenalty());
            }
            if (otherOptions.getStopSequences() != null) {
                options.setStopSequences(new ArrayList<>(otherOptions.getStopSequences()));
            }
            if (otherOptions.getTemperature() != null) {
                options.setTemperature(otherOptions.getTemperature());
            }
            if (otherOptions.getTopK() != null) {
                options.setTopK(otherOptions.getTopK());
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