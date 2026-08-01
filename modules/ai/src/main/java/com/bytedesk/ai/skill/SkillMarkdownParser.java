package com.bytedesk.ai.skill;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

final class SkillMarkdownParser {

    private static final String FRONTMATTER_BOUNDARY = "---";

    private SkillMarkdownParser() {
    }

    static SkillRequest parse(String markdown, String directoryName) {
        Map<String, String> frontmatter = extractFrontmatter(markdown);
        String name = firstNonBlank(frontmatter.get("name"), directoryName);
        String description = frontmatter.get("description");
        return SkillRequest.builder()
                .name(name)
                .description(normalizeValue(description))
                .build();
    }

    private static Map<String, String> extractFrontmatter(String markdown) {
        Map<String, String> frontmatter = new LinkedHashMap<>();
        if (!StringUtils.hasText(markdown)) {
            return frontmatter;
        }

        String[] lines = markdown.split("\\R");
        if (lines.length == 0 || !FRONTMATTER_BOUNDARY.equals(lines[0].trim())) {
            return frontmatter;
        }

        for (int index = 1; index < lines.length; index++) {
            String line = lines[index].trim();
            if (FRONTMATTER_BOUNDARY.equals(line)) {
                break;
            }

            int separatorIndex = line.indexOf(':');
            if (separatorIndex <= 0) {
                continue;
            }

            String key = line.substring(0, separatorIndex).trim();
            String value = line.substring(separatorIndex + 1).trim();
            frontmatter.put(key, normalizeValue(value));
        }
        return frontmatter;
    }

    private static String firstNonBlank(String primary, String fallback) {
        return StringUtils.hasText(primary) ? primary : fallback;
    }

    private static String normalizeValue(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String normalized = value.trim();
        if ((normalized.startsWith("\"") && normalized.endsWith("\""))
                || (normalized.startsWith("'") && normalized.endsWith("'"))) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }
        return normalized.trim();
    }
}