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
    
    // @ExcelProperty(index = 4, value = "真实姓名")
    // @ColumnWidth(15)
    // private String realname;
    
    // @ExcelProperty(index = 5, value = "性别")
    // @ColumnWidth(10)
    // private String gender;
    
    // @ExcelProperty(index = 6, value = "生日")
    // @ColumnWidth(15)
    // private String birthday;
    
    // @ExcelProperty(index = 7, value = "微信")
    // @ColumnWidth(20)
    // private String wechat;
    
    // @ExcelProperty(index = 8, value = "QQ")
    // @ColumnWidth(15)
    // private String qq;
    
    // @ExcelProperty(index = 9, value = "固定电话")
    // @ColumnWidth(15)
    // private String telephone;
    
    // @ExcelProperty(index = 10, value = "公司名称")
    // @ColumnWidth(25)
    // private String company;
    
    // @ExcelProperty(index = 11, value = "职位")
    // @ColumnWidth(20)
    // private String position;
    
    // @ExcelProperty(index = 12, value = "所属行业")
    // @ColumnWidth(20)
    // private String industry;
    
    // @ExcelProperty(index = 13, value = "详细地址")
    // @ColumnWidth(40)
    // private String address;
    
    // @ExcelProperty(index = 14, value = "省份")
    // @ColumnWidth(15)
    // private String province;
    
    // @ExcelProperty(index = 15, value = "城市")
    // @ColumnWidth(15)
    // private String city;
    
    // @ExcelProperty(index = 16, value = "区县")
    // @ColumnWidth(15)
    // private String district;
    
    // @ExcelProperty(index = 17, value = "邮编")
    // @ColumnWidth(10)
    // private String zipCode;
    
    // @ExcelProperty(index = 18, value = "客户类别")
    // @ColumnWidth(15)
    // private String category;
    
    // @ExcelProperty(index = 19, value = "客户级别")
    // @ColumnWidth(10)
    // private String crmlevel;
    
    // @ExcelProperty(index = 20, value = "优先级")
    // @ColumnWidth(10)
    // private String priority;
    
    // @ExcelProperty(index = 21, value = "标签")
    // @ColumnWidth(30)
    // private String tags;
    
    // @ExcelProperty(index = 22, value = "来源")
    // @ColumnWidth(15)
    // private String source;
    
    // @ExcelProperty(index = 23, value = "客户状态")
    // @ColumnWidth(15)
    // private String status;
    
    // @ExcelProperty(index = 24, value = "负责人")
    // @ColumnWidth(15)
    // private String ownerName;
    
    // @ExcelProperty(index = 25, value = "最后联系时间")
    // @ColumnWidth(20)
    // private String lastContactTime;
    
    // @ExcelProperty(index = 26, value = "下次联系时间")
    // @ColumnWidth(20)
    // private String nextContactTime;
    
    // @ExcelProperty(index = 27, value = "累计金额")
    // @ColumnWidth(15)
    // private String totalAmount;
    
    // @ExcelProperty(index = 28, value = "成交次数")
    // @ColumnWidth(10)
    // private String dealCount;
    
    // @ExcelProperty(index = 29, value = "最后成交时间")
    // @ColumnWidth(20)
    // private String lastDealTime;
    
    @ExcelProperty(value = "附加信息")
    @ColumnWidth(40)
    private String extra;
    
    @ExcelProperty(value = "备注")
    @ColumnWidth(40)
    private String notes;
}
