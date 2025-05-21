/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-24 21:40:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 11:49:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

// 导出prompt机器人
@Data
public class RobotExcel {

    @ExcelProperty(index = 0, value = "名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(index = 1, value = "昵称")
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(index = 2, value = "提示语")
    @ColumnWidth(40)
    private String prompt;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(index = 3, value = "创建时间")
    @ColumnWidth(20)
    private LocalDateTime createdAt;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(index = 4, value = "修改时间")
    @ColumnWidth(20)
    private LocalDateTime updatedAt;
}
