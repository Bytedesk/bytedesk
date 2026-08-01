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
package com.bytedesk.core.sms_push;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * https://github.com/alibaba/easyexcel
 */
@Data
public class SmsPushExcel {

    @ExcelProperty(index = 0, value = "发送人")
    @ColumnWidth(20)
    private String sender;

    @ExcelProperty(index = 1, value = "接收人")
    @ColumnWidth(24)
    private String receiver;

    @ExcelProperty(index = 2, value = "内容")
    @ColumnWidth(36)
    private String content;

    @ExcelProperty(index = 3, value = "类型")
    @ColumnWidth(20)
    private String type;

    @ExcelProperty(index = 4, value = "状态")
    @ColumnWidth(16)
    private String status;

    @ExcelProperty(index = 5, value = "渠道")
    @ColumnWidth(16)
    private String channel;

    @ExcelProperty(index = 6, value = "发送结果")
    @ColumnWidth(18)
    private Boolean sendSuccess;

    @ExcelProperty(index = 7, value = "结果消息")
    @ColumnWidth(30)
    private String sendMessage;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;

}
