/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 18:00:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.mcp_server;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * https://github.com/alibaba/easyexcel
 * MCP Server Excel export/import template
 */
@Data
public class McpServerExcel {

    @ExcelProperty(index = 0, value = "服务器名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(index = 1, value = "描述")
    @ColumnWidth(30)
    private String description;

    @ExcelProperty(index = 2, value = "服务器类型")
    @ColumnWidth(15)
    private String serverType;

    @ExcelProperty(index = 3, value = "服务器版本")
    @ColumnWidth(15)
    private String serverVersion;

    @ExcelProperty(index = 4, value = "服务器URL")
    @ColumnWidth(40)
    private String serverUrl;

    @ExcelProperty(index = 5, value = "主机地址")
    @ColumnWidth(20)
    private String host;

    @ExcelProperty(index = 6, value = "端口")
    @ColumnWidth(10)
    private Integer port;

    @ExcelProperty(index = 7, value = "协议")
    @ColumnWidth(10)
    private McpServerProtocolEnum protocol;

    @ExcelProperty(index = 8, value = "认证类型")
    @ColumnWidth(15)
    private McpServerAuthTypeEnum authType;

    @ExcelProperty(index = 9, value = "连接超时(ms)")
    @ColumnWidth(15)
    private Integer connectionTimeout;

    @ExcelProperty(index = 10, value = "读取超时(ms)")
    @ColumnWidth(15)
    private Integer readTimeout;

    @ExcelProperty(index = 11, value = "最大重试次数")
    @ColumnWidth(15)
    private Integer maxRetries;

    @ExcelProperty(index = 12, value = "状态")
    @ColumnWidth(10)
    private McpServerStatusEnum status;

    @ExcelProperty(index = 13, value = "是否启用")
    @ColumnWidth(10)
    private Boolean enabled;

    @ExcelProperty(index = 14, value = "自动启动")
    @ColumnWidth(10)
    private Boolean autoStart;

    @ExcelProperty(index = 15, value = "健康检查URL")
    @ColumnWidth(30)
    private String healthCheckUrl;

    @ExcelProperty(index = 16, value = "健康检查间隔(s)")
    @ColumnWidth(18)
    private Integer healthCheckInterval;

    @ExcelProperty(index = 17, value = "优先级")
    @ColumnWidth(10)
    private Integer priority;

    @ExcelProperty(index = 18, value = "标签")
    @ColumnWidth(20)
    private String tags;

    @ExcelProperty(index = 19, value = "能力")
    @ColumnWidth(30)
    private String capabilities;

    @ExcelProperty(index = 20, value = "可用工具")
    @ColumnWidth(30)
    private String availableTools;

    @ExcelProperty(index = 21, value = "可用资源")
    @ColumnWidth(30)
    private String availableResources;

    @ExcelProperty(index = 22, value = "可用提示")
    @ColumnWidth(30)
    private String availablePrompts;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(index = 23, value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(index = 24, value = "最后连接时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime lastConnected;

}
