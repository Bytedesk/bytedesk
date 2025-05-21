/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-22 22:12:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 12:17:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

@Data
public class FaqExcel {

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

    @ExcelProperty(value = "状态")
    @ColumnWidth(15)
    private String status;

    @ExcelProperty(value = "向量状态")
    @ColumnWidth(15)
    private String vectorStatus;

    @ExcelProperty(value = "标签")
    @ColumnWidth(20)
    private String tagList;

    @ExcelProperty(value = "点击次数")
    @ColumnWidth(20)
    private Integer clickCount;

    @ExcelProperty(value = "点赞次数")
    @ColumnWidth(20)
    private Integer upCount;

    @ExcelProperty(value = "点踩次数")
    @ColumnWidth(20)
    private Integer downCount;

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

    @ExcelProperty(value = "扩展问答")
    @ColumnWidth(20)
    private String answerList;

}
