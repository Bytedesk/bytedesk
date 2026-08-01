package com.bytedesk.ai.springai.providers.dashscope;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.util.StringUtils;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;

public class BytedeskDashScopeEmbeddingModel implements EmbeddingModel {

    private static final String DEFAULT_MODEL = "text-embedding-v4";

    private final String baseUrl;

    private final String apiKey;

    private final BytedeskDashScopeEmbeddingOptions defaultOptions;

    public BytedeskDashScopeEmbeddingModel(String baseUrl, String apiKey, BytedeskDashScopeEmbeddingOptions defaultOptions) {
        this.baseUrl = DashScopeBaseUrlSupport.normalize(baseUrl);
        this.apiKey = apiKey;
        this.defaultOptions = defaultOptions != null ? defaultOptions : BytedeskDashScopeEmbeddingOptions.builder().model(DEFAULT_MODEL).build();
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        try {
            TextEmbeddingResult result = new TextEmbedding(this.baseUrl).call(createParam(request));
            List<Embedding> embeddings = result.getOutput().getEmbeddings().stream()
                    .sorted(Comparator.comparing(TextEmbeddingResultItem::getTextIndex))
                    .map(item -> new Embedding(toFloatArray(item.getEmbedding()), item.getTextIndex()))
                    .toList();
            return new EmbeddingResponse(embeddings);
        } catch (Exception e) {
            throw new IllegalStateException("DashScope embedding call failed", e);
        }
    }

    @Override
    public float[] embed(Document document) {
        return embed(document.getText());
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        return call(new EmbeddingRequest(texts, this.defaultOptions)).getResults().stream()
                .map(Embedding::getOutput)
                .toList();
    }

    @Override
    public int dimensions() {
        Integer dimensions = this.defaultOptions.getDimensions();
        return dimensions != null && dimensions > 0 ? dimensions : EmbeddingModel.super.dimensions();
    }

    private TextEmbeddingParam createParam(EmbeddingRequest request) {
        BytedeskDashScopeEmbeddingOptions options = mergeOptions(request.getOptions());
        TextEmbeddingParam.TextEmbeddingParamBuilder<?, ?> builder = TextEmbeddingParam.builder()
                .apiKey(this.apiKey)
                .model(resolveModel(options))
                .texts(request.getInstructions());
        if (options.getDimensions() != null && options.getDimensions() > 0) {
            builder.dimension(options.getDimensions());
        }
        return builder.build();
    }

    private BytedeskDashScopeEmbeddingOptions mergeOptions(org.springframework.ai.embedding.EmbeddingOptions runtimeOptions) {
        BytedeskDashScopeEmbeddingOptions.BytedeskDashScopeEmbeddingOptionsBuilder builder = this.defaultOptions.toBuilder();
        if (runtimeOptions == null) {
            return builder.build();
        }
        if (StringUtils.hasText(runtimeOptions.getModel())) {
            builder.model(runtimeOptions.getModel());
        }
        if (runtimeOptions.getDimensions() != null) {
            builder.dimensions(runtimeOptions.getDimensions());
        }
        return builder.build();
    }

    private String resolveModel(BytedeskDashScopeEmbeddingOptions options) {
        return StringUtils.hasText(options.getModel()) ? options.getModel() : DEFAULT_MODEL;
    }

    private float[] toFloatArray(List<Double> values) {
        float[] result = new float[values.size()];
        IntStream.range(0, values.size()).forEach(index -> result[index] = values.get(index).floatValue());
        return result;
    }
}