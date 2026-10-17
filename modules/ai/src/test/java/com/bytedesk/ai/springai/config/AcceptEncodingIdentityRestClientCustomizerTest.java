package com.bytedesk.ai.springai.config;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AcceptEncodingIdentityRestClientCustomizerTest {

    @Test
    void forcesIdentityEncodingForDeepSeekRequests() {
        RestClient.Builder builder = RestClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
        new AcceptEncodingIdentityRestClientCustomizer(new MockEnvironment()
                .withProperty("spring.ai.deepseek.base-url", "https://api.deepseek.com"))
                .customize(builder);
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://api.deepseek.com/health"))
                .andExpect(header(HttpHeaders.ACCEPT_ENCODING, "identity"))
                .andRespond(withSuccess("ok", MediaType.TEXT_PLAIN));

        String response = builder.clone().build().get()
                .uri("https://api.deepseek.com/health")
                .retrieve()
                .body(String.class);

        assertEquals("ok", response);
        server.verify();
    }

    @Test
    void forcesIdentityEncodingThroughDeepSeekApi() {
        String baseUrl = "https://custom.deepseek.example";
        RestClient.Builder builder = RestClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
        new AcceptEncodingIdentityRestClientCustomizer(new MockEnvironment()
                .withProperty("spring.ai.deepseek.base-url", "https://api.deepseek.com"))
                .customizeForBaseUrl(builder, baseUrl);
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(baseUrl + "/chat/completions"))
                .andExpect(header(HttpHeaders.ACCEPT_ENCODING, "identity"))
                .andRespond(withSuccess("""
                        {"id":"test","object":"chat.completion","created":1,"model":"deepseek-chat",
                         "choices":[{"index":0,"message":{"role":"assistant","content":"ok"},
                         "finish_reason":"stop"}]}
                        """, MediaType.APPLICATION_JSON));

        DeepSeekApi api = DeepSeekApi.builder()
                .baseUrl(baseUrl)
                .apiKey("test-api-key")
                .restClientBuilder(builder.clone())
                .webClientBuilder(WebClient.builder())
                .build();
        DeepSeekApi.ChatCompletionRequest request = DeepSeekApi.ChatCompletionRequest.builder()
                .model("deepseek-chat")
                .stream(false)
                .messages(List.of(new DeepSeekApi.ChatCompletionMessage(
                        "hello", DeepSeekApi.ChatCompletionMessage.Role.USER)))
                .build();

        var response = api.chatCompletionEntity(request);

        assertEquals("ok", response.getBody().choices().get(0).message().content());
        server.verify();
    }

    @Test
    void leavesOtherHostsCompressionUnchanged() {
        RestClient.Builder builder = RestClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
        new AcceptEncodingIdentityRestClientCustomizer(new MockEnvironment()
                .withProperty("spring.ai.deepseek.base-url", "https://api.deepseek.com"))
                .customize(builder);
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://example.test/health"))
                .andExpect(header(HttpHeaders.ACCEPT_ENCODING, "gzip"))
                .andRespond(withSuccess("ok", MediaType.TEXT_PLAIN));

        String response = builder.clone().build().get()
                .uri("https://example.test/health")
                .retrieve()
                .body(String.class);

        assertEquals("ok", response);
        server.verify();
    }
}
