/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-08 10:09:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.bytedesk.core.base.BaseExcel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * https://github.com/alibaba/easyexcel
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BlackExcel  extends BaseExcel {

    // @ExcelProperty(value = "黑名单用户ID")
    // @ColumnWidth(20)
    // private String blackUid;

    @ExcelProperty(value = "黑名单用户昵称")
    @ColumnWidth(25)
    private String blackNickname;

    // @ExcelProperty(value = "黑名单用户头像")
    // @ColumnWidth(20)
    // private String blackAvatar;

    @ExcelProperty(value = "封禁原因")
    @ColumnWidth(30)
    private String reason;

    @ExcelProperty(value = "是否封禁IP")
    @ColumnWidth(15)
    private String blockIp;

    // @ExcelProperty(value = "客服UID")
    // @ColumnWidth(20)
    private String name;

    @ExcelProperty(value = "客服昵称")
    @ColumnWidth(25)
    private String userNickname;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "开始时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime startTime;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "结束时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime endTime;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "拉黑时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;
}
