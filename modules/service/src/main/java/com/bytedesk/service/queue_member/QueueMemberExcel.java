/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-04 09:59:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

// https://github.com/alibaba/easyexcel
// https://easyexcel.opensource.alibaba.com/docs/current/
@Data
public class QueueMemberExcel {

    @ExcelProperty(index = 0, value = "队列名称")
    @ColumnWidth(20)
    private String queueNickname;

    @ExcelProperty(index = 1, value = "访客昵称")
    @ColumnWidth(20)
    private String visitorNickname;

    @ExcelProperty(index = 2, value = "客服昵称")
    @ColumnWidth(20)
    private String agentNickname;

    @ExcelProperty(index = 3, value = "工作组名称")
    @ColumnWidth(20)
    private String workgroupName;

    @ExcelProperty(index = 4, value = "排队号码")
    @ColumnWidth(10)
    private Integer queueNumber;

    @ExcelProperty(index = 5, value = "前面排队人数")
    @ColumnWidth(10)
    private Integer beforeNumber;

    @ExcelProperty(index = 6, value = "等待时间(秒)")
    @ColumnWidth(15)
    private Integer waitTime;

    @ExcelProperty(index = 7, value = "状态")
    @ColumnWidth(15)
    private String status;

    @ExcelProperty(index = 8, value = "加入时间")
    @ColumnWidth(20)
    private String enqueueTime;

    @ExcelProperty(index = 9, value = "接单方式")
    @ColumnWidth(15)
    private String acceptType;

    @ExcelProperty(index = 10, value = "开始服务时间")
    @ColumnWidth(20)
    private String acceptTime;

    @ExcelProperty(index = 11, value = "首次响应时间")
    @ColumnWidth(20)
    private String firstResponseTime;

    @ExcelProperty(index = 12, value = "平均响应时间(秒)")
    @ColumnWidth(15)
    private Integer avgResponseTime;

    @ExcelProperty(index = 13, value = "最长响应时间(秒)")
    @ColumnWidth(15)
    private Integer maxResponseTime;

    @ExcelProperty(index = 14, value = "客服消息数")
    @ColumnWidth(10)
    private Integer agentMessageCount;

    @ExcelProperty(index = 15, value = "访客消息数")
    @ColumnWidth(10)
    private Integer visitorMessageCount;

    @ExcelProperty(index = 16, value = "是否超时")
    @ColumnWidth(10)
    private Boolean timeout;

    @ExcelProperty(index = 17, value = "最后响应时间")
    @ColumnWidth(20)
    private String lastResponseTime;

    @ExcelProperty(index = 18, value = "离开时间")
    @ColumnWidth(20)
    private String leaveTime;

    @ExcelProperty(index = 19, value = "结束时间")
    @ColumnWidth(20)
    private String closeTime;

    @ExcelProperty(index = 20, value = "优先级")
    @ColumnWidth(10)
    private Integer priority;

    @ExcelProperty(index = 21, value = "是否已解决")
    @ColumnWidth(10)
    private Boolean solved;

    @ExcelProperty(index = 22, value = "是否已评价")
    @ColumnWidth(10)
    private Boolean rated;

    @ExcelProperty(index = 23, value = "客户端类型")
    @ColumnWidth(15)
    private String client;

    /**
     * 从QueueMemberEntity创建QueueMemberExcel
     */
    // public static QueueMemberExcel from(QueueMemberEntity member) {
    //     QueueMemberExcel excel = new QueueMemberExcel();
    //     excel.setQueueNickname(member.getQueueNickname());
    //     excel.setVisitorNickname(member.getVisitorNickname());
    //     excel.setAgentNickname(member.getAgentNickname());
    //     excel.setWorkgroupName(member.getWorkgroupName());
    //     excel.setQueueNumber(member.getQueueNumber());
    //     excel.setBeforeNumber(member.getBeforeNumber());
    //     excel.setWaitTime(member.getWaitTime());
    //     excel.setStatus(member.getStatus());
    //     excel.setEnqueueTime(member.getEnqueueTime() != null ? member.getEnqueueTime().toString() : null);
    //     excel.setAcceptType(member.getAcceptType());
    //     excel.setAcceptTime(member.getAcceptTime() != null ? member.getAcceptTime().toString() : null);
    //     excel.setFirstResponseTime(member.getFirstResponseTime() != null ? member.getFirstResponseTime().toString() : null);
    //     excel.setAvgResponseTime(member.getAvgResponseTime());
    //     excel.setMaxResponseTime(member.getMaxResponseTime());
    //     excel.setAgentMessageCount(member.getAgentMessageCount());
    //     excel.setVisitorMessageCount(member.getVisitorMessageCount());
    //     excel.setTimeout(member.isTimeout());
    //     excel.setLastResponseTime(member.getLastResponseTime() != null ? member.getLastResponseTime().toString() : null);
    //     excel.setLeaveTime(member.getLeaveTime() != null ? member.getLeaveTime().toString() : null);
    //     excel.setCloseTime(member.getCloseTime() != null ? member.getCloseTime().toString() : null);
    //     excel.setPriority(member.getPriority());
    //     excel.setSolved(member.isSolved());
    //     excel.setRated(member.isRated());
    //     excel.setClient(member.getClient());
    //     return excel;
    // }
}
