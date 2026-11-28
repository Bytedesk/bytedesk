package com.bytedesk.ai.provider.aliyun.dashscope.decision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * 用本地 HTTP 服务模拟百炼 {@code /compatible-mode/v1/systemone}，验证：
 * URL 拼接（{base}/systemone）、Bearer 鉴权、请求体结构、官方返回示例的答案映射、provider 凭据解析。
 * https://docs.bailian.console.aliyun.com/zh/model-studio/decision-model-api
 */
class DashScopeDecisionServiceTest {

    /** 官方「工单分流示例」返回（节选）。 */
    private static final String DOCUMENTED_RESPONSE = """
            {
              "model": "decision-model",
              "request_id": "7b986c65-b223-9341-b5f0-b988e27ecaac",
              "answers": {
                "department": {
                  "type": "choice",
                  "choice": "billing",
                  "confidence": 0.88,
                  "probabilities": {"billing": 0.94, "technical": 0.06}
                },
                "escalate": {
                  "type": "noul",
                  "noul": 0.99
                },
                "severity": {
                  "type": "score",
                  "score": 2.25,
                  "confidence": 0.91,
                  "legend": {
                    "0": "轻微问题，不影响功能",
                    "1": "部分功能受影响，但存在替代方案",
                    "2": "核心功能不可用，没有替代方案",
                    "3": "造成严重业务或安全影响"
                  },
                  "probabilities": {"0": 0.0, "1": 0.01, "2": 0.73, "3": 0.26}
                }
              },
              "usage": {"input_tokens": 125},
              "latency_ms": 52.9
            }
            """;

    private HttpServer server;

    private final List<RecordedRequest> requests = new CopyOnWriteArrayList<>();

    private final ObjectMapper mapper = new ObjectMapper();

    private volatile int responseStatus = HttpStatus.OK.value();

    private volatile String responseBody = DOCUMENTED_RESPONSE;

    private LlmProviderRestService llmProviderRestService;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", this::handle);
        server.start();
        llmProviderRestService = mock(LlmProviderRestService.class);
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void routeTicketMapsDocumentedAnswersAndPostsToSystemOneUnderCompatibleMode() throws Exception {
        // provider baseUrl 为本地业务空间网关风格的 /api/v1，期望被改写为 /compatible-mode/v1
        LlmProviderEntity provider = LlmProviderEntity.builder()
                .apiKey("sk-provider-key")
                .baseUrl(baseUrl() + "/api/v1")
                .build();
        when(llmProviderRestService.findByUid("dashscope-local")).thenReturn(Optional.of(provider));
        DashScopeDecisionProperties properties = new DashScopeDecisionProperties();
        DashScopeDecisionService service = newService(properties);

        DashScopeDecisionResponse response = service.routeTicket(
                DashScopeDecisionResponse.TicketRoutingDemoRequest.builder()
                        .ticketId("T-1001")
                        .content("订单支付后超过 24 小时仍未到账，用户无法继续使用核心服务。")
                        .providerUid("dashscope-local")
                        .build());

        // 请求：路径、鉴权、body 结构
        assertEquals(1, requests.size());
        RecordedRequest recorded = requests.get(0);
        assertEquals("POST", recorded.method);
        assertEquals("/compatible-mode/v1/systemone", recorded.path);
        assertEquals("Bearer sk-provider-key", recorded.authorization);
        JsonNode body = mapper.readTree(recorded.body);
        assertEquals("decision-model", body.get("model").asText());
        assertEquals("T-1001", body.get("state").get("ticket_id").asText());
        assertEquals("choice", body.get("questions").get("department").get("type").asText());
        assertEquals("支付、退款和账单问题",
                body.get("questions").get("department").get("criteria").get("billing").asText());
        assertEquals("noul", body.get("questions").get("escalate").get("type").asText());
        assertEquals("score", body.get("questions").get("severity").get("type").asText());
        assertEquals(4, body.get("questions").get("severity").get("criteria").size());

        // 响应：与官方返回示例字段一致
        assertEquals("decision-model", response.getModel());
        assertEquals("7b986c65-b223-9341-b5f0-b988e27ecaac", response.getRequestId());
        assertNotNull(response.getDurationMs());
        assertEquals(Integer.valueOf(125), response.getUsage().getInputTokens());

        DashScopeDecisionResponse.Answer department = response.getAnswers().get("department");
        assertEquals("choice", department.getType());
        assertEquals("billing", department.getChoice());
        assertEquals(0.88, department.getConfidence());
        assertEquals(0.94, department.getProbabilities().get("billing"));

        DashScopeDecisionResponse.Answer escalate = response.getAnswers().get("escalate");
        assertEquals("noul", escalate.getType());
        assertEquals(0.99, escalate.getNoul());
        assertNull(escalate.getConfidence(), "noul 类型不返回置信度");

        DashScopeDecisionResponse.Answer severity = response.getAnswers().get("severity");
        assertEquals("score", severity.getType());
        assertEquals(2.25, severity.getScore());
        assertEquals(0.73, severity.getProbabilities().get("2"));
        assertEquals("核心功能不可用，没有替代方案", severity.getLegend().get("2"));
    }

    @Test
    void globalConfigFallbackAndWorkspaceResolutionAreReflectedInEndpoint() {
        DashScopeDecisionProperties properties = new DashScopeDecisionProperties();
        DashScopeDecisionService service = newService(properties);

        // 未配置 provider：回退全局 base-url（本测试传入的是裸域名）
        assertEquals(baseUrl() + "/compatible-mode/v1", service.resolveEndpoint(null));

        // workspace-id 优先于 base-url 回退
        properties.setWorkspaceId("llm-abc");
        assertEquals("https://llm-abc.cn-beijing.maas.aliyuncs.com/compatible-mode/v1",
                service.resolveEndpoint(null));

        // 显式 base-url 优先级最高
        properties.setBaseUrl(baseUrl() + "/compatible-mode/v1");
        assertEquals(baseUrl() + "/compatible-mode/v1", service.resolveEndpoint(null));
    }

    @Test
    void usesGlobalApiKeyWhenProviderHasNoKey() throws Exception {
        LlmProviderEntity provider = LlmProviderEntity.builder().baseUrl(baseUrl()).build();
        when(llmProviderRestService.findByUid(anyString())).thenReturn(Optional.of(provider));
        DashScopeDecisionService service = newService(new DashScopeDecisionProperties());
        // 单问 noul 的返回，与请求问题一一对应
        responseBody = """
                {"model":"decision-model","request_id":"req-noul","answers":{"escalate":{"type":"noul","noul":0.87}},
                 "usage":{"input_tokens":42},"latency_ms":12.5}
                """;

        DashScopeDecisionResponse response = service.decide(DashScopeDecisionRequest.builder()
                .state("订单支付后超过 24 小时仍未到账")
                .providerUid("dashscope-local")
                .questions(Map.of("escalate", DashScopeDecisionRequest.Question.noul("是否需要立即通知值班人员？")))
                .build());

        assertEquals(1, requests.size());
        assertEquals("Bearer sk-global-key", requests.get(0).authorization);
        assertEquals("/compatible-mode/v1/systemone", requests.get(0).path);
        JsonNode body = mapper.readTree(requests.get(0).body);
        assertTrue(body.get("state").isTextual());
        assertEquals("订单支付后超过 24 小时仍未到账", body.get("state").asText());
        assertEquals("req-noul", response.getRequestId());
        assertEquals(0.87, response.getAnswers().get("escalate").getNoul());
        assertEquals(Integer.valueOf(42), response.getUsage().getInputTokens());
        assertEquals(1, response.getAnswers().size(), "只返回请求中的问题");
    }

    @Test
    void mapsApiErrorToSystemOneException() {
        responseStatus = HttpStatus.UNAUTHORIZED.value();
        responseBody = "{\"error\":{\"message\":\"Invalid API-key provided.\",\"type\":\"authentication_error\"}}";
        DashScopeDecisionService service = newService(new DashScopeDecisionProperties());

        org.springframework.ai.systemone.api.exception.SystemOneApiException exception = org.junit.jupiter.api.Assertions
                .assertThrows(org.springframework.ai.systemone.api.exception.SystemOneApiException.class,
                        () -> service.decide(DashScopeDecisionRequest.builder()
                                .state("hello")
                                .questions(Map.of("escalate",
                                        DashScopeDecisionRequest.Question.noul("是否需要升级？")))
                                .build()));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.status());
        assertTrue(exception instanceof org.springframework.ai.systemone.api.exception.SystemOneAuthenticationException,
                "401 应映射为鉴权异常，便于 Controller 提示检查 api key");
    }

    @Test
    void missingApiKeyIsRejectedBeforeCallingEndpoint() {
        DashScopeDecisionService service = new DashScopeDecisionService(llmProviderRestService,
                new DashScopeDecisionProperties(), "", baseUrl(), null);
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.decide(DashScopeDecisionRequest.builder()
                        .state("hello")
                        .questions(Map.of("escalate", DashScopeDecisionRequest.Question.noul("是否升级？")))
                        .build()));
        assertTrue(exception.getMessage().contains("apiKey"));
        assertTrue(requests.isEmpty());
    }

    private DashScopeDecisionService newService(DashScopeDecisionProperties properties) {
        return new DashScopeDecisionService(llmProviderRestService, properties, "sk-global-key", baseUrl(), null);
    }

    private String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    private void handle(HttpExchange exchange) throws IOException {
        String body;
        try (InputStream in = exchange.getRequestBody()) {
            body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        requests.add(new RecordedRequest(exchange.getRequestMethod(), exchange.getRequestURI().getPath(),
                exchange.getRequestHeaders().getFirst("Authorization"), body));
        byte[] payload = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(responseStatus, payload.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(payload);
        }
    }

    private record RecordedRequest(String method, String path, String authorization, String body) {
    }
}
