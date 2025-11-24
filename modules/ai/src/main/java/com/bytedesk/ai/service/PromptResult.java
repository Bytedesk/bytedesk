package com.bytedesk.ai.service;

import org.springframework.ai.chat.prompt.Prompt;

/**
 * Prompt 处理结果
 * 提供同步调用中同时返回模型响应和构造的 Prompt 对象
 */
public class PromptResult {
    private final String response;
    private final Prompt prompt;

    public PromptResult(String response, Prompt prompt) {
        this.response = response;
        this.prompt = prompt;
    }

    public String getResponse() {
        return response;
    }

    public Prompt getPrompt() {
        return prompt;
    }
}
