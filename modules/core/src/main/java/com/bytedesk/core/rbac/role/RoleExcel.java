/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-11-29 12:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * Role Excel export model
 * https://github.com/alibaba/easyexcel
 */
@Data
public class RoleExcel {

    @ExcelProperty(index = 0, value = "角色名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(index = 1, value = "角色描述")
    @ColumnWidth(30)
    private String description;

    @ExcelProperty(index = 2, value = "系统角色")
    @ColumnWidth(15)
    private Boolean system;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;

}
