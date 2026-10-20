package com.bytedesk.ai.provider.ollama;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

class SpringAIOllamaChatControllerTest {

    private static final String VALID_RESPONSE = """
            {
              "model": "laya",
              "answers": {
                "intention": {
                  "type": "choice", "choice": "complaint",
                  "probabilities": {"consult": 0.05, "complaint": 0.91, "suggestion": 0.01, "other": 0.03},
                  "confidence": 0.88
                },
                "need_agent": {"type": "noul", "noul": 0.92},
                "urgency": {
                  "type": "score", "score": 1.2,
                  "legend": {"0": "No urgency stated", "1": "Wants timely attention", "2": "Critical"},
                  "probabilities": {"0": 0.1, "1": 0.6, "2": 0.3},
                  "confidence": 0.4
                }
              },
              "usage": {"input_tokens": 183, "output_tokens": 0}
            }
            """;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicInteger upstreamCalls = new AtomicInteger();
    private BytedeskProperties properties;
    private SpringAIOllamaChatController controller;
    private MockMvc mvc;
    private String upstreamBody;
    private HttpStatus upstreamStatus;
    private boolean unavailable;
    private String sentBody;
    private String sentUri;
    private String sentMethod;
    private MediaType sentContentType;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        properties = new BytedeskProperties();
        properties.setDebug(true);
        upstreamBody = VALID_RESPONSE;
        upstreamStatus = HttpStatus.OK;
        RestClient.Builder builder = RestClient.builder().requestInterceptor((request, body, execution) -> {
            upstreamCalls.incrementAndGet();
            sentUri = request.getURI().toString();
            sentMethod = request.getMethod().name();
            sentContentType = request.getHeaders().getContentType();
            sentBody = new String(body, StandardCharsets.UTF_8);
            if (unavailable) {
                throw new ResourceAccessException("test transport failure");
            }
            MockClientHttpResponse response = new MockClientHttpResponse(
                    upstreamBody.getBytes(StandardCharsets.UTF_8), upstreamStatus);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });
        controller = new SpringAIOllamaChatController(properties, mock(SpringAIOllamaChatService.class),
                builder, mock(ExecutorService.class));
        ReflectionTestUtils.setField(controller, "ollamaBaseUrl", "http://localhost:11434");
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void callsDecisionApiAndPreservesAllThreeAnswerTypes() throws Exception {
        mvc.perform(post("/ollama/laya")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"message":"We were billed twice. I need a human agent."}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.answers.intention.choice").value("complaint"))
                .andExpect(jsonPath("$.data.answers.intention.confidence").value(0.88))
                .andExpect(jsonPath("$.data.answers.need_agent.noul").value(0.92))
                .andExpect(jsonPath("$.data.answers.need_agent.confidence").doesNotExist())
                .andExpect(jsonPath("$.data.answers.urgency.score").value(1.2))
                .andExpect(jsonPath("$.data.usage.output_tokens").value(0));

        assertEquals(1, upstreamCalls.get());
        assertEquals("POST", sentMethod);
        assertEquals("http://localhost:11434/v1/systemone", sentUri);
        assertEquals(MediaType.APPLICATION_JSON, sentContentType);
        var payload = objectMapper.readTree(sentBody);
        assertEquals("laya", payload.path("model").asText());
        assertEquals("10m", payload.path("keep_alive").asText());
        assertEquals("We were billed twice. I need a human agent.", payload.path("state").asText());
        assertEquals(3, payload.path("questions").size());
        assertEquals("choice", payload.at("/questions/intention/type").asText());
        assertEquals(4, payload.at("/questions/intention/criteria").size());
        assertEquals("noul", payload.at("/questions/need_agent/type").asText());
        assertEquals("score", payload.at("/questions/urgency/type").asText());
        assertEquals(3, payload.at("/questions/urgency/criteria").size());
    }

    @Test
    void acceptsGetQueryAndStillUsesNativePostForInference() throws Exception {
        mvc.perform(get("/ollama/laya")
                .param("message", "We were billed twice. I need a human agent.")
                .param("baseUrl", "http://untrusted.invalid")
                .param("model", "another-model"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answers.intention.choice").value("complaint"))
                .andExpect(jsonPath("$.data.answers.need_agent.noul").value(0.92))
                .andExpect(jsonPath("$.data.answers.need_agent.confidence").doesNotExist())
                .andExpect(jsonPath("$.data.answers.urgency.score").value(1.2));
        assertEquals(1, upstreamCalls.get());
        assertEquals("POST", sentMethod);
        assertEquals("http://localhost:11434/v1/systemone", sentUri);
        var payload = objectMapper.readTree(sentBody);
        assertEquals("laya", payload.path("model").asText());
        assertEquals("We were billed twice. I need a human agent.", payload.path("state").asText());
    }

    @Test
    void refusesRequestsWhenDebugIsDisabled() throws Exception {
        properties.setDebug(false);
        mvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
        mvc.perform(get("/ollama/laya").param("message", "hello"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
        mvc.perform(get("/ollama/chat/sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Service is not available"));
        assertEquals(0, upstreamCalls.get());
    }

    @Test
    void rejectsGetWithoutMessage() throws Exception {
        mvc.perform(get("/ollama/laya"))
                .andExpect(status().isBadRequest());
        assertEquals(0, upstreamCalls.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    void rejectsBlankGetMessage(String message) throws Exception {
        mvc.perform(get("/ollama/laya").param("message", message))
                .andExpect(status().isBadRequest());
        assertEquals(0, upstreamCalls.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"message\":null}", "{\"message\":\"\"}", "{\"message\":\"   \"}", "invalid"})
    void rejectsInvalidInputBeforeCallingOllama(String body) throws Exception {
        mvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        assertEquals(0, upstreamCalls.get());
    }

    @Test
    void boundsMessageLength() throws Exception {
        mvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("message", "a".repeat(1001)))))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/ollama/laya").param("message", "a".repeat(1001)))
                .andExpect(status().isBadRequest());
        assertEquals(0, upstreamCalls.get());
    }

    @Test
    void acceptsMessageAtLengthLimit() throws Exception {
        mvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("message", "a".repeat(1000)))))
                .andExpect(status().isOk());
        mvc.perform(get("/ollama/laya").param("message", "a".repeat(1000)))
                .andExpect(status().isOk());
        assertEquals(2, upstreamCalls.get());
    }

    @Test
    void doesNotAllowClientToOverrideTheOllamaDestinationOrModel() throws Exception {
        mvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"message":"hello","baseUrl":"http://untrusted.invalid","model":"another-model"}
                        """))
                .andExpect(status().isOk());
        assertEquals("http://localhost:11434/v1/systemone", sentUri);
        assertEquals("laya", objectMapper.readTree(sentBody).path("model").asText());
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 500})
    void reportsUpstreamHttpErrorsWithoutLeakingResponseBody(int statusCode) throws Exception {
        upstreamStatus = HttpStatus.valueOf(statusCode);
        upstreamBody = "{\"error\":\"private upstream detail\"}";
        mvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\"}"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value(502))
                .andExpect(jsonPath("$.message")
                        .value("Ollama Laya request failed; check the model and Ollama version"));
        mvc.perform(get("/ollama/laya").param("message", "hello"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value(502));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "null", "{}", "{\"answers\":{}}", "{\"answers\":null}", "invalid json"})
    void rejectsEmptyOrMalformedDecisionResponses(String body) throws Exception {
        upstreamBody = body;
        mvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\"}"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value(502));
        mvc.perform(get("/ollama/laya").param("message", "hello"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value(502));
    }

    @Test
    void rejectsInvalidDecisionValues() throws Exception {
        upstreamBody = VALID_RESPONSE.replace("\"choice\": \"complaint\"", "\"choice\": null");
        mvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\"}"))
                .andExpect(status().isBadGateway());

        upstreamBody = VALID_RESPONSE.replace("\"noul\": 0.92", "\"noul\": 1.2");
        mvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\"}"))
                .andExpect(status().isBadGateway());

        upstreamBody = VALID_RESPONSE.replace("\"score\": 1.2", "\"score\": -1");
        mvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\"}"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void reportsConnectionFailuresAndTimeouts() throws Exception {
        unavailable = true;
        mvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value(503));
        mvc.perform(get("/ollama/laya").param("message", "hello"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value(503));
    }

    @Test
    void acceptsAnonymousGetAndPostWithMethodSecurityEnabled() throws Exception {
        ProxyFactory factory = new ProxyFactory(controller);
        factory.addAdvisor(AuthorizationManagerBeforeMethodInterceptor.preAuthorize());
        SpringAIOllamaChatController secured = (SpringAIOllamaChatController) factory.getProxy();
        MockMvc anonymousMvc = MockMvcBuilders.standaloneSetup(secured).build();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        anonymousMvc.perform(get("/ollama/laya").param("message", "hello"))
                .andExpect(status().isOk());
        anonymousMvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\"}"))
                .andExpect(status().isOk());
        assertEquals(2, upstreamCalls.get());
    }

    @Test
    @EnabledIfSystemProperty(named = "bytedesk.test.laya.live", matches = "true")
    void callsInstalledLocalLayaWhenExplicitlyEnabled() throws Exception {
        SpringAIOllamaChatController liveController = new SpringAIOllamaChatController(
                properties, mock(SpringAIOllamaChatService.class), RestClient.builder(), mock(ExecutorService.class));
        ReflectionTestUtils.setField(liveController, "ollamaBaseUrl", "http://127.0.0.1:11434");
        MockMvc liveMvc = MockMvcBuilders.standaloneSetup(liveController).build();
        liveMvc.perform(post("/ollama/laya").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"message":"We were billed twice. Please refund the duplicate. I need a human agent today."}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.model").isString())
                .andExpect(jsonPath("$.data.answers.intention.type").value("choice"))
                .andExpect(jsonPath("$.data.answers.need_agent.type").value("noul"))
                .andExpect(jsonPath("$.data.answers.need_agent.confidence").doesNotExist())
                .andExpect(jsonPath("$.data.answers.urgency.type").value("score"));
        liveMvc.perform(get("/ollama/laya")
                .param("message", "We were billed twice. Please refund the duplicate. I need a human agent today."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.model").isString())
                .andExpect(jsonPath("$.data.answers.intention.type").value("choice"))
                .andExpect(jsonPath("$.data.answers.need_agent.type").value("noul"))
                .andExpect(jsonPath("$.data.answers.need_agent.confidence").doesNotExist())
                .andExpect(jsonPath("$.data.answers.urgency.type").value("score"));
    }
}
