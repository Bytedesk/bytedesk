/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-22 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-22 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms of the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_android_device;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * 设备推送绑定导出 Excel 行
 *
 * <p>https://github.com/alibaba/easyexcel
 */
@Data
public class PushDeviceExcel {

    @ExcelProperty(index = 0, value = "绑定账号")
    @ColumnWidth(28)
    private String account;

    @ExcelProperty(index = 1, value = "供应商")
    @ColumnWidth(16)
    private String provider;

    @ExcelProperty(index = 2, value = "设备ID")
    @ColumnWidth(40)
    private String deviceId;

    @ExcelProperty(index = 3, value = "绑定类型")
    @ColumnWidth(20)
    private String type;

    @ExcelProperty(index = 4, value = "设备平台")
    @ColumnWidth(16)
    private String device;

    @ExcelProperty(index = 5, value = "客户端渠道")
    @ColumnWidth(16)
    private String channel;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;
}
