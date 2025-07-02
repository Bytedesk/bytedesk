/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:58:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form_result;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * https://github.com/alibaba/easyexcel
 */
@Data
public class FormResultExcel {

    @ExcelProperty(index = 0, value = "标签名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(index = 1, value = "类型")
    @ColumnWidth(20)
    private String type;

    @ExcelProperty(index = 2, value = "颜色")
    @ColumnWidth(20)
    private String color;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    @ColumnWidth(25)
    private LocalDateTime createdAt;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "修改时间")
    @ColumnWidth(25)
    private LocalDateTime updatedAt;

}
