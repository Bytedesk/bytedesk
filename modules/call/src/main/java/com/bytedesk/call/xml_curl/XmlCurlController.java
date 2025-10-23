package com.bytedesk.call.xml_curl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 最小可用的 mod_xml_curl HTTP 控制器。
 *
 * FreeSWITCH 配置 xml_curl.conf.xml 后，会以 HTTP 请求获取 directory 与 dialplan 的 XML。
 * 本控制器通过查询参数进行路由：
 * - type=directory&user=1000&domain=default
 * - type=dialplan&context=default&dest=1000
 *
 * 注意：生产环境需加入鉴权、白名单、限流与审计；此处为演示用途。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
// @ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "xmlcurl.enabled", havingValue = "true", matchIfMissing = false)
// @RequestMapping("/freeswitch/xmlcurl")
public class XmlCurlController {

    private final XmlCurlService xmlCurlService;

    @RequestMapping(value = {"/fs-xml", "/xmlcurl"}, method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/xml;charset=UTF-8")
    public @ResponseBody byte[] fsXml(@RequestParam MultiValueMap<String, String> paramsRaw, HttpServletRequest request) {
        // 记录请求来源与代理链（ngrok）信息，定位转发问题
        try {
            String remote = (request.getRemoteAddr() == null ? "" : request.getRemoteAddr()) + ":" + request.getRemotePort();
            String xff = safe(request.getHeader("X-Forwarded-For"));
            String xfp = safe(request.getHeader("X-Forwarded-Proto"));
            String xfh = safe(request.getHeader("X-Forwarded-Host"));
            String ua = truncate(safe(request.getHeader("User-Agent")), 160);
            String ct = safe(request.getContentType());
            String qs = safe(request.getQueryString());
            log.info("XML-CURL remote={} xff='{}' proto='{}' host='{}' ua='{}' ct='{}' qs='{}'", remote, xff, xfp, xfh, ua, ct, qs);
        } catch (Exception ignore) {}
        Map<String, String> p = normalize(paramsRaw);
        String section = p.getOrDefault("section", "").toLowerCase(Locale.ROOT);
        switch (section) {
            case "dialplan":
                return xmlCurlService.handleDialplan(p);
            case "directory":
                return xmlCurlService.handleDirectory(p);
            case "configuration":
                return xmlCurlService.handleConfiguration(p);
            case "phrases":
                return xmlCurlService.handlePhrases(p);
            default:
                return xmlCurlService.resultNotFound();
        }
    }

    // 健康检查（用于 ngrok/反向代理连通性排查）
    @GetMapping(value = "/_healthz", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String health() { return "OK"; }

    private static Map<String, String> normalize(MultiValueMap<String, String> raw) {
        Map<String, String> m = new HashMap<>();
        for (Map.Entry<String, java.util.List<String>> e : raw.entrySet()) {
            String k = e.getKey();
            String v = (e.getValue() != null && !e.getValue().isEmpty()) ? e.getValue().get(0) : "";
            try { v = URLDecoder.decode(v, StandardCharsets.UTF_8.name()); } catch (Exception ignore) {}
            m.put(k, v);
            m.putIfAbsent("variable_" + k, v);
            m.putIfAbsent(k.toUpperCase(Locale.ROOT), v);
        }
        return m;
    }

    private static String safe(String s) { return s == null ? "" : s; }
    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
