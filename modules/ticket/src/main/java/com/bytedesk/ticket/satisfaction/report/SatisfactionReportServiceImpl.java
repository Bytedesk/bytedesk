package com.bytedesk.ticket.satisfaction.report;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class SatisfactionReportServiceImpl implements SatisfactionReportService {

    @Override
    public SatisfactionReportDTO generateReport(LocalDateTime startTime, LocalDateTime endTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateReport'");
    }

    @Override
    public SatisfactionReportDTO generateAgentReport(Long agentId, LocalDateTime startTime, LocalDateTime endTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateAgentReport'");
    }

    @Override
    public SatisfactionReportDTO generateCategoryReport(Long categoryId, LocalDateTime startTime,
            LocalDateTime endTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateCategoryReport'");
    }

    @Override
    public byte[] exportReport(LocalDateTime startTime, LocalDateTime endTime, String format) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'exportReport'");
    }
    
}
