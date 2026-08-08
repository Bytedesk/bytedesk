package com.bytedesk.ai.robot_settings.tools;

import java.util.Arrays;

public enum ToolIntentMatchMode {

    KEYWORD,
    MODEL,
    HYBRID;

    public static String normalize(String value) {
        if (value == null) {
            return KEYWORD.name();
        }

        String normalized = value.trim();
        return Arrays.stream(values())
                .filter(mode -> mode.name().equalsIgnoreCase(normalized))
                .findFirst()
                .map(Enum::name)
                .orElse(KEYWORD.name());
    }
}