/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 13:22:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 15:55:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.agi.export;

import org.springframework.stereotype.Component;

import com.bytedesk.ticket.ticket.TicketEntity;

import java.util.Map;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

@Component
public class ExportFormatFactory {
    
    private final Map<String, TicketExporter> exporters = new HashMap<>();
    
    public ExportFormatFactory() {
        // exporters.put("csv", new CsvTicketExporter());
        // exporters.put("excel", new ExcelTicketExporter());
        // exporters.put("pdf", new PdfTicketExporter());
    }
    
    public TicketExporter getExporter(String format) {
        TicketExporter exporter = exporters.get(format.toLowerCase());
        if (exporter == null) {
            throw new IllegalArgumentException("Unsupported export format: " + format);
        }
        return exporter;
    }
}

interface TicketExporter {
    byte[] export(List<TicketEntity> tickets, ExportConfig config);
    void exportToStream(List<TicketEntity> tickets, ExportConfig config, OutputStream outputStream);
} 