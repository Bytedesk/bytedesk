package com.bytedesk.call.xml_curl;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * xml_curl 配置项。
 */
@Data
@ConfigurationProperties(prefix = "bytedesk.call.freeswitch.xmlcurl")
public class XmlCurlProperties {
    /**
     * 是否启用 xmlcurl 控制器。
     * 注意：控制器本身也通过 @ConditionalOnProperty 控制。
     */
    private boolean enabled = false;

    /**
     * 访问令牌。若非空，将校验请求头 X-XMLCURL-TOKEN 或查询参数 token。
     */
    private String token;

    /**
     * IP 白名单；若非空，仅允许来自这些 IP（或经 X-Forwarded-For 解析的最左 IP）。
     */
    private List<String> ipWhitelist = Collections.emptyList();

    /**
     * 路径前缀，仅用于过滤器匹配；控制器映射为固定路径 /freeswitch/xmlcurl。
     */
    private String pathPrefix = "/freeswitch/xmlcurl";
}
