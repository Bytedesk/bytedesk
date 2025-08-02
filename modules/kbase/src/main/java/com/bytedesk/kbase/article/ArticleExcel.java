/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-03 07:26:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-03 07:43:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleExcel {

    @ExcelProperty(index = 0, value = "标题")
    @ColumnWidth(20)
    private String title;
    
    @ExcelProperty(index = 1, value = "摘要")
    @ColumnWidth(30)
    private String summary;
    
    @ExcelProperty(index = 2, value = "内容")
    @ColumnWidth(50)
    private String contentHtml;
    
    @ExcelProperty(index = 3, value = "浏览次数")
    @ColumnWidth(15)
    private Integer readCount;
    
    @ExcelProperty(index = 4, value = "点赞次数")
    @ColumnWidth(15)
    private Integer likeCount;

    @ExcelProperty(index = 5, value = "创建时间")
    @ColumnWidth(20)
    private String createdAt;
    
    @ExcelProperty(index = 6, value = "更新时间")
    @ColumnWidth(20)
    private String updatedAt;

}
