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
package com.bytedesk.call.xml_curl_trace;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * https://github.com/alibaba/easyexcel
 */
@Data
public class XmlCurlTraceExcel {

    @ExcelProperty(index = 0, value = "分区")
    @ColumnWidth(20)
    private String section;

    @ExcelProperty(index = 1, value = "类别")
    @ColumnWidth(20)
    private String category;

    @ExcelProperty(index = 2, value = "来源")
    @ColumnWidth(28)
    private String remote;

    @ExcelProperty(index = 3, value = "方法")
    @ColumnWidth(12)
    private String method;

    @ExcelProperty(index = 4, value = "路径")
    @ColumnWidth(28)
    private String uri;

    @ExcelProperty(index = 5, value = "命中")
    @ColumnWidth(12)
    private Boolean found;

    @ExcelProperty(index = 6, value = "响应大小")
    @ColumnWidth(14)
    private Integer responseSize;

    @ExcelProperty(index = 7, value = "耗时(ms)")
    @ColumnWidth(14)
    private Long costMs;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(index = 8, value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;

}
