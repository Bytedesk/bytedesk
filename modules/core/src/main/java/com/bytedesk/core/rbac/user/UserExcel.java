/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-25 11:28:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 11:48:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

@Data
public class UserExcel {

    @ExcelProperty(index = 0, value = "用户名")
    @ColumnWidth(20)
    private String username;

    @ExcelProperty(index = 1, value = "昵称")
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(index = 2, value = "邮箱")
    @ColumnWidth(20)
    private String email;

    @ExcelProperty(index = 3, value = "手机")
    @ColumnWidth(20)
    private String mobile;

    @ExcelProperty(index = 4, value = "描述")
    @ColumnWidth(20)
    private String description;

    // @ExcelProperty(index = 4, value = "头像")
    // @ColumnWidth(20)
    // private String avatar;

    // @ExcelProperty(index = 5, value = "平台")
    // @ColumnWidth(20)
    // private String platform;

    // @ExcelProperty(index = 6, value = "组织")
    // @ColumnWidth(20)
    // private String orgUid;
}
