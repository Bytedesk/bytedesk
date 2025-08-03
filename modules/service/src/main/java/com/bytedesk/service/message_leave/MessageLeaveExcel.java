/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-09 09:22:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 15:49:24
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
import com.bytedesk.core.base.BaseExcel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageLeaveExcel extends BaseExcel {

    @ExcelProperty(value = "联系方式")
    @ColumnWidth(15)
    private String contact;
    
    @ExcelProperty(value = "留言内容")
    @ColumnWidth(20)
    private String content;
    
    @ExcelProperty(value = "留言类型")
    @ColumnWidth(15)
    private String type;
    
    @ExcelProperty(value = "图片")
    @ColumnWidth(10)
    private String images;

    @ExcelProperty(value = "附件")
    @ColumnWidth(10)
    private String attachments;

    @ExcelProperty(value = "回复内容")
    @ColumnWidth(20)
    private String reply;

    @ExcelProperty(value = "回复图片")
    @ColumnWidth(10)
    private String replyImages;

    @ExcelProperty(value = "回复附件")
    @ColumnWidth(10)
    private String replyAttachments;
    
    @ExcelProperty(value = "状态")
    @ColumnWidth(10)
    private String status;

    @ExcelProperty(value = "留言人")
    @ColumnWidth(15)
    private String user;

    @ExcelProperty(value = "回复人")
    @ColumnWidth(15)
    private String replyUser;
    
    @ExcelProperty(value = "留言时间")
    @ColumnWidth(20)
    private String createdAt;

    @ExcelProperty(value = "回复时间")
    @ColumnWidth(20)
    private String repliedAt;
}
