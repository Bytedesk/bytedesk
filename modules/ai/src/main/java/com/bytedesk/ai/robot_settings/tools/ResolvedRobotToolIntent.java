package com.bytedesk.ai.robot_settings.tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record ResolvedRobotToolIntent(
        String toolName,
        String toolKey,
        String description,
        String bindingType,
        List<String> intentKeywords,
        String intentMatchMode,
        Integer orderIndex,
        Map<String, Object> metadata) implements Serializable {

    public ResolvedRobotToolIntent {
        intentKeywords = intentKeywords == null ? List.of() : List.copyOf(intentKeywords);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String toolName;
        private String toolKey;
        private String description;
        private String bindingType;
        private List<String> intentKeywords = new ArrayList<>();
        private String intentMatchMode;
        private Integer orderIndex;
        private Map<String, Object> metadata = Map.of();

        public Builder toolName(String toolName) {
            this.toolName = toolName;
            return this;
        }

        public Builder toolKey(String toolKey) {
            this.toolKey = toolKey;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder bindingType(String bindingType) {
            this.bindingType = bindingType;
            return this;
        }

        public Builder intentKeywords(List<String> intentKeywords) {
            this.intentKeywords = intentKeywords;
            return this;
        }

        public Builder intentMatchMode(String intentMatchMode) {
            this.intentMatchMode = intentMatchMode;
            return this;
        }

        public Builder orderIndex(Integer orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public ResolvedRobotToolIntent build() {
            return new ResolvedRobotToolIntent(toolName, toolKey, description, bindingType, intentKeywords,
                    intentMatchMode, orderIndex, metadata);
        }
    }
}