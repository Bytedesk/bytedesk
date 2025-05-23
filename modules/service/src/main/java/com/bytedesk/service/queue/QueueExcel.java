/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 14:26:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.bytedesk.core.base.BaseExcel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
// import lombok.experimental.SuperBuilder;

// https://github.com/alibaba/easyexcel
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class QueueExcel extends BaseExcel {

    @ExcelProperty(value = "监控名称")
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(value = "监控类型")
    @ColumnWidth(15)
    private String type;

    // @ExcelProperty(value = "队列主题")
    // @ColumnWidth(20)
    // private String topic;

    @ExcelProperty(value = "日期")
    @ColumnWidth(15)
    private String day;

    // @ExcelProperty(value = "队列状态")
    // @ColumnWidth(15)
    // private String status;

    @ExcelProperty(value = "总请求数")
    @ColumnWidth(25)
    private Integer totalCount;

    @ExcelProperty(value = "机器人对话中")
    @ColumnWidth(25)
    private Integer robotingCount;

    @ExcelProperty(value = "离线留言")
    @ColumnWidth(20)
    private Integer offlineCount;

    @ExcelProperty(value = "留言数")
    @ColumnWidth(15)
    private Integer messageLeaveCount;

    @ExcelProperty(value = "转人工数")
    @ColumnWidth(15)
    private Integer robotToAgentCount;

    @ExcelProperty(value = "排队中")
    @ColumnWidth(15)
    private Integer queuingCount;

    @ExcelProperty(value = "会话中")
    @ColumnWidth(25)
    private Integer chattingCount;

    @ExcelProperty(value = "已结束")
    @ColumnWidth(25)
    private Integer closedCount;

}
