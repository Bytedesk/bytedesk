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
package com.bytedesk.core.push.apns_p12;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * https://github.com/alibaba/easyexcel
 */
@Data
public class ApnsP12Excel {

    @ExcelProperty(index = 0, value = "证书名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(index = 1, value = "Bundle ID")
    @ColumnWidth(28)
    private String bundleId;

    @ExcelProperty(index = 2, value = "证书地址")
    @ColumnWidth(36)
    private String p12Url;

    @ExcelProperty(index = 3, value = "沙盒环境")
    @ColumnWidth(12)
    private Boolean sandbox;

    @ExcelProperty(index = 4, value = "启用")
    @ColumnWidth(12)
    private Boolean enabled;

    @ExcelProperty(index = 5, value = "备注")
    @ColumnWidth(28)
    private String description;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(index = 6, value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;

}
