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

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

// https://github.com/alibaba/easyexcel
// https://easyexcel.opensource.alibaba.com/docs/current/
// 字段与 admin 前端 MemberTable 列对齐（不含所属组织列）：昵称、用户名、手机、邮箱、工号、职位、座位号、分机号、部门、角色、可登录平台、创建时间、更新时间
@Data
public class MemberExcelExport {

    @ExcelProperty(value = "昵称")
    @ColumnWidth(20)
    private String nickname;

    // 登录用户名，来自关联的 UserEntity（导出时由 convertToExcel 填充）
    @ExcelProperty(value = "用户名")
    @ColumnWidth(20)
    private String username;

    @ExcelProperty(value = "手机")
    @ColumnWidth(20)
    private String mobile;

    @ExcelProperty(value = "邮箱")
    @ColumnWidth(20)
    private String email;

    @ExcelProperty(value = "工号")
    @ColumnWidth(20)
    private String jobNo;

    // 职位，可能为 i18n key（如 i18n.admin），导出时按请求语言翻译
    @ExcelProperty(value = "职位")
    @ColumnWidth(20)
    private String jobTitle;

    @ExcelProperty(value = "座位号")
    @ColumnWidth(20)
    private String seatNo;

    @ExcelProperty(value = "分机号")
    @ColumnWidth(20)
    private String telephone;

    @ExcelProperty(value = "部门")
    @ColumnWidth(20)
    private String departmentName;

    // 角色名称，逗号分隔（如 ROLE_ADMIN, ROLE_USER），导出时逐个按语言翻译
    @ExcelProperty(value = "角色")
    @ColumnWidth(22)
    private String roles;

    // 可登录平台，逗号分隔（如 admin, desktop）
    @ExcelProperty(value = "可登录平台")
    @ColumnWidth(22)
    private String allowedLoginPlatforms;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    @ColumnWidth(20)
    private ZonedDateTime createdAt;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "更新时间")
    @ColumnWidth(20)
    private ZonedDateTime updatedAt;

}
