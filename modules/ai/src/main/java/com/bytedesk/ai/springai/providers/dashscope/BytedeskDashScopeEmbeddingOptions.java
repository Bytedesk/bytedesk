package com.bytedesk.ai.springai.providers.dashscope;

import org.springframework.ai.embedding.EmbeddingOptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BytedeskDashScopeEmbeddingOptions implements EmbeddingOptions {

    private String model;

    private Integer dimensions;
}