/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 09:44:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

// https://github.com/alibaba/easyexcel
// https://easyexcel.opensource.alibaba.com/docs/current/
@Data
public class ActionExcel {

    @ExcelProperty(index = 0, value = "Title")
    @ColumnWidth(20)
    private String title;

    @ExcelProperty(index = 1, value = "Action")
    @ColumnWidth(20)
    private String action;

    @ExcelProperty(index = 2, value = "Description")
    @ColumnWidth(20)
    private String description;

    @ExcelProperty(index = 3, value = "IP")
    @ColumnWidth(20)
    private String ip;

    @ExcelProperty(index = 4, value = "IP Location")
    @ColumnWidth(20)
    private String ipLocation;

    @ExcelProperty(index = 5, value = "Type")
    @ColumnWidth(20)
    private String type;

    @ExcelProperty(index = 6, value = "Extra")
    @ColumnWidth(20)
    private String extra;

    @ExcelProperty(index = 7, value = "User")
    @ColumnWidth(20)
    private String user;

    @ExcelProperty(index = 8, value = "Created At")
    @ColumnWidth(20)
    private String createdAt;
    
}
