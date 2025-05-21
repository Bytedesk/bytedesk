package com.bytedesk.voc.feedback.validator;

import com.bytedesk.voc.config.VocConfig;
import com.bytedesk.voc.feedback.dto.FeedbackRequest;
import com.bytedesk.voc.feedback.exception.FeedbackValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeedbackValidator {
    
    @Autowired
    private VocConfig vocConfig;
    
    public void validateCreate(FeedbackRequest request) {
        // 检查内容长度
        if (request.getContent().length() > vocConfig.getMaxFeedbackLength()) {
            throw new FeedbackValidationException("Content too long");
        }
        
        // 检查类型是否有效
        if (!isValidType(request.getType())) {
            throw new FeedbackValidationException("Invalid feedback type");
        }
        
        // 检查敏感词
        if (vocConfig.isFilterSensitiveWords() && containsSensitiveWords(request.getContent())) {
            throw new FeedbackValidationException("Content contains sensitive words");
        }
    }
    
    private Boolean isValidType(String type) {
        return type != null && type.matches("^(bug|suggestion|complaint|feature|other)$");
    }
    
    private Boolean containsSensitiveWords(String content) {
        // TODO: 实现敏感词检查
        return false;
    }
} 