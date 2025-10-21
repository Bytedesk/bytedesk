package com.bytedesk.call.xml_curl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class XmlCurlAuthFilter extends OncePerRequestFilter {

    private final XmlCurlProperties properties;
    private final XmlCurlService xmlCurlService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String prefix = properties.getPathPrefix();
        return !properties.isEnabled() || (prefix != null && !path.startsWith(prefix));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Token 校验（若配置了 token）
        String configuredToken = properties.getToken();
        if (StringUtils.hasText(configuredToken)) {
            String token = request.getHeader("X-XMLCURL-TOKEN");
            if (!StringUtils.hasText(token)) {
                token = request.getParameter("token");
            }
            if (!configuredToken.equals(token)) {
                writeXml(response, HttpServletResponse.SC_UNAUTHORIZED,
                        xmlCurlService.buildError("unauthorized", "invalid token"));
                return;
            }
        }

        // IP 白名单（若配置）
        List<String> whitelist = properties.getIpWhitelist();
        if (whitelist != null && !whitelist.isEmpty()) {
            String ip = clientIp(request);
            if (ip == null || whitelist.stream().noneMatch(allowed -> allowed.equals(ip))) {
                log.warn("xmlcurl forbidden ip: {}", ip);
                writeXml(response, HttpServletResponse.SC_FORBIDDEN,
                        xmlCurlService.buildError("forbidden", "ip not allowed"));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private static void writeXml(HttpServletResponse response, int status, String xml) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.getWriter().write(xml);
    }

    private static String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
