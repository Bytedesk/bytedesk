/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-14 07:12:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 12:24:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RobotMessageExcel {
    
    @ExcelProperty(value = "内容")
    @ColumnWidth(20)
    private String content;

    @ExcelProperty(value = "类型")
    @ColumnWidth(20)
    private String type;

    @ExcelProperty(value = "访客")
    @ColumnWidth(20)
    private String user;

    @ExcelProperty(value = "机器人")
    @ColumnWidth(20)
    private String robot;

    @ExcelProperty(value = "时间")
    @ColumnWidth(20)
    private String createdAt;
}
