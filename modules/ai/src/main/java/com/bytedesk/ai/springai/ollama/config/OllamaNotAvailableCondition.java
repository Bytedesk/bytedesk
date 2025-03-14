package com.bytedesk.ai.springai.ollama.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OllamaNotAvailableCondition implements Condition {

    private final OllamaAvailableCondition ollamaAvailableCondition = new OllamaAvailableCondition();
    
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !ollamaAvailableCondition.matches(context, metadata);
    }
}
