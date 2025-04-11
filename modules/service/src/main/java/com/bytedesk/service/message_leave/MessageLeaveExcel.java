/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-09 09:22:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 10:00:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO: 需要添加更多的字段
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageLeaveExcel {

    @ExcelProperty(value = "联系方式")
    @ColumnWidth(15)
    private String contact;
    
    @ExcelProperty(value = "留言内容")
    @ColumnWidth(20)
    private String content;
    
    @ExcelProperty(value = "图片")
    @ColumnWidth(10)
    private String images;
    
    @ExcelProperty(value = "状态")
    @ColumnWidth(10)
    private String status;
    
    // @ExcelProperty(value = "分类")
    // @ColumnWidth(10)
    // private String category;
    
    // @ExcelProperty(value = "优先级")
    // @ColumnWidth(10)
    // private String priority;
    
    // @ExcelProperty(value = "处理时间")
    // @ColumnWidth(20)
    // private String handleTimeStr;
    
    // @ExcelProperty(value = "处理备注")
    // @ColumnWidth(20)
    // private String handleRemark;
    
    // @ExcelProperty(value = "关联工单")
    // @ColumnWidth(15)
    // private String ticketUid;
    
    // @ExcelProperty(value = "关联会话")
    // @ColumnWidth(15)
    // private String threadUid;
    
    // @ExcelProperty(value = "客户来源")
    // @ColumnWidth(10)
    // private String client;
    
    // @ExcelProperty(value = "设备信息")
    // @ColumnWidth(15)
    // private String deviceInfo;
    
    // @ExcelProperty(value = "IP地址")
    // @ColumnWidth(15)
    // private String ipAddress;
    
    // @ExcelProperty(value = "地理位置")
    // @ColumnWidth(15)
    // private String location;
    
    // @ExcelProperty(value = "标签")
    // @ColumnWidth(15)
    // private String tagList;
    
    @ExcelProperty(value = "用户")
    @ColumnWidth(15)
    private String user;
    
    // @ExcelProperty(value = "处理人")
    // @ColumnWidth(15)
    // private String handler;
    
    @ExcelProperty(value = "创建时间")
    @ColumnWidth(20)
    private String createdAt;
}
