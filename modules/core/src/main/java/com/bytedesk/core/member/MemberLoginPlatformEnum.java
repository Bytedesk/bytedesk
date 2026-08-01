package com.bytedesk.core.member;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.BytedeskConsts;

public enum MemberLoginPlatformEnum {

    ADMIN("admin"),
    DESKTOP("desktop"),
    NOTEBASE("notebase"),
    WORKFLOW("workflow"),
    CALL("call"),
    CALL_ADMIN("callAdmin");

        private static final Set<String> IMPORT_ALL_ALIASES = Set.of(
            "all",
            "全部",
            "全部平台",
            "全平台",
            "所有",
            "所有平台");

        private static final Map<String, String> IMPORT_ALIASES = createImportAliases();

    private final String code;

    MemberLoginPlatformEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static boolean isSupported(String code) {
        if (code == null) {
            return false;
        }
        for (MemberLoginPlatformEnum platform : values()) {
            if (platform.code.equals(code)) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> allCodes() {
        LinkedHashSet<String> codes = new LinkedHashSet<>();
        for (MemberLoginPlatformEnum platform : values()) {
            codes.add(platform.code);
        }
        return codes;
    }

    public static Set<String> defaultForRoleUids(Set<String> roleUids) {
        LinkedHashSet<String> codes = new LinkedHashSet<>();
        if (roleUids != null && (roleUids.contains(BytedeskConsts.DEFAULT_ROLE_ADMIN_UID)
                || roleUids.contains(BytedeskConsts.DEFAULT_ROLE_SUPER_UID))) {
            codes.addAll(allCodes());
            return codes;
        }
        codes.add(DESKTOP.code);
        return codes;
    }

    public static Set<String> parseImportPlatforms(String rawValue) {
        LinkedHashSet<String> codes = new LinkedHashSet<>();
        if (!StringUtils.hasText(rawValue)) {
            return codes;
        }
        String[] tokens = rawValue.split("[,，、;；|/\\s]+");
        for (String token : tokens) {
            String normalizedToken = normalizeImportToken(token);
            if (!StringUtils.hasText(normalizedToken)) {
                continue;
            }
            if (IMPORT_ALL_ALIASES.contains(normalizedToken)) {
                codes.addAll(allCodes());
                return codes;
            }
            String code = IMPORT_ALIASES.get(normalizedToken);
            if (code != null) {
                codes.add(code);
            }
        }
        return codes;
    }

    public static Set<String> resolveImportPlatforms(String rawValue) {
        Set<String> parsedPlatforms = parseImportPlatforms(rawValue);
        return parsedPlatforms.isEmpty() ? allCodes() : parsedPlatforms;
    }

    private static Map<String, String> createImportAliases() {
        Map<String, String> aliases = new java.util.LinkedHashMap<>();
        addImportAliases(aliases, ADMIN, "管理后台", "管理后台admin", "admin后台");
        addImportAliases(aliases, DESKTOP, "客服工作台", "桌面客服", "desktop工作台");
        addImportAliases(aliases, NOTEBASE, "知识库后台", "知识库", "notebase后台");
        addImportAliases(aliases, WORKFLOW, "工作流后台", "工作流", "workflow后台");
        addImportAliases(aliases, CALL, "呼叫中心工作台", "呼叫中心", "call工作台");
        addImportAliases(aliases, CALL_ADMIN, "呼叫中心后台", "calladmin后台", "calladmin");
        return aliases;
    }

    private static void addImportAliases(Map<String, String> aliases,
            MemberLoginPlatformEnum platform,
            String... extraAliases) {
        aliases.put(normalizeImportToken(platform.code), platform.code);
        for (String alias : extraAliases) {
            aliases.put(normalizeImportToken(alias), platform.code);
        }
    }

    private static String normalizeImportToken(String token) {
        if (token == null) {
            return "";
        }
        return token.trim()
                .replace('（', '(')
                .replace('）', ')')
                .replaceAll("[()\\[\\]{}]", "")
                .replaceAll("\\s+", "")
                .toLowerCase(Locale.ROOT);
    }
}