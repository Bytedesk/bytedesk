/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-30 22:05:12
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

    @ExcelProperty("用户")
    @ColumnWidth(20)
    private String user;

    @ExcelProperty("标题")
    @ColumnWidth(20)
    private String title;

    @ExcelProperty("操作")
    @ColumnWidth(20)
    private String action;

    // @ExcelProperty("描述")
    // @ColumnWidth(20)
    // private String description;

    @ExcelProperty("IP")
    @ColumnWidth(20)
    private String ip;

    @ExcelProperty("IP位置")
    @ColumnWidth(20)
    private String ipLocation;

    // @ExcelProperty("类型")
    // @ColumnWidth(20)
    // private String type;

    @ExcelProperty("创建时间")
    @ColumnWidth(25)
    private String createdAt;
    
}
