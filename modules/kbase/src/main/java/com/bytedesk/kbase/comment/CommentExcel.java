/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-22 22:12:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-04 12:56:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.comment;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;


@Data
public class CommentExcel {

    @ExcelProperty(index = 0, value = "分类")
    @ColumnWidth(20)
    private String category;

    @ExcelProperty(index = 1, value = "类型")
    @ColumnWidth(20)
    private String type;

    @ExcelProperty(index = 2, value = "问题")
    @ColumnWidth(20)
    private String question;

    @ExcelProperty(index = 3, value = "答案")
    @ColumnWidth(20)
    private String answer;

}
