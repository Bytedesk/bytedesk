/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 09:53:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.bytedesk.core.base.BaseExcel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户信息导出Excel模型
 * https://github.com/alibaba/easyexcel
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerExcel extends BaseExcel {
    
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "昵称")
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(value = "邮箱")
    @ColumnWidth(30)
    private String email;

    @ExcelProperty(value = "手机号")
    @ColumnWidth(15)
    private String mobile;

    @ExcelProperty(value = "描述")
    @ColumnWidth(30)
    private String description;
    
    @ExcelProperty(value = "附加信息")
    @ColumnWidth(40)
    private String extra;
    
    @ExcelProperty(value = "备注")
    @ColumnWidth(40)
    private String notes;
}
