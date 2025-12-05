/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 11:06:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow_log;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * https://github.com/alibaba/easyexcel
 */
@Data
public class WorkflowLogExcel {

    @ExcelProperty(index = 0, value = "工作流名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(index = 1, value = "执行ID")
    @ColumnWidth(25)
    private String executionUid;

    @ExcelProperty(index = 2, value = "节点名称")
    @ColumnWidth(20)
    private String nodeName;

    @ExcelProperty(index = 3, value = "节点类型")
    @ColumnWidth(18)
    private String nodeType;

    @ExcelProperty(index = 4, value = "节点状态")
    @ColumnWidth(18)
    private String nodeStatus;

    @ExcelProperty(index = 5, value = "耗时(ms)")
    @ColumnWidth(15)
    private Long durationMs;

    @ExcelProperty(index = 6, value = "错误信息")
    @ColumnWidth(30)
    private String errorMessage;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(index = 7, value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(index = 8, value = "修改时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime updatedAt;

}
