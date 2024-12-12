package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Map;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class WebhookBlockOptions extends BlockOptions {
    private String url;
    private String method;  // GET, POST, PUT, DELETE
    private Map<String, String> headers;
    private String body;
    private List<String> queryParams;
    private String variableId;  // 存储响应的变量
    private Integer timeout;
    private RetryConfig retryConfig;
}

