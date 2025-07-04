/*
 * @Author: jackning 270580206@qq.com
 * @Date: 2025-05-06 09:36:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-26 15:45:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580206@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.bytedesk.core.base.BaseExcel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 访客信息导出Excel实体类
 * https://github.com/alibaba/easyexcel
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VisitorExcel extends BaseExcel {
    
    @ExcelProperty(value = "昵称")
    @ColumnWidth(25)
    private String nickname;

    // @ExcelProperty(value = "语言")
    // @ColumnWidth(10)
    // private String lang;
    
    // @ExcelProperty(value = "设备")
    // @ColumnWidth(30)
    // private String device;
    
    @ExcelProperty(value = "手机号")
    @ColumnWidth(20)
    private String mobile;
    
    @ExcelProperty(value = "邮箱")
    @ColumnWidth(20)
    private String email;
    
    @ExcelProperty(value = "备注")
    @ColumnWidth(25)
    private String note;
    
    @ExcelProperty(value = "客户端")
    @ColumnWidth(20)
    private String client;
    
    // @ExcelProperty(value = "状态")
    // @ColumnWidth(20)
    // private String status;
    
    // @ExcelProperty(value = "标签")
    // @ColumnWidth(30)
    // private String tags;
    
    // @ExcelProperty(value = "附加信息")
    // @ColumnWidth(40)
    // private String extra;
    
    @ExcelProperty(value = "IP地址")
    @ColumnWidth(20)
    private String ip;
    
    @ExcelProperty(value = "IP所在地")
    @ColumnWidth(30)
    private String ipLocation;
    
    @ExcelProperty(value = "会员等级")
    @ColumnWidth(20)
    private Integer vipLevel;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;
}
