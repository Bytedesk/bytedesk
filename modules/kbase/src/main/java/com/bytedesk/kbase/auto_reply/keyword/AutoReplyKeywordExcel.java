/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-22 22:12:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 09:56:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.keyword;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;
import lombok.EqualsAndHashCode;

// https://github.com/alibaba/easyexcel
// https://easyexcel.opensource.alibaba.com/docs/current/
@Data
@EqualsAndHashCode
public class AutoReplyKeywordExcel {

    @ExcelProperty(index = 0, value = "分类")
    @ColumnWidth(20)
    private String category;

    @ExcelProperty(index = 1, value = "内容类型")
    @ColumnWidth(15)
    private String contentType; // TEXT, IMAGE, FILE, etc.

    @ExcelProperty(index = 2, value = "关键词列表")
    @ColumnWidth(30)
    private String keywordList; // 存储格式："关键词1|关键词2|关键词3"

    @ExcelProperty(index = 3, value = "回复列表")
    @ColumnWidth(30)
    private String replyList; // 存储格式："回复1|回复2|回复3"

    @ExcelProperty(index = 4, value = "匹配模式")
    @ColumnWidth(15)
    private String matchType; // EXACT, FUZZY, REGEX

    
}
