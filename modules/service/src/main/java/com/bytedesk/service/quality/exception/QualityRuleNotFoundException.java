package com.bytedesk.service.quality.exception;

public class QualityRuleNotFoundException extends QualityException {
    
    public QualityRuleNotFoundException(Long ruleId) {
        super("Quality rule not found: " + ruleId);
    }
} 