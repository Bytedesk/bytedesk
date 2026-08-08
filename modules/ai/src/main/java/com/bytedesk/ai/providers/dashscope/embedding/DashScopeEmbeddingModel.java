package com.bytedesk.ai.providers.dashscope.embedding;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.observation.DefaultEmbeddingModelObservationConvention;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationContext;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationDocumentation;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;
import com.bytedesk.ai.providers.dashscope.DashScopeBaseUrlSupport;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * 阶段 7C：为 DashScope EmbeddingModel 补齐手动观测。
 *
 * <p>参照 {@code MoonshotChatModel} 的手动观测模式，在 {@link #call(EmbeddingRequest)}
 * 入口包装 {@link EmbeddingModelObservationDocumentation#EMBEDDING_MODEL_OPERATION}，
 * 使 Embedding 调用产生 {@code gen_ai.client.operation} 指标与 span。</p>
 */
@Slf4j
public class DashScopeEmbeddingModel implements EmbeddingModel {

    private static final String PROVIDER = "dashscope";

    private static final EmbeddingModelObservationConvention DEFAULT_OBSERVATION_CONVENTION =
            new DefaultEmbeddingModelObservationConvention();

    private static final String DEFAULT_MODEL = "text-embedding-v4";

    private final String baseUrl;

    private final String apiKey;

    private final DashScopeEmbeddingOptions defaultOptions;

    private final ObservationRegistry observationRegistry;

    private EmbeddingModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

    public DashScopeEmbeddingModel(String baseUrl, String apiKey, DashScopeEmbeddingOptions defaultOptions) {
        this(baseUrl, apiKey, defaultOptions, ObservationRegistry.NOOP);
    }

    public DashScopeEmbeddingModel(String baseUrl, String apiKey, DashScopeEmbeddingOptions defaultOptions,
                                           ObservationRegistry observationRegistry) {
        Assert.notNull(observationRegistry, "observationRegistry cannot be null");
        this.baseUrl = DashScopeBaseUrlSupport.normalize(baseUrl);
        this.apiKey = apiKey;
        this.defaultOptions = defaultOptions != null ? defaultOptions : DashScopeEmbeddingOptions.builder().model(DEFAULT_MODEL).build();
        this.observationRegistry = observationRegistry;
    }

    public void setObservationConvention(EmbeddingModelObservationConvention observationConvention) {
        this.observationConvention = observationConvention;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        DashScopeEmbeddingOptions merged = mergeOptions(request.getOptions());
        EmbeddingModelObservationContext observationContext = EmbeddingModelObservationContext.builder()
                .embeddingRequest(request)
                .provider(PROVIDER)
                .build();

        Observation observation = EmbeddingModelObservationDocumentation.EMBEDDING_MODEL_OPERATION
                .observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION,
                        () -> observationContext, this.observationRegistry)
                .start();

        try (Observation.Scope scope = observation.openScope()) {
            TextEmbeddingResult result = new TextEmbedding(this.baseUrl).call(createParam(request, merged));
            List<Embedding> embeddings = result.getOutput().getEmbeddings().stream()
                    .sorted(Comparator.comparing(TextEmbeddingResultItem::getTextIndex))
                    .map(item -> new Embedding(toFloatArray(item.getEmbedding()), item.getTextIndex()))
                    .toList();
            EmbeddingResponse response = new EmbeddingResponse(embeddings);
            observationContext.setResponse(response);
            return response;
        } catch (Exception e) {
            observation.error(e);
            throw new IllegalStateException("DashScope embedding call failed", e);
        } finally {
            observation.stop();
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

    private TextEmbeddingParam createParam(EmbeddingRequest request, DashScopeEmbeddingOptions options) {
        TextEmbeddingParam.TextEmbeddingParamBuilder<?, ?> builder = TextEmbeddingParam.builder()
                .apiKey(this.apiKey)
                .model(resolveModel(options))
                .texts(request.getInstructions());
        if (options.getDimensions() != null && options.getDimensions() > 0) {
            builder.dimension(options.getDimensions());
        }
        return builder.build();
    }

    private DashScopeEmbeddingOptions mergeOptions(org.springframework.ai.embedding.EmbeddingOptions runtimeOptions) {
        DashScopeEmbeddingOptions.DashScopeEmbeddingOptionsBuilder builder = this.defaultOptions.toBuilder();
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

    private String resolveModel(DashScopeEmbeddingOptions options) {
        return StringUtils.hasText(options.getModel()) ? options.getModel() : DEFAULT_MODEL;
    }

    private float[] toFloatArray(List<Double> values) {
        float[] result = new float[values.size()];
        IntStream.range(0, values.size()).forEach(index -> result[index] = values.get(index).floatValue());
        return result;
    }
}