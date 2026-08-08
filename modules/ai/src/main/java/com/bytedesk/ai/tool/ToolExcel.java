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
package com.bytedesk.ai.tool;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * https://github.com/alibaba/easyexcel
 */
@Data
public class ToolExcel {

    @ExcelProperty(index = 0, value = "工具标识")
    @ColumnWidth(20)
    private String key;

    @ExcelProperty(index = 1, value = "工具名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(index = 2, value = "类型")
    @ColumnWidth(20)
    private String type;

    @ExcelProperty(index = 3, value = "分类")
    @ColumnWidth(20)
    private String category;

    @ExcelProperty(index = 4, value = "绑定类型")
    @ColumnWidth(20)
    private String bindingType;

    @ExcelProperty(index = 5, value = "启用")
    @ColumnWidth(12)
    private Boolean enabled;

    @ExcelProperty(index = 6, value = "顺序")
    @ColumnWidth(12)
    private Integer orderIndex;

    @ExcelProperty(index = 7, value = "描述")
    @ColumnWidth(40)
    private String description;

    @ExcelProperty(index = 8, value = "MCP暴露模式")
    @ColumnWidth(18)
    private String mcpExposureMode;

    @ExcelProperty(index = 9, value = "允许方法")
    @ColumnWidth(30)
    private String allowedMethods;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;

}
