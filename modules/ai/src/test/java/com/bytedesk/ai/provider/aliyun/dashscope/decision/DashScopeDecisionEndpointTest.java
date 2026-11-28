package com.bytedesk.ai.provider.aliyun.dashscope.decision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 决策模型端点解析规则：显式 base-url > workspace-id > DashScope base-url 回退。
 * https://docs.bailian.console.aliyun.com/zh/model-studio/decision-model-api
 */
class DashScopeDecisionEndpointTest {

    @Test
    void explicitBaseUrlWinsAndTrailingSlashIsTrimmed() {
        assertEquals("https://proxy.example.com/compatible-mode/v1",
                DashScopeDecisionEndpoint.resolve("https://proxy.example.com/compatible-mode/v1/", "ws-ignored",
                        "cn-beijing", "https://ignored.example.com/api/v1"));
    }

    @Test
    void workspaceIdBuildsWorkspaceGatewayUrlWithDefaultRegion() {
        assertEquals("https://llm-abc.cn-beijing.maas.aliyuncs.com/compatible-mode/v1",
                DashScopeDecisionEndpoint.resolve(null, "llm-abc", null, null));
        assertEquals("https://llm-abc.ap-southeast-1.maas.aliyuncs.com/compatible-mode/v1",
                DashScopeDecisionEndpoint.resolve(null, "llm-abc", "ap-southeast-1", null));
    }

    @Test
    void fallsBackToDashScopeBaseUrlAndRewritesNativeApiPath() {
        // 本地业务空间网关配置为 .../api/v1，决策模型需要 compatible-mode
        assertEquals("https://llm-l33c9v25gxaavg8k.cn-beijing.maas.aliyuncs.com/compatible-mode/v1",
                DashScopeDecisionEndpoint.resolve(null, null, null,
                        "https://llm-l33c9v25gxaavg8k.cn-beijing.maas.aliyuncs.com/api/v1"));
        // 共享域名
        assertEquals("https://dashscope.aliyuncs.com/compatible-mode/v1",
                DashScopeDecisionEndpoint.resolve(null, null, null, "https://dashscope.aliyuncs.com"));
        assertEquals("https://dashscope.aliyuncs.com/compatible-mode/v1",
                DashScopeDecisionEndpoint.fromDashScopeBaseUrl("https://dashscope.aliyuncs.com/api/v1/"));
    }

    @Test
    void keepsExistingCompatibleModeOrV1BaseUrlsUntouched() {
        assertEquals("https://dashscope.aliyuncs.com/compatible-mode/v1",
                DashScopeDecisionEndpoint.fromDashScopeBaseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1"));
        assertEquals("https://dashscope.aliyuncs.com/compatible-mode/v1",
                DashScopeDecisionEndpoint.fromDashScopeBaseUrl("https://dashscope.aliyuncs.com/compatible-mode"));
    }

    @Test
    void returnsNullWhenNothingConfigured() {
        assertNull(DashScopeDecisionEndpoint.resolve(null, "  ", null, " "));
    }

    @Test
    void questionFactoriesProduceOfficialFieldLayout() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(java.util.Map.of(
                "department", DashScopeDecisionRequest.Question.choice("应该由哪个团队处理？",
                        java.util.Map.of("billing", "支付、退款和账单问题")),
                "escalate", DashScopeDecisionRequest.Question.noul("是否需要立即通知值班人员？"),
                "severity", DashScopeDecisionRequest.Question.score("这个问题有多严重？",
                        java.util.List.of("轻微", "严重"))));
        java.util.Map<String, java.util.Map<String, Object>> questions = mapper.readValue(json,
                new TypeReference<java.util.Map<String, java.util.Map<String, Object>>>() {
                });
        assertEquals("choice", questions.get("department").get("type"));
        assertEquals(java.util.Map.of("billing", "支付、退款和账单问题"),
                questions.get("department").get("criteria"));
        assertEquals("noul", questions.get("escalate").get("type"));
        assertTrue(!questions.get("escalate").containsKey("criteria"));
        assertEquals("score", questions.get("severity").get("type"));
        assertEquals(java.util.List.of("轻微", "严重"), questions.get("severity").get("criteria"));
    }

    @Test
    void ticketDemoBuildsDocumentedStateAndThreeQuestions() {
        DashScopeDecisionResponse.TicketRoutingDemoRequest request = DashScopeDecisionResponse.TicketRoutingDemoRequest
                .builder().ticketId("T-1001").content("订单支付后超过 24 小时仍未到账").build();
        assertEquals(java.util.Map.of("ticket_id", "T-1001", "content", "订单支付后超过 24 小时仍未到账"),
                request.toState());
        assertEquals(java.util.List.of("department", "escalate", "severity"),
                java.util.List.copyOf(request.toQuestions().keySet()));
        assertEquals("choice", request.toQuestions().get("department").getType());
        assertEquals("noul", request.toQuestions().get("escalate").getType());
        assertEquals("score", request.toQuestions().get("severity").getType());
    }

    @Test
    void rejectsUnsupportedQuestionType() {
        DashScopeDecisionService service = new DashScopeDecisionService(null, new DashScopeDecisionProperties(),
                "sk-test", "https://dashscope.aliyuncs.com", null);
        DashScopeDecisionRequest request = DashScopeDecisionRequest.builder()
                .state("hello")
                .questions(java.util.Map.of("bad", DashScopeDecisionRequest.Question.builder().type("unknown")
                        .build()))
                .build();
        assertThrows(IllegalArgumentException.class, () -> service.decide(request));
    }
}
