package com.bytedesk.ai.robot_settings.tools;

import java.util.Arrays;

/**
 * Known tool choice values mirrored from the admin tool settings UI.
 * <p>
 * The settings model still stores plain strings so custom provider-specific
 * values can continue to pass through unchanged when needed.
 */
public enum ToolChoice {

    AUTO,
    NONE,
    REQUIRED;

    public String providerValue() {
        return name().toLowerCase();
    }

    public static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return Arrays.stream(values())
                .filter(choice -> choice.name().equalsIgnoreCase(normalized))
                .findFirst()
                .map(ToolChoice::providerValue)
                .orElse(normalized);
    }
}