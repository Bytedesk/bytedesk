/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 23:14:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.black;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * https://github.com/alibaba/easyexcel
 */
@Data
public class IpBlacklistExcel {

    @ExcelProperty(index = 0, value = "IP地址")
    @ColumnWidth(20)
    private String ip;

    @ExcelProperty(index = 1, value = "IP位置")
    @ColumnWidth(20)
    private String ipLocation;

    @ExcelProperty(index = 2, value = "开始时间")
    @ColumnWidth(20)
    private String startTime;

    @ExcelProperty(index = 3, value = "结束时间")
    @ColumnWidth(20)
    private String endTime;
    
    @ExcelProperty(index = 4, value = "原因")
    @ColumnWidth(30)
    private String reason;
    
    @ExcelProperty(index = 5, value = "黑名单用户ID")
    @ColumnWidth(20)
    private String blackUid;
    
    @ExcelProperty(index = 6, value = "黑名单用户昵称")
    @ColumnWidth(20)
    private String blackNickname;
    
    @ExcelProperty(index = 7, value = "操作员ID")
    @ColumnWidth(20)
    private String userUid;
    
    @ExcelProperty(index = 8, value = "操作员昵称")
    @ColumnWidth(20)
    private String userNickname;
}
