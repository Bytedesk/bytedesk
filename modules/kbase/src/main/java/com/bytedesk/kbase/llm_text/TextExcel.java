/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-22 22:12:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-04 12:57:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * 文本导出Excel实体类
 */
@Data
public class TextExcel {

    @ExcelProperty(value = "分类")
    @ColumnWidth(20)
    private String category;

    @ExcelProperty(value = "标题")
    @ColumnWidth(30)
    private String title;

    @ExcelProperty(value = "内容")
    @ColumnWidth(50)
    private String content;

}
