package com.bytedesk.ai.springai.providers.dashscope;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.prompt.ChatOptions;

import lombok.Data;

@Data
public class BytedeskDashScopeChatOptions implements ChatOptions {

    private String model;

    private Double temperature;

    private Integer maxTokens;

    private Double topP;

    private Integer topK;

    private Double frequencyPenalty;

    private Double presencePenalty;

    private List<String> stopSequences;

    private Boolean incrementalOutput;

    @Override
    public Builder mutate() {
        return new Builder()
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .topP(topP)
                .topK(topK)
                .frequencyPenalty(frequencyPenalty)
                .presencePenalty(presencePenalty)
                .stopSequences(stopSequences)
                .incrementalOutput(incrementalOutput);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements ChatOptions.Builder<Builder> {

        private final BytedeskDashScopeChatOptions options = new BytedeskDashScopeChatOptions();

        @Override
        public Builder clone() {
            return options.mutate();
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

        @Override
        public BytedeskDashScopeChatOptions build() {
            return options;
        }

        @Override
        public Builder combineWith(ChatOptions.Builder<?> other) {
            if (other == null) {
                return this;
            }
            ChatOptions otherOptions = other.build();
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
            return this;
        }
    }
}