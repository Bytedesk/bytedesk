package com.bytedesk.call.xml_curl_trace;

import com.bytedesk.call.config.CallConstants;
import com.bytedesk.core.uid.UidUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import lombok.RequiredArgsConstructor;

/**
 * Persistent trace buffer for recent xml_curl requests.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch.xmlcurl", name = "enabled", havingValue = "true", matchIfMissing = false)
public class XmlCurlTraceService {

    private static final int MAX_TRACE_SIZE = 500;
    private final XmlCurlTraceRepository xmlCurlTraceRepository;
    private final ObjectMapper objectMapper;
    private final UidUtils uidUtils;

    public void record(Map<String, String> params,
            String section,
            String remote,
            String method,
            String uri,
            String queryString,
            boolean found,
            int responseSize,
            long costMs) {
        Map<String, String> keyFields = extractKeyFields(params);
        XmlCurlTraceEntity entity = XmlCurlTraceEntity.builder()
            .uid(uidUtils.getUid())
                .section(normalizeLower(section))
                .category(resolveCategory(section, params))
                .remote(safe(remote))
                .method(normalizeUpper(method))
                .uri(safe(uri))
                .query(safe(queryString))
                .found(found)
                .responseSize(responseSize)
                .costMs(costMs)
                .keyFields(writeJson(keyFields))
                .orgUid(resolveOrgUid(params, keyFields))
                .build();

        xmlCurlTraceRepository.save(entity);
        trimOverflow();
    }

    public List<XmlCurlTraceEntity> recent(int limit, String section, String category) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        String sectionFilter = normalizeLower(section);
        String categoryFilter = normalizeLower(category);

        if (StringUtils.hasText(sectionFilter) && StringUtils.hasText(categoryFilter)) {
            return xmlCurlTraceRepository
                    .findTop200BySectionAndCategoryAndDeletedFalseOrderByCreatedAtDesc(sectionFilter, categoryFilter)
                    .stream().limit(safeLimit).toList();
        }
        if (StringUtils.hasText(sectionFilter)) {
            return xmlCurlTraceRepository.findTop200BySectionAndDeletedFalseOrderByCreatedAtDesc(sectionFilter)
                    .stream().limit(safeLimit).toList();
        }
        if (StringUtils.hasText(categoryFilter)) {
            return xmlCurlTraceRepository.findTop200ByCategoryAndDeletedFalseOrderByCreatedAtDesc(categoryFilter)
                    .stream().limit(safeLimit).toList();
        }
        return xmlCurlTraceRepository.findTop200ByDeletedFalseOrderByCreatedAtDesc()
                .stream().limit(safeLimit).toList();
    }

    private static String resolveCategory(String section, Map<String, String> params) {
        String sectionValue = normalizeLower(section);
        if (CallConstants.DIRECTORY_NAME.equals(sectionValue)) {
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
            return java.util.Collections.emptyMap();
        }
        Map<String, String> keys = new LinkedHashMap<>();
        putIfPresent(keys, "section", pick(params, "section"));
        putIfPresent(keys, "user", pick(params, "user", "User", "variable_user_name", "Caller-Username"));
        putIfPresent(keys, "domain", pick(params, "domain", "Domain", "variable_domain_name", "sip_from_host"));
        putIfPresent(keys, "key_value", pick(params, "key_value", "Configuration-Name", "configuration", "name"));
        putIfPresent(keys, "context", pick(params, "Caller-Context", "context", "variable_context"));
        putIfPresent(keys, "destination",
                pick(params, "Caller-Destination-Number", "destination_number", "variable_destination_number"));
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

    private static String normalizeUpper(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private String writeJson(Map<String, String> keyFields) {
        try {
            return objectMapper.writeValueAsString(keyFields);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private String resolveOrgUid(Map<String, String> params, Map<String, String> keyFields) {
        String directOrgUid = pick(params,
                "orgUid", "org_uid", "Org-Uid", "variable_org_uid", "Caller-Org-Uid");
        if (StringUtils.hasText(directOrgUid)) {
            return directOrgUid;
        }
        String domain = pick(keyFields, "domain");
        if (CallConstants.DIRECTORY_DOMAIN_DEFAULT.equalsIgnoreCase(domain)) {
            return null;
        }
        return null;
    }

    private void trimOverflow() {
        long total = xmlCurlTraceRepository.count();
        if (total <= MAX_TRACE_SIZE) {
            return;
        }
        List<XmlCurlTraceEntity> rows = xmlCurlTraceRepository.findAll(org.springframework.data.domain.Sort
                .by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        rows.stream().skip(MAX_TRACE_SIZE).forEach(entity -> {
            entity.setDeleted(true);
            xmlCurlTraceRepository.save(entity);
        });
    }
}
