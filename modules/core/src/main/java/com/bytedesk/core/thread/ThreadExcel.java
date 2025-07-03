/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-22 22:12:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-01 09:11:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.bytedesk.core.base.BaseExcel;

import lombok.Data;
import lombok.EqualsAndHashCode;

// https://github.com/alibaba/easyexcel
// https://easyexcel.opensource.alibaba.com/docs/current/
@Data
@EqualsAndHashCode(callSuper = false)
public class ThreadExcel extends BaseExcel {

    @ExcelProperty(value = "昵称")
    @ColumnWidth(25)
    private String visitorNickname;

    @ExcelProperty(value = "人工客服")
    @ColumnWidth(20)
    private String agentNickname;

    // robot
    @ExcelProperty(value = "机器人")
    @ColumnWidth(20)
    private String robotNickname;

    // workgroup
    @ExcelProperty(value = "工作组")
    @ColumnWidth(20)
    private String workgroupNickname;

    // status
    @ExcelProperty(value = "状态")
    @ColumnWidth(20)
    private String status;

    // client
    @ExcelProperty(value = "客户端")
    @ColumnWidth(20)
    private String client;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    @ColumnWidth(28)
    private ZonedDateTime createdAt;

}
