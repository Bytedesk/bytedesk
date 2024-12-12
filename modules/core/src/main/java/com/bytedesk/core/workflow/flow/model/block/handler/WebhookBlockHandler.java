package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.RetryConfig;
import com.bytedesk.core.workflow.flow.model.block.model.options.WebhookBlockOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebhookBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    
    public WebhookBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String getType() {
        return BlockType.WEBHOOK.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        WebhookBlockOptions options = objectMapper.convertValue(block.getOptions(), WebhookBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            String processedUrl = processVariables(options.getUrl(), context);
            String processedBody = processVariables(options.getBody(), context);
            
            HttpHeaders headers = createHeaders(options.getHeaders(), context);
            HttpEntity<?> entity = createHttpEntity(processedBody, headers);
            
            ResponseEntity<String> response = executeRequest(
                processedUrl,
                HttpMethod.valueOf(options.getMethod()),
                entity,
                options.getTimeout(),
                options.getRetryConfig()
            );
            
            handleResponse(response, options, result);
            
        } catch (Exception e) {
            log.error("Webhook execution failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            WebhookBlockOptions options = objectMapper.convertValue(block.getOptions(), WebhookBlockOptions.class);
            return options.getUrl() != null && 
                   !options.getUrl().trim().isEmpty() &&
                   options.getMethod() != null &&
                   isValidHttpMethod(options.getMethod());
        } catch (Exception e) {
            return false;
        }
    }

    private HttpHeaders createHeaders(Map<String, String> headerMap, Map<String, Object> context) {
        HttpHeaders headers = new HttpHeaders();
        if (headerMap != null) {
            headerMap.forEach((key, value) -> headers.add(key, processVariables(value, context)));
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpEntity<?> createHttpEntity(String body, HttpHeaders headers) {
        return body != null ? new HttpEntity<>(body, headers) : new HttpEntity<>(headers);
    }

    private ResponseEntity<String> executeRequest(
        String url,
        HttpMethod method,
        HttpEntity<?> entity,
        Integer timeout,
        RetryConfig retryConfig
    ) throws InterruptedException {
        Exception lastException = null;
        int retryCount = 0;
        int maxRetries = retryConfig != null ? retryConfig.getLimit() : 3;
        int retryInterval = retryConfig != null ? retryConfig.getInterval() : 1000;

        while (retryCount < maxRetries) {
            try {
                return restTemplate.exchange(url, method, entity, String.class);
            } catch (Exception e) {
                lastException = e;
                log.warn("Webhook request failed (attempt {}): {}", retryCount + 1, e.getMessage());
                retryCount++;
                if (retryCount < maxRetries) {
                    TimeUnit.MILLISECONDS.sleep(retryInterval);
                }
            }
        }

        throw new RuntimeException("Failed after " + maxRetries + " retries", lastException);
    }

    private void handleResponse(
        ResponseEntity<String> response, 
        WebhookBlockOptions options,
        Map<String, Object> result
    ) {
        result.put("statusCode", response.getStatusCode().value());
        result.put("response", response.getBody());
        result.put("success", response.getStatusCode().is2xxSuccessful());
        
        if (options.getVariableId() != null && response.getBody() != null) {
            result.put(options.getVariableId(), response.getBody());
        }
    }

    private String processVariables(String template, Map<String, Object> context) {
        if (template == null) return null;
        
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            if (template.contains(placeholder)) {
                template = template.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        return template;
    }

    private boolean isValidHttpMethod(String method) {
        try {
            HttpMethod.valueOf(method.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
} 
