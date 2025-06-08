/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.gateway;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * FreeSwitch网关Excel导出实体
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FreeSwitchGatewayExcel {

    @ExcelProperty("网关名称")
    @ColumnWidth(20)
    private String gatewayName;

    @ExcelProperty("描述")
    @ColumnWidth(30)
    private String description;

    @ExcelProperty("代理地址")
    @ColumnWidth(30)
    private String proxy;

    @ExcelProperty("用户名")
    @ColumnWidth(20)
    private String username;

    @ExcelProperty("从号码")
    @ColumnWidth(20)
    private String fromUser;

    @ExcelProperty("从域名")
    @ColumnWidth(20)
    private String fromDomain;

    @ExcelProperty("注册")
    @ColumnWidth(10)
    private String register;

    @ExcelProperty("传输协议")
    @ColumnWidth(15)
    private String registerTransport;

    @ExcelProperty("状态")
    @ColumnWidth(10)
    private String status;

    @ExcelProperty("启用状态")
    @ColumnWidth(10)
    private String enabled;

    @ExcelProperty("在线状态")
    @ColumnWidth(10)
    private String online;

    @ExcelProperty("备注")
    @ColumnWidth(40)
    private String remarks;

    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ExcelProperty("更新时间")
    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 从实体转换为Excel对象
     */
    public static FreeSwitchGatewayExcel fromEntity(FreeSwitchGatewayEntity entity) {
        return FreeSwitchGatewayExcel.builder()
                .gatewayName(entity.getGatewayName())
                .description(entity.getDescription())
                .proxy(entity.getProxy())
                .username(entity.getUsername())
                .fromUser(entity.getFromUser())
                .fromDomain(entity.getFromDomain())
                .register(entity.getRegister() ? "是" : "否")
                .registerTransport(entity.getRegisterTransport())
                .status(entity.getStatus())
                .enabled(entity.getEnabled() ? "启用" : "禁用")
                .online(entity.isOnline() ? "在线" : "离线")
                .remarks(entity.getRemarks())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
