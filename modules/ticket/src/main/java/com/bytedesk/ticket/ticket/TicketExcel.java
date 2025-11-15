/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-22 22:12:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 17:33:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

// https://github.com/alibaba/easyexcel
// https://easyexcel.opensource.alibaba.com/docs/current/
@Data
public class TicketExcel {

    // @ExcelProperty(value = "标题")
    // @ColumnWidth(20)
    // private String title;

    @ExcelProperty(value = "工单编号")
    @ColumnWidth(20)
    private String ticketNumber;

    @ExcelProperty(value = "描述")
    @ColumnWidth(20)
    private String description;

    @ExcelProperty(value = "状态")
    @ColumnWidth(20)
    private String status;

    @ExcelProperty(value = "优先级")
    @ColumnWidth(20)
    private String priority;

    @ExcelProperty(value = "类型")
    @ColumnWidth(20)
    private String type;

    // @ExcelProperty(value = "服务会话Topic")
    // @ColumnWidth(20)
    // private String serviceThreadTopic;

    @ExcelProperty(value = "会话Uid")
    @ColumnWidth(20)
    private String threadUid;

    @ExcelProperty(value = "客户")
    @ColumnWidth(20)
    private String user;

    @ExcelProperty(value = "部门")
    @ColumnWidth(20)
    private String department;

    @ExcelProperty(value = "指定处理人")
    @ColumnWidth(20)
    private String assignee;

    @ExcelProperty(value = "报告人")
    @ColumnWidth(20)
    private String reporter;

    @ExcelProperty(value = "创建时间")
    @ColumnWidth(20)
    private String createdAt;

    @ExcelProperty(value = "更新时间")
    @ColumnWidth(20)
    private String updatedAt;

}
