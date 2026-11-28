/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-26 15:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-11-26 15:10:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.aliyun.dashscope.decision;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.decision.DecisionOptions;
import org.springframework.ai.decision.DecisionPrompt;
import org.springframework.ai.decision.DecisionResponse;
import org.springframework.ai.decision.question.ChoiceQuestion;
import org.springframework.ai.decision.question.DecisionQuestion;
import org.springframework.ai.decision.question.ScoreQuestion;
import org.springframework.ai.decision.question.YesNoQuestion;
import org.springframework.ai.systemone.SystemOneDecisionModel;
import org.springframework.ai.systemone.SystemOneDecisionOptions;
import org.springframework.ai.systemone.api.SystemOneApi;
import org.springframework.ai.systemone.api.SystemOneClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云百炼决策模型服务（TypeSafe System One 协议，{@code POST /compatible-mode/v1/systemone}）。
 *
 * <p>复用 Spring AI 2.1.0 的 {@link SystemOneClient} / {@link SystemOneDecisionModel}：
 * 阿里云决策模型即 System One 协议的百炼实现，请求/返回结构（model / state / questions / answers /
 * probabilities / confidence / legend）与 Spring AI 一致，只需把 baseUrl 指向百炼业务空间网关。
 * 这样 {@link #call(DecisionPrompt)} 与 Ollama 决策演示共用同一套 {@code DecisionModel} 抽象，
 * 后续客服路由 / 工单分流可直接以 provider 无关的方式接入。</p>
 *
 * <p>凭据解析：request.providerUid → {@link LlmProviderEntity}（apiKey / baseUrl），
 * 缺省回退全局 {@code spring.ai.dashscope.api-key} / {@code spring.ai.dashscope.base-url}；
 * 决策专用配置（base-url / workspace-id / region / api-key）优先级见 {@link DashScopeDecisionEndpoint}。</p>
 *
 * https://docs.bailian.console.aliyun.com/zh/model-studio/decision-model-api
 */
@Slf4j
@Service
public class DashScopeDecisionService {

    /** System One 协议路径；baseUrl 已包含 /compatible-mode/v1（或 workspace 网关根路径）。 */
    public static final String SYSTEM_ONE_PATH = "/systemone";

    /** Spring AI 从该响应头读取 request_id；阿里云在响应体中返回 request_id，需回填。 */
    static final String REQUEST_ID_HEADER = "x-typesafe-request-id";

    /**
     * 读取原始响应体中的 request_id，并以 {@link #REQUEST_ID_HEADER} 头回填，供 Spring AI 解析到
     * {@code DecisionResponseMetadata.getId()}。只解析一次 JSON，失败时静默跳过（不影响正常返回）。
     */
    static final ClientHttpRequestInterceptor REQUEST_ID_HEADER_INTERCEPTOR = (request, body, execution) -> {
        ClientHttpResponse response = execution.execute(request, body);
        byte[] payload = response.getBody().readAllBytes();
        String requestId = extractRequestId(payload);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(response.getHeaders());
        if (requestId != null) {
            headers.set(REQUEST_ID_HEADER, requestId);
        }
        return new BufferedResponse(response.getStatusCode(), headers, payload);
    };

    private static String extractRequestId(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return null;
        }
        try {
            JsonNode node = new ObjectMapper().readTree(payload);
            JsonNode requestId = node == null ? null : node.get("request_id");
            return requestId != null && requestId.isTextual() ? requestId.asText() : null;
        } catch (IOException ex) {
            return null;
        }
    }

    /** 不可变的已缓冲响应：允许下游（RestClient 反序列化）重复读取响应体。 */
    private record BufferedResponse(HttpStatusCode status, HttpHeaders headers, byte[] body) implements ClientHttpResponse {

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public HttpStatusCode getStatusCode() {
            return status;
        }

        @Override
        public String getStatusText() {
            return status.toString();
        }

        @Override
        public void close() {
        }
    }

    private final LlmProviderRestService llmProviderRestService;

    private final DashScopeDecisionProperties properties;

    private final String globalApiKey;

    private final String globalBaseUrl;

    private final ObjectProvider<ObservationRegistry> observationRegistryProvider;

    public DashScopeDecisionService(
            LlmProviderRestService llmProviderRestService,
            DashScopeDecisionProperties properties,
            @Value("${spring.ai.dashscope.api-key:}") String globalApiKey,
            @Value("${spring.ai.dashscope.base-url:}") String globalBaseUrl,
            ObjectProvider<ObservationRegistry> observationRegistryProvider) {
        this.llmProviderRestService = llmProviderRestService;
        this.properties = properties;
        this.globalApiKey = globalApiKey;
        this.globalBaseUrl = globalBaseUrl;
        this.observationRegistryProvider = observationRegistryProvider;
    }

    // ==================== 对外：通用 Java API ====================

    /**
     * 执行一次决策：组装 {@link DecisionPrompt} → 调用百炼决策模型 → 映射为与官方字段一致的响应。
     */
    public DashScopeDecisionResponse decide(DashScopeDecisionRequest request) {
        Assert.notNull(request, "request must not be null");
        long startTime = System.currentTimeMillis();
        DecisionPrompt prompt = toDecisionPrompt(request);
        SystemOneDecisionModel decisionModel = createDecisionModel(request.getProviderUid(), request.getModel());
        log.info("DashScope decision: model={}, endpoint={}, questions={}", decisionModel.getOptions().getModel(),
                resolveEndpoint(request.getProviderUid()), prompt.getQuestions().keySet());
        DecisionResponse response = decisionModel.call(prompt);
        DashScopeDecisionResponse result = toResponse(prompt, response);
        result.setDurationMs(System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * provider 无关入口：直接接受 Spring AI {@link DecisionPrompt}（与 Ollama 决策演示同一抽象），
     * 返回 Spring AI {@link DecisionResponse}，供客服路由 / 工单分流等后续能力调用。
     */
    public DecisionResponse call(DecisionPrompt prompt) {
        return call(prompt, null, null);
    }

    public DecisionResponse call(DecisionPrompt prompt, String providerUid, String model) {
        Assert.notNull(prompt, "request must not be null");
        return createDecisionModel(providerUid, model).call(prompt);
    }

    /**
     * 工单分流示例：派单团队 choice + 是否升级 noul + 严重度 score，一次前向返回三项决策。
     */
    public DashScopeDecisionResponse routeTicket(DashScopeDecisionResponse.TicketRoutingDemoRequest request) {
        Assert.notNull(request, "request must not be null");
        return decide(DashScopeDecisionRequest.builder()
                .model(request.getModel())
                .providerUid(request.getProviderUid())
                .state(request.toState())
                .questions(request.toQuestions())
                .build());
    }

    /** 当前生效的决策模型端点（不含 /systemone），用于排查配置与日志。 */
    public String resolveEndpoint(String providerUid) {
        return resolveEndpoint(resolveProvider(providerUid).orElse(null));
    }

    // ==================== 构建 Spring AI 决策模型 ====================

    private SystemOneDecisionModel createDecisionModel(String providerUid, String model) {
        Optional<LlmProviderEntity> provider = resolveProvider(providerUid);
        String apiKey = resolveApiKey(provider.orElse(null));
        Assert.hasText(apiKey, "DashScope decision apiKey is not configured; set spring.ai.dashscope.api-key "
                + "or spring.ai.dashscope.decision.api-key");
        String endpoint = resolveEndpoint(provider.orElse(null));
        Assert.hasText(endpoint, "DashScope decision endpoint is not configured; set spring.ai.dashscope.base-url, "
                + "spring.ai.dashscope.decision.workspace-id or spring.ai.dashscope.decision.base-url");
        String resolvedModel = StringUtils.hasText(model) ? model : properties.getModel();
        Assert.hasText(resolvedModel, "DashScope decision model must not be blank");

        // 阿里云把 request_id 放在响应体而非 x-typesafe-request-id 头；Spring AI 只从该头读取，
        // 这里用拦截器把响应体中的 request_id 回填到头里，使 DecisionResponseMetadata.id 可用于排障。
        SystemOneClient client = SystemOneClient.builder()
                .systemOneApi(SystemOneApi.builder()
                        // baseUrl 已含 /compatible-mode/v1；Spring AI 默认 systemOnePath 为 /v1/systemone，会重复拼成 /v1/v1
                        .systemOnePath(SYSTEM_ONE_PATH)
                        .baseUrl(endpoint)
                        .apiKey(apiKey)
                        .restClientBuilder(RestClient.builder().requestInterceptor(REQUEST_ID_HEADER_INTERCEPTOR))
                        .build())
                .apiKey(apiKey)
                .baseUrl(endpoint)
                .defaultModel(resolvedModel)
                .timeout(properties.getTimeout() != null ? properties.getTimeout() : Duration.ofSeconds(60))
                .build();
        ObservationRegistry observationRegistry = observationRegistryProvider != null
                ? observationRegistryProvider.getIfAvailable(() -> ObservationRegistry.NOOP)
                : ObservationRegistry.NOOP;
        return SystemOneDecisionModel.builder(client)
                .options(SystemOneDecisionOptions.builder().model(resolvedModel).build())
                .provider(LlmProviderConstants.DASHSCOPE)
                .observationRegistry(observationRegistry)
                .build();
    }

    private Optional<LlmProviderEntity> resolveProvider(String providerUid) {
        if (!StringUtils.hasText(providerUid)) {
            return Optional.empty();
        }
        Optional<LlmProviderEntity> provider = llmProviderRestService.findByUid(providerUid);
        if (provider.isEmpty()) {
            log.warn("DashScope decision: LlmProvider with uid {} not found, fallback to global config", providerUid);
        }
        return provider;
    }

    private String resolveApiKey(LlmProviderEntity provider) {
        if (StringUtils.hasText(properties.getApiKey())) {
            return properties.getApiKey();
        }
        if (provider != null && StringUtils.hasText(provider.getApiKey())) {
            return provider.getApiKey();
        }
        return globalApiKey;
    }

    private String resolveEndpoint(LlmProviderEntity provider) {
        String fallbackBaseUrl = provider != null && StringUtils.hasText(provider.getBaseUrl())
                ? provider.getBaseUrl()
                : globalBaseUrl;
        return DashScopeDecisionEndpoint.resolve(properties.getBaseUrl(), properties.getWorkspaceId(),
                properties.getRegion(), fallbackBaseUrl);
    }

    // ==================== 请求映射：DTO → DecisionPrompt ====================

    private DecisionPrompt toDecisionPrompt(DashScopeDecisionRequest request) {
        DecisionPrompt.Builder builder = DecisionPrompt.builder();
        Object state = request.getState();
        if (state instanceof Map<?, ?> map) {
            Map<String, Object> fields = new LinkedHashMap<>();
            map.forEach((key, value) -> fields.put(String.valueOf(key), value));
            builder.fields(fields);
        } else if (state instanceof List<?> list) {
            // System One 的 state 为数组时整体作为结构化对象送模型
            builder.fields(Map.of("items", list));
        } else {
            builder.text(String.valueOf(state));
        }
        Map<String, DecisionQuestion> questions = new LinkedHashMap<>();
        request.getQuestions().forEach((id, question) -> questions.put(id, toDecisionQuestion(id, question)));
        builder.questions(questions);
        String model = StringUtils.hasText(request.getModel()) ? request.getModel() : properties.getModel();
        if (StringUtils.hasText(model)) {
            builder.options(DecisionOptions.builder().model(model).build());
        }
        return builder.build();
    }

    private DecisionQuestion toDecisionQuestion(String id, DashScopeDecisionRequest.Question question) {
        Assert.notNull(question, () -> "question '" + id + "' must not be null");
        String type = StringUtils.hasText(question.getType()) ? question.getType() : "";
        switch (type) {
            case DashScopeDecisionRequest.Question.TYPE_CHOICE -> {
                ChoiceQuestion.Builder builder = ChoiceQuestion.builder().text(question.getInstructions());
                toStringMap(question.getCriteria(), "criteria of choice question '" + id + "'")
                        .forEach(builder::option);
                return builder.build();
            }
            case DashScopeDecisionRequest.Question.TYPE_NOUL -> {
                YesNoQuestion.Builder builder = YesNoQuestion.builder().text(question.getInstructions());
                Map<String, String> criteria = toStringMapOrNull(question.getCriteria());
                if (criteria != null) {
                    if (criteria.containsKey("true")) {
                        builder.whenYes(criteria.get("true"));
                    }
                    if (criteria.containsKey("false")) {
                        builder.whenNo(criteria.get("false"));
                    }
                }
                return builder.build();
            }
            case DashScopeDecisionRequest.Question.TYPE_SCORE -> {
                ScoreQuestion.Builder builder = ScoreQuestion.builder().text(question.getInstructions());
                for (String level : toStringList(question.getCriteria(), "criteria of score question '" + id + "'")) {
                    builder.level(level);
                }
                return builder.build();
            }
            default -> throw new IllegalArgumentException(
                    "question '" + id + "' has unsupported type '" + type + "'; expected choice, noul or score");
        }
    }

    // ==================== 响应映射：DecisionResponse → DTO ====================

    private DashScopeDecisionResponse toResponse(DecisionPrompt prompt, DecisionResponse response) {
        Assert.notNull(response, "decision response must not be null");
        DashScopeDecisionResponse result = new DashScopeDecisionResponse();
        if (response.getMetadata() != null) {
            result.setModel(response.getMetadata().getModel());
            result.setRequestId(response.getMetadata().getId());
            Usage usage = response.getMetadata().getUsage();
            if (usage != null) {
                result.setUsage(DashScopeDecisionResponse.Usage.builder()
                        .inputTokens(usage.getPromptTokens())
                        .outputTokens(usage.getCompletionTokens())
                        .totalTokens(usage.getTotalTokens())
                        .build());
            }
        }
        Map<String, DashScopeDecisionResponse.Answer> answers = new LinkedHashMap<>();
        prompt.getQuestions().forEach((id, question) -> {
            if (!response.hasAnswer(id)) {
                return;
            }
            DashScopeDecisionResponse.Answer answer = toAnswer(question, response, id);
            if (answer != null) {
                answers.put(id, answer);
            }
        });
        result.setAnswers(answers);
        return result;
    }

    private DashScopeDecisionResponse.Answer toAnswer(DecisionQuestion question, DecisionResponse response,
            String id) {
        DashScopeDecisionResponse.Answer answer = new DashScopeDecisionResponse.Answer();
        if (question instanceof ChoiceQuestion) {
            org.springframework.ai.decision.answer.ChoiceAnswer source = response.getChoice(id);
            answer.setType(DashScopeDecisionRequest.Question.TYPE_CHOICE);
            answer.setChoice(source.getValue());
            answer.setProbabilities(source.getProbabilities());
            answer.setConfidence(source.getConfidence());
            return answer;
        }
        if (question instanceof YesNoQuestion) {
            org.springframework.ai.decision.answer.YesNoAnswer source = response.getYesNo(id);
            answer.setType(DashScopeDecisionRequest.Question.TYPE_NOUL);
            answer.setNoul(source.getProbability());
            return answer;
        }
        if (question instanceof ScoreQuestion scoreQuestion) {
            org.springframework.ai.decision.answer.ScoreAnswer source = response.getScore(id);
            answer.setType(DashScopeDecisionRequest.Question.TYPE_SCORE);
            answer.setScore(source.getValue());
            answer.setConfidence(source.getConfidence());
            List<Double> probabilities = source.getProbabilities();
            if (probabilities != null) {
                Map<String, Double> distribution = new LinkedHashMap<>();
                for (int index = 0; index < probabilities.size(); index++) {
                    distribution.put(String.valueOf(index), probabilities.get(index));
                }
                answer.setProbabilities(distribution);
            }
            List<String> labels = scoreQuestion.getLevelLabels();
            if (labels != null) {
                Map<String, String> legend = new LinkedHashMap<>();
                for (int index = 0; index < labels.size(); index++) {
                    legend.put(String.valueOf(index), labels.get(index));
                }
                answer.setLegend(legend);
            }
            return answer;
        }
        return null;
    }

    private static Map<String, String> toStringMap(Object criteria, String message) {
        Map<String, String> result = toStringMapOrNull(criteria);
        Assert.notEmpty(result, message + " must be a non-empty object");
        return result;
    }

    private static Map<String, String> toStringMapOrNull(Object criteria) {
        if (!(criteria instanceof Map<?, ?> map) || map.isEmpty()) {
            return null;
        }
        Map<String, String> result = new LinkedHashMap<>();
        map.forEach((key, value) -> result.put(String.valueOf(key), value == null ? null : String.valueOf(value)));
        return result;
    }

    private static List<String> toStringList(Object criteria, String message) {
        if (!(criteria instanceof List<?> list) || list.isEmpty()) {
            throw new IllegalArgumentException(message + " must be a non-empty array");
        }
        List<String> result = new ArrayList<>();
        for (Object item : list) {
            result.add(String.valueOf(item));
        }
        return result;
    }
}
