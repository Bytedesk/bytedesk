/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 09:56:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

// https://github.com/alibaba/easyexcel
// https://easyexcel.opensource.alibaba.com/docs/current/
@Data
public class MemberExcelExport {

    @ExcelProperty(value = "昵称")
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(value = "邮箱")
    @ColumnWidth(20)
    private String email;

    @ExcelProperty(value = "手机")
    @ColumnWidth(20)
    private String mobile;

    @ExcelProperty(value = "工号")
    @ColumnWidth(20)
    private String jobNo;

    @ExcelProperty(value = "职位")
    @ColumnWidth(20)
    private String jobTitle;

    @ExcelProperty(value = "部门")
    @ColumnWidth(20)
    private String departmentName;

    @ExcelProperty(value = "座位号")
    @ColumnWidth(20)
    private String seatNo;

    @ExcelProperty(value = "分机号")
    @ColumnWidth(20)
    private String telephone;
    
}
