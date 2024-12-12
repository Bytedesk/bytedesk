/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 13:22:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 15:55:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.export;

import java.io.OutputStream;
import java.time.LocalDateTime;

import lombok.Data;

public interface TicketExportService {
    
    // 导出工单
    byte[] exportTickets(ExportConfig config);
    
    // 导出到流
    void exportTicketsToStream(ExportConfig config, OutputStream outputStream);
    
    // 异步导出
    String startAsyncExport(ExportConfig config);
    
    // 获取导出进度
    ExportProgress getExportProgress(String exportId);
    
    // 获取导出结果
    byte[] getExportResult(String exportId);
    
    // 取消导出
    void cancelExport(String exportId);
}

@Data
class ExportProgress {
    private String exportId;
    private int totalRecords;
    private int processedRecords;
    private String status;  // pending, processing, completed, failed, cancelled
    private String error;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String downloadUrl;
} 