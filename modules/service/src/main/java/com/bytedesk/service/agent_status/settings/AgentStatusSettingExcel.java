/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 23:09:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status.settings;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * https://github.com/alibaba/easyexcel
 */
@Data
public class AgentStatusSettingExcel {

    @ExcelProperty(index = 0, value = "Name")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(index = 1, value = "Type")
    @ColumnWidth(20)
    private String type;

    @ExcelProperty(index = 2, value = "Color")
    @ColumnWidth(20)
    private String color;

    
}
