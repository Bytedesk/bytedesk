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

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * 用户导出Excel实体，列顺序与 admin 前端 UserTable 表格列对齐：
 * 昵称、用户名、手机、邮箱、当前组织、当前角色、描述、启用、超级管理员、性别、注册来源、创建时间、更新时间
 */
@Data
public class UserExcel {

    @ExcelProperty(index = 0, value = "昵称")
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(index = 1, value = "用户名")
    @ColumnWidth(20)
    private String username;

    @ExcelProperty(index = 2, value = "手机")
    @ColumnWidth(20)
    private String mobile;

    @ExcelProperty(index = 3, value = "邮箱")
    @ColumnWidth(25)
    private String email;

    /** 当前组织名称（来自 currentOrganization.name） */
    @ExcelProperty(index = 4, value = "当前组织")
    @ColumnWidth(20)
    private String currentOrganizationName;

    /** 当前角色名称，逗号分隔（导出时按语言逐个翻译） */
    @ExcelProperty(index = 5, value = "当前角色")
    @ColumnWidth(25)
    private String currentRoles;

    @ExcelProperty(index = 6, value = "描述")
    @ColumnWidth(25)
    private String description;

    @ExcelProperty(index = 7, value = "启用")
    @ColumnWidth(10)
    private Boolean enabled;

    @ExcelProperty(index = 8, value = "超级管理员")
    @ColumnWidth(12)
    private Boolean superUser;

    /** UserEntity.Sex 枚举名：MALE/FEMALE/UNKNOWN */
    @ExcelProperty(index = 9, value = "性别")
    @ColumnWidth(10)
    private String sex;

    /** UserEntity.RegisterSource 枚举名 */
    @ExcelProperty(index = 10, value = "注册来源")
    @ColumnWidth(15)
    private String registerSource;

    @ExcelProperty(index = 11, value = "创建时间")
    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime createdAt;

    @ExcelProperty(index = 12, value = "更新时间")
    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime updatedAt;
}
