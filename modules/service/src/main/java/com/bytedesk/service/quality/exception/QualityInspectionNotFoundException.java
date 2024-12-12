package com.bytedesk.service.quality.exception;

public class QualityInspectionNotFoundException extends QualityException {
    
    public QualityInspectionNotFoundException(Long inspectionId) {
        super("Quality inspection not found: " + inspectionId);
    }
} 