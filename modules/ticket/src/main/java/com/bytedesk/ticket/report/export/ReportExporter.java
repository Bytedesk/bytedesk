/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 13:04:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 16:12:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.report.export;

import com.bytedesk.ticket.report.TicketReportDTO;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class ReportExporter {
    
    public byte[] exportReport(TicketReportDTO report, String format) throws IOException {
        return switch (format.toLowerCase()) {
            case "pdf" -> exportToPdf(report);
            case "excel" -> exportToExcel(report);
            case "csv" -> exportToCsv(report);
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }
    
    private byte[] exportToPdf(TicketReportDTO report) throws IOException {
        return new PdfExporter().export(report);
    }
    
    private byte[] exportToExcel(TicketReportDTO report) throws IOException {
        return new ExcelExporter().export(report);
    }
    
    private byte[] exportToCsv(TicketReportDTO report) throws IOException {
        return new CsvExporter().export(report);
    }
} 