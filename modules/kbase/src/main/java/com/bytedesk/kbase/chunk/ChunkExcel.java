/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-22 22:12:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 12:17:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.chunk;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * Chunk导出Excel实体类
 */
@Data
public class ChunkExcel {

    @ExcelProperty(value = "名称")
    @ColumnWidth(30)
    private String name;

    @ExcelProperty(value = "内容")
    @ColumnWidth(50)
    private String content;

    @ExcelProperty(value = "状态")
    @ColumnWidth(15)
    private String status;

    @ExcelProperty(value = "向量状态")
    @ColumnWidth(15)
    private String vectorStatus;

    @ExcelProperty(value = "标签")
    @ColumnWidth(20)
    private String tagList;

    @ExcelProperty(value = "是否启用")   
    @ColumnWidth(20)
    private String enabled;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "有效开始日期")
    @ColumnWidth(25)
    private LocalDateTime startDate;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "有效结束日期")
    @ColumnWidth(25)
    private LocalDateTime endDate;

    @ExcelProperty(value = "文件名称")
    @ColumnWidth(20)
    private String fileName;

    @ExcelProperty(value = "知识库")
    @ColumnWidth(20)
    private String kbaseName;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    @ColumnWidth(25)
    private LocalDateTime createdAt;
}
