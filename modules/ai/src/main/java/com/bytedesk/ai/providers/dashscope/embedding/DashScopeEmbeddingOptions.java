package com.bytedesk.ai.providers.dashscope.embedding;

import org.springframework.ai.embedding.EmbeddingOptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class DashScopeEmbeddingOptions implements EmbeddingOptions {

    private String model;

    private Integer dimensions;
}