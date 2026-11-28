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

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.ai.systemone.api.exception.SystemOneApiConnectionException;
import org.springframework.ai.systemone.api.exception.SystemOneApiException;
import org.springframework.ai.systemone.api.exception.SystemOneAuthenticationException;
import org.springframework.ai.systemone.api.exception.SystemOneNotFoundException;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云百炼决策模型接口（{@code /dashscope/decision/**}）。
 *
 * <p>与 {@code /ollama/decision/**} 的区别：Ollama 走本地 d1-omni，这里走百炼
 * {@code POST /compatible-mode/v1/systemone}（decision-model），但两者共用 Spring AI
 * {@code DecisionModel} 抽象。接口默认受 {@code bytedesk.debug} 保护，仅用于联调验证；
 * 业务侧请直接注入 {@link DashScopeDecisionService}。</p>
 *
 * https://docs.bailian.console.aliyun.com/zh/model-studio/decision-model-api
 */
@Slf4j
@RestController
@RequestMapping(value = "/dashscope/decision", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.dashscope.decision", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DashScopeDecisionController {

    private final BytedeskProperties bytedeskProperties;

    private final DashScopeDecisionService dashScopeDecisionService;

    /**
     * 通用决策：一次请求携带 state + 若干 choice / noul / score 问题。
     *
     * <p>GET 通过查询参数绑定（{@code questions[<id>].type / instructions}），但 {@code criteria} 为
     * {@code Object}，无法从查询参数绑定，因此 GET 仅支持 noul 问题；choice / score 请走 POST JSON。</p>
     *
     * <p>GET 示例（查询参数需 URL 编码，中文直接放在地址栏可能被服务端错误解析）：<br>
     * {@code http://127.0.0.1:9003/dashscope/decision?state=订单支付后超过24小时未到账&model=decision-model&questions[need_human].type=noul&questions[need_human].instructions=是否需要人工介入处理？
     * }</p>
     */
    @GetMapping
    public ResponseEntity<JsonResult<?>> decideGet(@Valid @ModelAttribute DashScopeDecisionRequest request) {
        return execute(request);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonResult<?>> decide(@Valid @RequestBody DashScopeDecisionRequest request) {
        return execute(request);
    }

    /**
     * 工单分流示例：派单团队 choice + 是否升级 noul + 严重度 score，一次前向返回三项决策。
     * {@code questions} 由服务端固定生成，因此 GET 不需要（也不接受）{@code questions} 参数。
     * http://127.0.0.1:9003/dashscope/decision/ticket?content=%E8%AE%A2%E5%8D%95%E6%94%AF%E4%BB%98%E5%90%8E%E8%B6%85%E8%BF%8724%E5%B0%8F%E6%97%B6%E4%BB%8D%E6%9C%AA%E5%88%B0%E8%B4%A6%EF%BC%8C%E7%94%A8%E6%88%B7%E6%97%A0%E6%B3%95%E7%BB%A7%E7%BB%AD%E4%BD%BF%E7%94%A8%E6%A0%B8%E5%BF%83%E6%9C%8D%E5%8A%A1%E3%80%82
     */
    @GetMapping("/ticket")
    public ResponseEntity<JsonResult<?>> routeTicketGet(
            @Valid @ModelAttribute DashScopeDecisionResponse.TicketRoutingDemoRequest request) {
        return routeTicket(request);
    }

    @PostMapping(value = "/ticket", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonResult<?>> routeTicket(
            @Valid @RequestBody DashScopeDecisionResponse.TicketRoutingDemoRequest request) {
        if (!isDebugEnabled()) {
            return serviceUnavailable();
        }
        try {
            return ResponseEntity.ok(JsonResult.success(dashScopeDecisionService.routeTicket(request)));
        } catch (SystemOneApiException | SystemOneApiConnectionException | ResourceAccessException e) {
            return handleSystemOneError(e);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("DashScope decision ticket demo rejected: {}", e.getMessage());
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }

    /**
     * 是非判断示例：单问 noul，返回 P(yes)。{@code text} / {@code instructions} 均有默认值，无参访问也可运行。
     * http://127.0.0.1:9003/dashscope/decision/noul?text=客户明确表示要投诉到监管部门
     */
    @GetMapping("/noul")
    public ResponseEntity<JsonResult<?>> decideNoul(
            @RequestParam(value = "text", defaultValue = "客户明确表示要投诉到监管部门。") String text,
            @RequestParam(value = "instructions", defaultValue = "是否需要立即升级给人工客服？") String instructions,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "providerUid", required = false) String providerUid) {
        return execute(DashScopeDecisionRequest.builder()
                .model(model)
                .providerUid(providerUid)
                .state(text)
                .questions(Map.of("noul", DashScopeDecisionRequest.Question.noul(instructions)))
                .build());
    }

    /**
     * 当前生效的决策模型端点与模型，便于排查 workspace / base-url 配置。
     * http://127.0.0.1:9003/dashscope/decision/config
     */
    @GetMapping("/config")
    public ResponseEntity<JsonResult<?>> config(
            @RequestParam(value = "providerUid", required = false) String providerUid) {
        Map<String, String> config = Map.of(
                "provider", "dashscope",
                "endpoint", String.valueOf(dashScopeDecisionService.resolveEndpoint(providerUid)),
                "systemOnePath", "/systemone");
        return ResponseEntity.ok(JsonResult.success(config));
    }

    private ResponseEntity<JsonResult<?>> execute(DashScopeDecisionRequest request) {
        if (!isDebugEnabled()) {
            return serviceUnavailable();
        }
        try {
            return ResponseEntity.ok(JsonResult.success(dashScopeDecisionService.decide(request)));
        } catch (SystemOneApiException | SystemOneApiConnectionException | ResourceAccessException e) {
            return handleSystemOneError(e);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("DashScope decision request rejected: {}", e.getMessage());
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }

    private ResponseEntity<JsonResult<?>> handleSystemOneError(Exception e) {
        if (e instanceof SystemOneAuthenticationException) {
            log.warn("DashScope decision authentication failed (401); check spring.ai.dashscope.api-key");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(JsonResult.error("DashScope decision authentication failed; check the api key", 502));
        }
        if (e instanceof SystemOneNotFoundException) {
            log.warn("DashScope decision model not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(JsonResult.error("DashScope decision model not found; check the model name", 502));
        }
        if (e instanceof SystemOneApiConnectionException || e instanceof ResourceAccessException) {
            log.warn("DashScope decision connection failed or timed out: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(JsonResult.error("DashScope decision is unavailable or timed out", 503));
        }
        if (e instanceof SystemOneApiException apiException) {
            log.warn("DashScope decision request failed: status={}", apiException.status());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(JsonResult.error("DashScope decision request failed: " + e.getMessage(), 502));
        }
        log.warn("DashScope decision response could not be decoded", e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(JsonResult.error("DashScope decision returned an unreadable response", 502));
    }

    private boolean isDebugEnabled() {
        return Boolean.TRUE.equals(bytedeskProperties.getDebug());
    }

    private ResponseEntity<JsonResult<?>> serviceUnavailable() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(JsonResult.error("Service is not available", 403));
    }
}
