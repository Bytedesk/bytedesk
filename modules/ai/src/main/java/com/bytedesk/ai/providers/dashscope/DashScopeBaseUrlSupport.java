package com.bytedesk.ai.providers.dashscope;

import java.net.URI;

import org.springframework.util.StringUtils;

public final class DashScopeBaseUrlSupport {

    public static final String DEFAULT_BASE_URL = "https://dashscope.aliyuncs.com/api/v1";

    private DashScopeBaseUrlSupport() {
    }

    public static String normalize(String baseUrl) {
        if (!StringUtils.hasText(baseUrl)) {
            return DEFAULT_BASE_URL;
        }

        String trimmed = trimTrailingSlash(baseUrl.trim());
        try {
            URI uri = URI.create(trimmed);
            String host = uri.getHost();
            if (host == null || !host.endsWith("dashscope.aliyuncs.com")) {
                return trimmed;
            }

            String path = uri.getPath();
            if (!StringUtils.hasText(path) || "/".equals(path)) {
                return defaultApiBase(uri);
            }

            if (path.equals("/api/v1") || path.startsWith("/api/v1/")) {
                return trimmed;
            }

            if (path.startsWith("/compatible-mode")
                    || path.equals("/v1")
                    || path.startsWith("/v1/")
                    || path.contains("/chat/completions")
                    || path.contains("/embeddings")) {
                return defaultApiBase(uri);
            }

            return defaultApiBase(uri);
        } catch (IllegalArgumentException ex) {
            return trimmed;
        }
    }

    private static String defaultApiBase(URI uri) {
        return uri.getScheme() + "://" + uri.getAuthority() + "/api/v1";
    }

    private static String trimTrailingSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}