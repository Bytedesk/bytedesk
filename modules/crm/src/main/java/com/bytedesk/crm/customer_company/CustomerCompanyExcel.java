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
package com.bytedesk.crm.customer_company;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * https://github.com/alibaba/easyexcel
 */
@Data
public class CustomerCompanyExcel {

    @ExcelProperty(index = 0, value = "公司名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(index = 1, value = "行业")
    @ColumnWidth(20)
    private String industry;

    @ExcelProperty(index = 2, value = "规模")
    @ColumnWidth(20)
    private String size;

    @ExcelProperty(index = 3, value = "网站")
    @ColumnWidth(30)
    private String website;

    @ExcelProperty(index = 4, value = "归属人")
    @ColumnWidth(25)
    private String ownerUserUid;

    @ExcelProperty(index = 5, value = "备注")
    @ColumnWidth(30)
    private String description;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;

}
