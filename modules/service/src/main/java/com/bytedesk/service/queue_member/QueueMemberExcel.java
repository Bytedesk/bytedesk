/*
 * @Author: jackning 270580206@qq.com
 * @Date: 2024-08-01 06:18:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 15:23:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580206@qq.com 
 *  联系：270580206@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.bytedesk.core.base.BaseExcel;

import lombok.Data;
import lombok.EqualsAndHashCode;

// https://github.com/alibaba/easyexcel
// https://easyexcel.opensource.alibaba.com/docs/current/
@Data
@EqualsAndHashCode(callSuper = true)    
public class QueueMemberExcel extends BaseExcel {

    // 按照前端代码中的列顺序排列
    @ExcelProperty(value = "访客昵称")
    @ColumnWidth(25)
    private String visitorNickname;

    @ExcelProperty(value = "序号")
    @ColumnWidth(20)
    private Integer queueNumber;

    @ExcelProperty(value = "监控名称")
    @ColumnWidth(20)
    private String queueNickname;

    @ExcelProperty(value = "机器人")
    @ColumnWidth(20)
    private String robotNickname;

    @ExcelProperty(value = "等待时长(秒)")
    @ColumnWidth(20)
    private Integer waitLength;

    @ExcelProperty(value = "入队时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(25)
    private ZonedDateTime visitorEnqueueAt;

    @ExcelProperty(value = "状态")
    @ColumnWidth(20)
    private String status;

    @ExcelProperty(value = "机器人接入方式")
    @ColumnWidth(20)
    private String robotAcceptType;

    @ExcelProperty(value = "机器人接入时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(25)
    private ZonedDateTime robotAcceptedAt;

    @ExcelProperty(value = "机器人转人工")
    @ColumnWidth(20)
    private String robotToAgent;

    @ExcelProperty(value = "转人工时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(25)
    private ZonedDateTime robotToAgentAt;

    @ExcelProperty(value = "客服昵称")
    @ColumnWidth(20)
    private String agentNickname;

    @ExcelProperty(value = "客服是否离线")
    @ColumnWidth(20)
    private String agentOffline;

    @ExcelProperty(value = "接入方式")
    @ColumnWidth(20)
    private String agentAcceptType;

    @ExcelProperty(value = "接入时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(25)
    private ZonedDateTime agentAcceptedAt;

    @ExcelProperty(value = "是否已解决")
    @ColumnWidth(20)
    private String resolved;

    @ExcelProperty(value = "是否已评价")
    @ColumnWidth(20)
    private String rated;

    @ExcelProperty(value = "评分时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(25)
    private ZonedDateTime rateAt;

    @ExcelProperty(value = "是否已小结")
    @ColumnWidth(20)
    private String summarized;

    @ExcelProperty(value = "访客消息数")
    @ColumnWidth(20)
    private Integer visitorMessageCount;

    @ExcelProperty(value = "客服消息数")
    @ColumnWidth(20)
    private Integer agentMessageCount;

    @ExcelProperty(value = "机器人消息数")
    @ColumnWidth(20)
    private Integer robotMessageCount;

    @ExcelProperty(value = "是否留言")
    @ColumnWidth(20)
    private String messageLeave;

    // @ExcelProperty(value = "留言时间")
    // @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    // @ColumnWidth(25)
    // private ZonedDateTime messageLeaveAt;

    @ExcelProperty(value = "客户端")
    @ColumnWidth(20)
    private String client;
}
