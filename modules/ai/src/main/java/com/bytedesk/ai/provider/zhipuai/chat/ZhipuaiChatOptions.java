package com.bytedesk.ai.provider.zhipuai.chat;

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
 * 智谱 AI 聊天选项。
 *
 * <p>2026-09-04（规划 {@code docs/plans/2026-09-04-dashscope-springai-chatmodel-chatmemory-plan.md} Phase 5）：
 * 新建为 {@link ToolCallingChatOptions}（蓝本 DashScopeChatOptions / MoonshotChatOptions），用于
 * ZhipuaiService 回接 Spring AI ChatClient 链路（多轮记忆 / 工具调用 / Advisor）。</p>
 *
 * <p>特有字段：</p>
 * <ul>
 *   <li>{@code enableThinking}：zai-sdk {@code ChatThinking} type=enabled/disabled。
 *       语义与 DashScope 不同——智谱 API 允许显式关闭，故此处为三态：null=不设置该参数，
 *       true/false=显式开启/关闭（ZhipuaiService 默认按 robot.llm.thinking 显式设置）。</li>
 * </ul>
 */
@Data
public class ZhipuaiChatOptions implements ToolCallingChatOptions {

    private String model;

    private Double temperature;

    private Integer maxTokens;

    private Double topP;

    // ChatOptions 契约字段（Spring AI 2.0 抽象方法；zai 接口本身不支持，仅透传默认值）
    private Integer topK;

    private Double frequencyPenalty;

    private Double presencePenalty;

    private List<String> stopSequences;

    // ---- 思考模式（zai-sdk ChatThinking type=enabled/disabled）----
    // 三态：null=不设置 thinking 参数；true=enabled；false=disabled。
    private Boolean enableThinking;

    /**
     * 工具选择模式（规划 G8）：auto / none / required，
     * 透传至 zai-sdk ChatCompletionCreateParams.toolChoice；null=不设置（默认 auto）。
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

    public static ZhipuaiChatOptions fromOptions(ZhipuaiChatOptions from) {
        return ZhipuaiChatOptions.builder()
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
        ZhipuaiChatOptions other = (ZhipuaiChatOptions) o;
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
        return "ZhipuaiChatOptions{model='" + this.model + "', temperature=" + this.temperature
                + ", maxTokens=" + this.maxTokens + ", topP=" + this.topP
                + ", enableThinking=" + this.enableThinking + ", toolChoice=" + this.toolChoice + "}";
    }

    public static final class Builder implements ToolCallingChatOptions.Builder<Builder> {

        private final ZhipuaiChatOptions options = new ZhipuaiChatOptions();

        @Override
        public Builder clone() {
            return ZhipuaiChatOptions.fromOptions(this.options).mutate();
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
        public ZhipuaiChatOptions build() {
            return options;
        }

        @Override
        public Builder combineWith(ChatOptions.Builder<?> other) {
            if (other == null) {
                return this;
            }
            ChatOptions otherOptions = other.build();
            if (otherOptions instanceof ZhipuaiChatOptions zhipuaiOptions) {
                this.options.setModel(zhipuaiOptions.getModel());
                this.options.setTemperature(zhipuaiOptions.getTemperature());
                this.options.setMaxTokens(zhipuaiOptions.getMaxTokens());
                this.options.setTopP(zhipuaiOptions.getTopP());
                this.options.setStopSequences(zhipuaiOptions.getStopSequences() != null
                        ? new ArrayList<>(zhipuaiOptions.getStopSequences()) : null);
                this.options.setEnableThinking(zhipuaiOptions.getEnableThinking());
                this.options.setToolChoice(zhipuaiOptions.getToolChoice());
                this.options.setToolCallbacks(zhipuaiOptions.getToolCallbacks());
                this.options.setToolNames(zhipuaiOptions.getToolNames());
                this.options.setToolContext(zhipuaiOptions.getToolContext());
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
