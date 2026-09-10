/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 23:09:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.holiday;

import java.time.LocalDate;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * https://github.com/alibaba/easyexcel
 */
@Data
public class HolidayExcel {

    @ExcelProperty(index = 0, value = "名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(index = 1, value = "描述")
    @ColumnWidth(40)
    private String description;

    @ExcelProperty(index = 2, value = "类型")
    @ColumnWidth(20)
    private String type;

    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty(index = 3, value = "日期")
    @ColumnWidth(20)
    private LocalDate holidayDate;

    @ExcelProperty(index = 4, value = "年份")
    @ColumnWidth(15)
    private Integer holidayYear;

    @ExcelProperty(index = 5, value = "国家/地区代码")
    @ColumnWidth(20)
    private String countryCode;

    @ExcelProperty(index = 6, value = "休息日")
    @ColumnWidth(15)
    private Boolean offDay;

    @ExcelProperty(index = 7, value = "官方数据")
    @ColumnWidth(15)
    private Boolean official;

    @ExcelProperty(index = 8, value = "节假日标识")
    @ColumnWidth(30)
    private String holidayKey;

}
