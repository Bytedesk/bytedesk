/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-03-27 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-03-27 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.service.agent_status;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * Agent status Excel export model
 * https://github.com/alibaba/easyexcel
 */
@Data
public class AgentStatusExcel {

    @ExcelProperty(index = 0, value = "客服昵称")
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(index = 1, value = "客服UID")
    @ColumnWidth(25)
    private String agentUid;

    @ExcelProperty(index = 2, value = "状态")
    @ColumnWidth(15)
    private String status;

    @ExcelProperty(index = 3, value = "小休原因")
    @ColumnWidth(30)
    private String restReason;

    @ExcelProperty(index = 4, value = "持续时长")
    @ColumnWidth(18)
    private String duration;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;
}