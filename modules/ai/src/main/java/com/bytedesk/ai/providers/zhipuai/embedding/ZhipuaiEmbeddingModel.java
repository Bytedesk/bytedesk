package com.bytedesk.ai.providers.zhipuai.embedding;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.util.StringUtils;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.embedding.EmbeddingCreateParams;
import ai.z.openapi.service.embedding.EmbeddingResult;

public class ZhipuaiEmbeddingModel implements EmbeddingModel {

    private static final String DEFAULT_MODEL = "embedding-2";

    private final ZhipuAiClient client;

    private final String defaultModel;

    private final Integer defaultDimensions;

    public ZhipuaiEmbeddingModel(ZhipuAiClient client, String defaultModel, Integer defaultDimensions) {
        this.client = client;
        this.defaultModel = StringUtils.hasText(defaultModel) ? defaultModel : DEFAULT_MODEL;
        this.defaultDimensions = defaultDimensions;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        try {
            EmbeddingCreateParams params = EmbeddingCreateParams.builder()
                    .model(resolveModel(request))
                    .dimensions(resolveDimensions(request))
                    .build();
            params.setInput(request.getInstructions());

            ai.z.openapi.service.embedding.EmbeddingResponse response = client.embeddings().createEmbeddings(params);
            EmbeddingResult result = response != null ? response.getData() : null;
            if (result == null || result.getData() == null) {
                return new EmbeddingResponse(List.of());
            }

            List<Embedding> embeddings = result.getData().stream()
                    .sorted(Comparator.comparing(ai.z.openapi.service.embedding.Embedding::getIndex,
                            Comparator.nullsLast(Integer::compareTo)))
                    .map(item -> new Embedding(toFloatArray(item.getEmbedding()), item.getIndex() != null ? item.getIndex() : 0))
                    .toList();
            return new EmbeddingResponse(embeddings);
        } catch (Exception e) {
            throw new IllegalStateException("ZhipuAI embedding call failed", e);
        }
    }

    @Override
    public float[] embed(Document document) {
        return embed(document.getText());
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        return call(new EmbeddingRequest(texts, null)).getResults().stream()
                .map(Embedding::getOutput)
                .toList();
    }

    @Override
    public int dimensions() {
        return defaultDimensions != null && defaultDimensions > 0 ? defaultDimensions : EmbeddingModel.super.dimensions();
    }

    private String resolveModel(EmbeddingRequest request) {
        if (request.getOptions() != null && StringUtils.hasText(request.getOptions().getModel())) {
            return request.getOptions().getModel();
        }
        return defaultModel;
    }

    private Integer resolveDimensions(EmbeddingRequest request) {
        if (request.getOptions() != null && request.getOptions().getDimensions() != null) {
            return request.getOptions().getDimensions();
        }
        return defaultDimensions;
    }

    private float[] toFloatArray(List<Double> values) {
        float[] result = new float[values.size()];
        IntStream.range(0, values.size()).forEach(index -> result[index] = values.get(index).floatValue());
        return result;
    }
}