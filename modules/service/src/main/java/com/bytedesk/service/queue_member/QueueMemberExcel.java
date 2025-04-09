/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 15:16:44
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
    
    @ExcelProperty(index = 5, value = "前面人数")
    @ColumnWidth(10)
    private Integer beforeNumber;

    @ExcelProperty(index = 6, value = "等待时长(秒)")
    @ColumnWidth(15)
    private Integer waitTime;

    @ExcelProperty(index = 7, value = "状态")
    @ColumnWidth(15)
    private String status;

    @ExcelProperty(index = 8, value = "优先级")
    @ColumnWidth(10)
    private Integer priority;

    @ExcelProperty(index = 9, value = "客户端")
    @ColumnWidth(15)
    private String client;

    // 时间信息
    @ExcelProperty(index = 10, value = "入队时间")
    @ColumnWidth(20)
    private String enqueueTime;

    @ExcelProperty(index = 11, value = "访客首次消息时间")
    @ColumnWidth(20)
    private String visitorFirstMessageTime;

    @ExcelProperty(index = 12, value = "访客最后消息时间")
    @ColumnWidth(20)
    private String visitorLastMessageTime;

    @ExcelProperty(index = 13, value = "离开时间")
    @ColumnWidth(20)
    private String leaveTime;

    // 人工客服相关
    @ExcelProperty(index = 14, value = "接入方式")
    @ColumnWidth(15)
    private String acceptType;

    @ExcelProperty(index = 15, value = "接入时间")
    @ColumnWidth(20)
    private String acceptTime;

    @ExcelProperty(index = 16, value = "首次响应时间")
    @ColumnWidth(20)
    private String firstResponseTime;

    @ExcelProperty(index = 17, value = "最后响应时间")
    @ColumnWidth(20)
    private String lastResponseTime;

    @ExcelProperty(index = 18, value = "会话结束时间")
    @ColumnWidth(20)
    private String closeTime;

    @ExcelProperty(index = 19, value = "平均响应时长(秒)")
    @ColumnWidth(15)
    private Integer avgResponseTime;

    @ExcelProperty(index = 20, value = "最长响应时长(秒)")
    @ColumnWidth(15)
    private Integer maxResponseTime;

    @ExcelProperty(index = 21, value = "客服消息数")
    @ColumnWidth(15)
    private Integer agentMessageCount;

    @ExcelProperty(index = 22, value = "是否超时")
    @ColumnWidth(10)
    private Boolean timeout;

    @ExcelProperty(index = 23, value = "客服是否离线")
    @ColumnWidth(15)
    private Boolean agentOffline;

    // 机器人相关
    @ExcelProperty(index = 24, value = "机器人接入方式")
    @ColumnWidth(15)
    private String robotAcceptType;

    @ExcelProperty(index = 25, value = "机器人接入时间")
    @ColumnWidth(20)
    private String robotAcceptTime;

    @ExcelProperty(index = 26, value = "机器人首次响应时间")
    @ColumnWidth(20)
    private String robotFirstResponseTime;

    @ExcelProperty(index = 27, value = "机器人最后响应时间")
    @ColumnWidth(20)
    private String robotLastResponseTime;

    @ExcelProperty(index = 28, value = "机器人结束时间")
    @ColumnWidth(20)
    private String robotCloseTime;

    @ExcelProperty(index = 29, value = "机器人平均响应时长(秒)")
    @ColumnWidth(15)
    private Integer robotAvgResponseTime;

    @ExcelProperty(index = 30, value = "机器人最长响应时长(秒)")
    @ColumnWidth(15)
    private Integer robotMaxResponseTime;

    @ExcelProperty(index = 31, value = "机器人消息数")
    @ColumnWidth(15)
    private Integer robotMessageCount;

    @ExcelProperty(index = 32, value = "机器人是否超时")
    @ColumnWidth(15)
    private Boolean robotTimeout;

    @ExcelProperty(index = 33, value = "机器人转人工")
    @ColumnWidth(15)
    private Boolean robotToAgent;

    // 消息统计
    @ExcelProperty(index = 34, value = "访客消息数")
    @ColumnWidth(15)
    private Integer visitorMessageCount;

    @ExcelProperty(index = 35, value = "系统消息数")
    @ColumnWidth(15)
    private Integer systemMessageCount;

    // 评价与服务质量
    @ExcelProperty(index = 36, value = "是否已评价")
    @ColumnWidth(15)
    private Boolean rated;

    @ExcelProperty(index = 38, value = "是否已解决")
    @ColumnWidth(15)
    private Boolean resolved;

    @ExcelProperty(index = 40, value = "是否已质检")
    @ColumnWidth(15)
    private Boolean qualityChecked;

    @ExcelProperty(index = 41, value = "质检结果")
    @ColumnWidth(15)
    private String qualityCheckResult;

    // 留言与小结
    @ExcelProperty(index = 42, value = "是否留言")
    @ColumnWidth(10)
    private Boolean leaveMsg;

    @ExcelProperty(index = 43, value = "留言时间")
    @ColumnWidth(20)
    private String leaveMsgTime;

    @ExcelProperty(index = 44, value = "是否已小结")
    @ColumnWidth(15)
    private Boolean summarized;

    // 交互状态
    @ExcelProperty(index = 45, value = "转接状态")
    @ColumnWidth(15)
    private String transferStatus;

    @ExcelProperty(index = 46, value = "邀请状态")
    @ColumnWidth(15)
    private String inviteStatus;

    @ExcelProperty(index = 47, value = "意图类型")
    @ColumnWidth(15)
    private String intentionType;

    @ExcelProperty(index = 48, value = "情绪类型")
    @ColumnWidth(15)
    private String emotionType;
}
