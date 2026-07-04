package com.bytedesk.ai.mcp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(BytedeskMcpAuthProperties.class)
@ConditionalOnProperty(prefix = "spring.ai.mcp.server", name = "enabled", havingValue = "true")
public class BytedeskMcpBearerTokenFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final BytedeskMcpAuthProperties properties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !isMcpEndpoint(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String expectedToken = normalizeToken(properties.getBearerToken());
        if (!StringUtils.hasText(expectedToken)) {
            reject(response, HttpStatus.SERVICE_UNAVAILABLE, "MCP bearer token is not configured");
            return;
        }

        String actualToken = normalizeAuthorization(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (!constantTimeEquals(expectedToken, actualToken)) {
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
            reject(response, HttpStatus.UNAUTHORIZED, "Invalid MCP bearer token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isMcpEndpoint(String requestUri) {
        return matchesEndpoint(requestUri, properties.getSseEndpoint())
                || matchesEndpoint(requestUri, properties.getMessageEndpoint());
    }

    private boolean matchesEndpoint(String requestUri, String endpoint) {
        if (!StringUtils.hasText(endpoint)) {
            return false;
        }
        return requestUri.equals(endpoint) || requestUri.startsWith(endpoint + "/");
    }

    private String normalizeAuthorization(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        String normalized = authorization.trim();
        if (normalized.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return normalizeToken(normalized.substring(BEARER_PREFIX.length()));
        }
        return null;
    }

    private String normalizeToken(String token) {
        return StringUtils.hasText(token) ? token.trim() : null;
    }

    private boolean constantTimeEquals(String expectedToken, String actualToken) {
        if (!StringUtils.hasText(actualToken)) {
            return false;
        }
        return MessageDigest.isEqual(
                expectedToken.getBytes(StandardCharsets.UTF_8),
                actualToken.getBytes(StandardCharsets.UTF_8));
    }

    private void reject(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\"}");
    }
}
