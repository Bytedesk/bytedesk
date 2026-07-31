package com.bytedesk.ai.springai.providers.dashscope;

import java.util.List;

import org.springframework.ai.chat.prompt.ChatOptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
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
    @SuppressWarnings("unchecked")
    public <T extends ChatOptions> T copy() {
        return (T) this.toBuilder().build();
    }
}