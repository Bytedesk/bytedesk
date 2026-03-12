package com.bytedesk.call.xml_curl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * In-memory trace buffer for recent xml_curl requests.
 */
@Service
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch.xmlcurl", name = "enabled", havingValue = "true", matchIfMissing = false)
public class XmlCurlTraceService {

    private static final int MAX_TRACE_SIZE = 500;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);

    private final ConcurrentLinkedDeque<Map<String, Object>> traces = new ConcurrentLinkedDeque<>();

    public void record(Map<String, String> params,
                       String section,
                       String remote,
                       String method,
                       String uri,
                       String queryString,
                       boolean found,
                       int responseSize,
                       long costMs) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("timestamp", TIME_FMT.format(Instant.now()));
        row.put("section", normalizeLower(section));
        row.put("category", resolveCategory(section, params));
        row.put("remote", safe(remote));
        row.put("method", safe(method));
        row.put("uri", safe(uri));
        row.put("query", safe(queryString));
        row.put("found", found);
        row.put("responseSize", responseSize);
        row.put("costMs", costMs);
        row.put("keys", extractKeyFields(params));

        traces.addFirst(row);
        while (traces.size() > MAX_TRACE_SIZE) {
            traces.pollLast();
        }
    }

    public List<Map<String, Object>> recent(int limit, String section, String category) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        String sectionFilter = normalizeLower(section);
        String categoryFilter = normalizeLower(category);

        List<Map<String, Object>> out = new ArrayList<>(safeLimit);
        for (Map<String, Object> item : traces) {
            if (!matches(item.get("section"), sectionFilter)) {
                continue;
            }
            if (!matches(item.get("category"), categoryFilter)) {
                continue;
            }
            out.add(item);
            if (out.size() >= safeLimit) {
                break;
            }
        }
        return out;
    }

    private static boolean matches(Object value, String filter) {
        if (!StringUtils.hasText(filter)) {
            return true;
        }
        String v = value == null ? "" : String.valueOf(value).toLowerCase(Locale.ROOT);
        return v.equals(filter);
    }

    private static String resolveCategory(String section, Map<String, String> params) {
        String sectionValue = normalizeLower(section);
        if ("directory".equals(sectionValue)) {
            boolean registerLookup = hasAny(params,
                    "action", "sip_auth_method", "sip_auth_username", "key", "purpose", "event_name");
            return registerLookup ? "load_registered_extension" : "load_extension";
        }
        if ("configuration".equals(sectionValue)) {
            String cfg = pick(params, "key_value", "configuration", "name", "Configuration-Name");
            if (StringUtils.hasText(cfg) && cfg.toLowerCase(Locale.ROOT).contains("cdr")) {
                return "load_cdr";
            }
            return "load_configuration";
        }
        if ("dialplan".equals(sectionValue)) {
            return "load_dialplan";
        }
        if ("phrases".equals(sectionValue)) {
            return "load_phrases";
        }
        return "other";
    }

    private static Map<String, String> extractKeyFields(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> keys = new LinkedHashMap<>();
        putIfPresent(keys, "section", pick(params, "section"));
        putIfPresent(keys, "user", pick(params, "user", "User", "variable_user_name", "Caller-Username"));
        putIfPresent(keys, "domain", pick(params, "domain", "Domain", "variable_domain_name", "sip_from_host"));
        putIfPresent(keys, "key_value", pick(params, "key_value", "Configuration-Name", "configuration", "name"));
        putIfPresent(keys, "context", pick(params, "Caller-Context", "context", "variable_context"));
        putIfPresent(keys, "destination", pick(params, "Caller-Destination-Number", "destination_number", "variable_destination_number"));
        putIfPresent(keys, "action", pick(params, "action", "sip_auth_method", "Event-Name"));
        return keys;
    }

    private static void putIfPresent(Map<String, String> out, String key, String value) {
        if (StringUtils.hasText(value)) {
            out.put(key, value);
        }
    }

    private static boolean hasAny(Map<String, String> params, String... keys) {
        if (params == null || params.isEmpty()) {
            return false;
        }
        for (String key : keys) {
            String val = params.get(key);
            if (StringUtils.hasText(val)) {
                return true;
            }
        }
        return false;
    }

    private static String pick(Map<String, String> params, String... keys) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        for (String key : keys) {
            String value = params.get(key);
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private static String normalizeLower(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
