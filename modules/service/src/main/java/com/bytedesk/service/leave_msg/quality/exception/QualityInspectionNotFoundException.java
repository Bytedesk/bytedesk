package com.bytedesk.service.leave_msg.quality.exception;

public class QualityInspectionNotFoundException extends QualityException {
    
    public QualityInspectionNotFoundException(Long inspectionId) {
        super("Quality inspection not found: " + inspectionId);
    }
} 