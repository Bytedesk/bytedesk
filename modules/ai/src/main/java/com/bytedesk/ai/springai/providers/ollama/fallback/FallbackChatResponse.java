package com.bytedesk.ai.springai.providers.ollama.fallback;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FallbackChatResponse {

    private final String message;

    public FallbackChatResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("status", "fallback");
        return Collections.unmodifiableMap(metadata);
    }

    @Override
    public String toString() {
        return message;
    }
}
