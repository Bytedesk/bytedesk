/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.number;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * FreeSwitch用户Excel导出类
 * https://github.com/alibaba/easyexcel
 */
@Data
public class FreeSwitchNumberExcel {

    @ExcelProperty(index = 0, value = "用户名")
    @ColumnWidth(20)
    private String username;

    @ExcelProperty(index = 1, value = "域名")
    @ColumnWidth(20)
    private String domain;

    @ExcelProperty(index = 2, value = "显示名称")
    @ColumnWidth(20)
    private String displayName;

    @ExcelProperty(index = 3, value = "邮箱")
    @ColumnWidth(25)
    private String email;

    @ExcelProperty(index = 4, value = "账户代码")
    @ColumnWidth(20)
    private String accountcode;

    @ExcelProperty(index = 5, value = "是否启用")
    @ColumnWidth(15)
    private Boolean enabled;

    @ExcelProperty(index = 6, value = "SIP端口")
    @ColumnWidth(15)
    private Integer sipPort;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "最后注册时间")
    @ColumnWidth(25)
    private LocalDateTime lastRegister;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    @ColumnWidth(25)
    private LocalDateTime createdAt;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "修改时间")
    @ColumnWidth(25)
    private LocalDateTime updatedAt;

}
