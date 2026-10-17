/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-10-16 11:50:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms of the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 * 联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.config;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.http.HttpHeaders;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

import lombok.extern.slf4j.Slf4j;

/**
 * Apache HttpClient 5.6.x 在懒读取响应体时透明解压（Brotli/gzip）存在缺陷：
 * DeepSeek 等返回 content-encoding: br 的接口经 RestClient 读取会被截断为 1 字节，
 * 导致 "Unexpected end-of-input: expected close marker for Object" JSON 解析失败。
 *
 * 对 DeepSeek 请求强制设置 Accept-Encoding: identity，要求服务端返回未压缩响应，
 * 从源头规避该缺陷。其他 RestClient 请求不受影响。
 * 如上游修复后可移除此配置。
 *
 * @see <a href="https://issues.apache.org/jira/browse/HTTPCLIENT-2430">HTTPCLIENT-2430</a>
 */
@Slf4j
@Component
public class AcceptEncodingIdentityRestClientCustomizer implements RestClientCustomizer {

    private final String deepSeekHost;

    public AcceptEncodingIdentityRestClientCustomizer(Environment environment) {
        String baseUrl = environment.getProperty(
                "spring.ai.deepseek.chat.base-url",
                environment.getProperty("spring.ai.deepseek.base-url", "https://api.deepseek.com"));
        String host = URI.create(baseUrl).getHost();
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("DeepSeek base URL must include a host");
        }
        this.deepSeekHost = host;
    }

    @Override
    public void customize(RestClient.Builder restClientBuilder) {
        customizeForHost(restClientBuilder, deepSeekHost);
    }

    public void customizeForBaseUrl(RestClient.Builder restClientBuilder, String baseUrl) {
        String host = URI.create(baseUrl).getHost();
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("DeepSeek base URL must include a host");
        }
        customizeForHost(restClientBuilder, host);
    }

    private void customizeForHost(RestClient.Builder restClientBuilder, String host) {
        restClientBuilder.requestInterceptor((request, body, execution) -> {
            String requestHost = request.getURI().getHost();
            if (requestHost != null && host.equalsIgnoreCase(requestHost)) {
                request.getHeaders().set(HttpHeaders.ACCEPT_ENCODING, "identity");
            }
            return execution.execute(request, body);
        });
        log.debug("DeepSeek RestClient Accept-Encoding=identity workaround enabled for host {}", host);
    }
}
