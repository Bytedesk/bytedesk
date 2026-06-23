/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-20 12:00:00
 * @Description: 工单流程 - HTTP 请求委托
 *  用于在流程执行过程中调用外部 API
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 */
package com.bytedesk.ticket.process.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HTTP 请求委托 - TicketBuilder 生成 BPMN 中 http 节点对应的 JavaDelegate
 * <p>
 * BPMN 示例：
 * {@code <serviceTask id="xxx" name="HTTP请求" flowable:class="com.bytedesk.ticket.process.delegate.HttpDelegate" />}
 * </p>
 * <p>
 * 流程变量约定：
 * - httpMethod: String (GET/POST/PUT/PATCH/DELETE)
 * - httpUrl: String (请求 URL)
 * - httpHeaders: String (JSON 格式 headers)
 * - httpQueryParams: String (JSON 格式 query params)
 * - httpBody: String (请求体)
 * - httpResponseVariable: String (响应存入的变量名，默认 httpResponse)
 * </p>
 */
@Slf4j
@Component("httpDelegate")
@RequiredArgsConstructor
public class HttpDelegate implements JavaDelegate {

    private final RestTemplate restTemplate;

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        // String activityName = execution.getCurrentActivityName();

        // 读取流程变量
        String method = (String) execution.getVariable("httpMethod");
        String url = (String) execution.getVariable("httpUrl");
        // String headersJson = (String) execution.getVariable("httpHeaders");
        // String queryParamsJson = (String) execution.getVariable("httpQueryParams");
        String body = (String) execution.getVariable("httpBody");
        String responseVariable = (String) execution.getVariable("httpResponseVariable");

        if (method == null) method = "GET";
        if (responseVariable == null) responseVariable = "httpResponse";

        log.info("[HttpDelegate] processInstanceId={}, method={}, url={}",
                processInstanceId, method, url);

        if (url == null || url.isBlank()) {
            log.warn("[HttpDelegate] URL is empty, skipping HTTP call");
            execution.setVariable(responseVariable, "{}");
            return;
        }

        try {
            // 构建 URI (含 query params)
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
            HttpEntity<String> requestEntity = new HttpEntity<>(body, httpHeaders);

            ResponseEntity<String> response = restTemplate.exchange(
                    uriBuilder.build().toUri(),
                    httpMethod,
                    requestEntity,
                    String.class);

            String responseBody = response.getBody() != null ? response.getBody() : "{}";
            execution.setVariable(responseVariable, responseBody);

            log.info("[HttpDelegate] HTTP {} {} → status={}, responseLength={}",
                    method, url, response.getStatusCode(), responseBody.length());

        } catch (Exception e) {
            log.error("[HttpDelegate] HTTP call failed: method={}, url={}, error={}",
                    method, url, e.getMessage());
            execution.setVariable(responseVariable,
                    "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
            // 不抛出异常，避免流程中断
        }
    }
}
