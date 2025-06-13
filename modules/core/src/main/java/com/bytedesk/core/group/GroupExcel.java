/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 23:58:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

// https://github.com/alibaba/easyexcel
// https://easyexcel.opensource.alibaba.com/docs/current/
@Data
public class GroupExcel {

    @ExcelProperty(value = "群组ID")
    @ColumnWidth(20)
    private String uid;

    @ExcelProperty(value = "群组名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(value = "群组头像")
    @ColumnWidth(50)
    private String avatar;

    @ExcelProperty(value = "群组描述")
    @ColumnWidth(50)
    private String description;

    @ExcelProperty(value = "群组类型")
    @ColumnWidth(15)
    private String type;

    @ExcelProperty(value = "群组状态")
    @ColumnWidth(15)
    private String status;

    @ExcelProperty(value = "是否外部群")
    @ColumnWidth(15)
    private String isExternal;

    @ExcelProperty(value = "创建者")
    @ColumnWidth(20)
    private String creator;

    @ExcelProperty(value = "成员数量")
    @ColumnWidth(15)
    private Integer memberCount;

    @ExcelProperty(value = "管理员数量")
    @ColumnWidth(15)
    private Integer adminCount;

    @ExcelProperty(value = "最大成员数")
    @ColumnWidth(15)
    private Integer maxMembers;

    @ExcelProperty(value = "是否需要审批")
    @ColumnWidth(15)
    private String needApproval;

    @ExcelProperty(value = "是否允许邀请")
    @ColumnWidth(15)
    private String allowInvite;

    @ExcelProperty(value = "是否全员禁言")
    @ColumnWidth(15)
    private String muteAll;

    @ExcelProperty(value = "是否显示顶部通知")
    @ColumnWidth(15)
    private String showTopTip;

    @ExcelProperty(value = "顶部通知内容")
    @ColumnWidth(50)
    private String topTip;

    @ExcelProperty(value = "创建时间")
    @ColumnWidth(20)
    private String createdAt;

    @ExcelProperty(value = "更新时间")
    @ColumnWidth(20)
    private String updatedAt;

    // 转换布尔值为是/否
    public void setIsExternal(boolean value) {
        this.isExternal = value ? "是" : "否";
    }

    public void setNeedApproval(boolean value) {
        this.needApproval = value ? "是" : "否";
    }

    public void setAllowInvite(boolean value) {
        this.allowInvite = value ? "是" : "否";
    }

    public void setMuteAll(boolean value) {
        this.muteAll = value ? "是" : "否";
    }

    public void setShowTopTip(boolean value) {
        this.showTopTip = value ? "是" : "否";
    }
}
