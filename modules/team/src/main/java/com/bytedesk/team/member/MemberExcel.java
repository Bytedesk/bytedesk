/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-01 06:21:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

// https://github.com/alibaba/easyexcel
// https://easyexcel.opensource.alibaba.com/docs/current/
@Data
public class MemberExcel {

    @ExcelProperty(index = 0, value = "昵称")
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(index = 1, value = "Email")
    @ColumnWidth(20)
    private String email;

    @ExcelProperty(index = 2, value = "手机")
    @ColumnWidth(20)
    private String mobile;

    @ExcelProperty(index = 3, value = "工号")
    @ColumnWidth(20)
    private String jobNo;

    @ExcelProperty(index = 4, value = "职位")
    @ColumnWidth(20)
    private String jobTitle;

    @ExcelProperty(index = 5, value = "座位号")
    @ColumnWidth(20)
    private String seatNo;

    @ExcelProperty(index = 6, value = "座机")
    @ColumnWidth(20)
    private String telephone;
    
}
