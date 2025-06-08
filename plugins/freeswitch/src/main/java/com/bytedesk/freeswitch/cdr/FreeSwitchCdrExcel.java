/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.cdr;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * FreeSwitch通话详单Excel导出类
 * https://github.com/alibaba/easyexcel
 */
@Data
public class FreeSwitchCdrExcel {

    @ExcelProperty(index = 0, value = "通话UUID")
    @ColumnWidth(40)
    private String uuid;

    @ExcelProperty(index = 1, value = "主叫号码")
    @ColumnWidth(20)
    private String callerIdNumber;

    @ExcelProperty(index = 2, value = "被叫号码")
    @ColumnWidth(20)
    private String destinationNumber;

    @ExcelProperty(index = 3, value = "通话状态")
    @ColumnWidth(15)
    private String hangupCause;

    @ExcelProperty(index = 4, value = "通话时长(秒)")
    @ColumnWidth(15)
    private Integer duration;

    @ExcelProperty(index = 5, value = "计费时长(秒)")
    @ColumnWidth(15)
    private Integer billSec;

    @ExcelProperty(index = 6, value = "接听延迟(秒)")
    @ColumnWidth(15)
    private Integer progressMediaSec;

    @ExcelProperty(index = 7, value = "主叫IP")
    @ColumnWidth(20)
    private String networkAddr;

    @ExcelProperty(index = 8, value = "用户代理")
    @ColumnWidth(30)
    private String userAgent;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "开始时间")
    @ColumnWidth(25)
    private LocalDateTime startStamp;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "接听时间")
    @ColumnWidth(25)
    private LocalDateTime answerStamp;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "结束时间")
    @ColumnWidth(25)
    private LocalDateTime endStamp;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    @ColumnWidth(25)
    private LocalDateTime createdAt;

}
