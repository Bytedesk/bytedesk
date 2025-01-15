package com.bytedesk.ticket.agi.export;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExportConfig {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> fields;  // 要导出的字段
    private String format;        // 导出格式：csv, excel, pdf
    private String timeZone;      // 时区
    private String dateFormat;    // 日期格式
    private Boolean includeComments = false;  // 是否包含评论
    private Boolean includeHistory = false;   // 是否包含历史记录
    private Boolean includeAttachments = false; // 是否包含附件
    private String language = "zh_CN";  // 导出语言
    private Long categoryId;      // 分类过滤
    private String priority;      // 优先级过滤
    private String status;        // 状态过滤
    private Long assignedTo;      // 处理人过滤
} 