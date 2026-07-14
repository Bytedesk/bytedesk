package com.bytedesk.call.httapi;

import java.util.Map;
import org.springframework.util.StringUtils;

public final class HttapiMrcpProfileOverrideSupport {

    private HttapiMrcpProfileOverrideSupport() {
    }

    public static String resolveExplicitProfile(Map<String, String> vars) {
        return pick(vars,
                "mrcp_profile",
                "variable_mrcp_profile",
                "tts_profile",
                "variable_tts_profile",
                "asr_profile",
                "variable_asr_profile",
                "unimrcp:profile",
                "variable_unimrcp:profile");
    }

    public static String pick(Map<String, String> vars, String... keys) {
        if (vars == null || vars.isEmpty()) {
            return null;
        }
        for (String key : keys) {
            String value = vars.get(key);
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }
}