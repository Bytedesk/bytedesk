/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 16:14:24
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

    @ExcelProperty(value = "队列名称")
    @ColumnWidth(20)
    private String queueNickname;

    @ExcelProperty(value = "访客昵称")
    @ColumnWidth(20)
    private String visitorNickname;

    @ExcelProperty(value = "客服昵称")
    @ColumnWidth(20)
    private String agentNickname;

    @ExcelProperty(value = "工作组名称")
    @ColumnWidth(20)
    private String workgroupName;

    @ExcelProperty(value = "排队号码")
    @ColumnWidth(10)
    private Integer queueNumber;

    @ExcelProperty(value = "等待时长(秒)")
    @ColumnWidth(15)
    private Integer waitTime;

    @ExcelProperty(value = "状态")
    @ColumnWidth(15)
    private String status;

    @ExcelProperty(value = "客户端")
    @ColumnWidth(15)
    private String client;

    // 时间信息
    @ExcelProperty(value = "入队时间")
    @ColumnWidth(20)
    private String enqueueTime;

    @ExcelProperty(value = "访客首次消息时间")
    @ColumnWidth(20)
    private String visitorFirstMessageTime;

    @ExcelProperty(value = "访客最后消息时间")
    @ColumnWidth(20)
    private String visitorLastMessageTime;

    // @ExcelProperty(value = "离开时间")
    // @ColumnWidth(20)
    // private String leaveTime;

    // 人工客服相关
    @ExcelProperty(value = "接入方式")
    @ColumnWidth(15)
    private String agentAcceptType;

    @ExcelProperty(value = "接入时间")
    @ColumnWidth(20)
    private String agentAcceptTime;

    @ExcelProperty(value = "首次响应时间")
    @ColumnWidth(20)
    private String agentFirstResponseTime;

    @ExcelProperty(value = "最后响应时间")
    @ColumnWidth(20)
    private String agentLastResponseTime;

    // @ExcelProperty(value = "会话结束时间")
    // @ColumnWidth(20)
    // private String closeTime;

    // @ExcelProperty(value = "平均响应时长(秒)")
    // @ColumnWidth(15)
    // private Integer avgResponseTime;

    // @ExcelProperty(value = "最长响应时长(秒)")
    // @ColumnWidth(15)
    // private Integer maxResponseTime;

    @ExcelProperty(value = "客服消息数")
    @ColumnWidth(15)
    private Integer agentMessageCount;

    // @ExcelProperty(value = "是否超时")
    // @ColumnWidth(10)
    // private Boolean timeout;

    // @ExcelProperty(value = "客服是否离线")
    // @ColumnWidth(15)
    // private Boolean agentOffline;

    // 机器人相关
    @ExcelProperty(value = "机器人接入方式")
    @ColumnWidth(15)
    private String robotAcceptType;

    @ExcelProperty(value = "机器人接入时间")
    @ColumnWidth(20)
    private String robotAcceptTime;

    @ExcelProperty(value = "机器人首次响应时间")
    @ColumnWidth(20)
    private String robotFirstResponseTime;

    @ExcelProperty(value = "机器人最后响应时间")
    @ColumnWidth(20)
    private String robotLastResponseTime;

    // @ExcelProperty(value = "机器人结束时间")
    // @ColumnWidth(20)
    // private String robotCloseTime;

    // @ExcelProperty(value = "机器人平均响应时长(秒)")
    // @ColumnWidth(15)
    // private Integer robotAvgResponseTime;

    // @ExcelProperty(value = "机器人最长响应时长(秒)")
    // @ColumnWidth(15)
    // private Integer robotMaxResponseTime;

    // @ExcelProperty(value = "机器人消息数")
    // @ColumnWidth(15)
    // private Integer robotMessageCount;

    // @ExcelProperty(value = "机器人是否超时")
    // @ColumnWidth(15)
    // private Boolean robotTimeout;

    @ExcelProperty(value = "机器人转人工")
    @ColumnWidth(15)
    private String robotToAgent;

    // 消息统计
    @ExcelProperty(value = "访客消息数")
    @ColumnWidth(15)
    private Integer visitorMessageCount;

    // @ExcelProperty(value = "系统消息数")
    // @ColumnWidth(15)
    // private Integer systemMessageCount;

    // 评价与服务质量
    @ExcelProperty(value = "是否已评价")
    @ColumnWidth(15)
    private String rated;

    @ExcelProperty(value = "是否已解决")
    @ColumnWidth(15)
    private String resolved;

    // @ExcelProperty(value = "是否已质检")
    // @ColumnWidth(15)
    // private Boolean qualityChecked;

    // @ExcelProperty(value = "质检结果")
    // @ColumnWidth(15)
    // private String qualityCheckResult;

    // 留言与小结
    @ExcelProperty(value = "是否留言")
    @ColumnWidth(10)
    private String leaveMsg;

    @ExcelProperty(value = "留言时间")
    @ColumnWidth(20)
    private String leaveMsgAt;

    @ExcelProperty(value = "是否已小结")
    @ColumnWidth(15)
    private String summarized;

    // 交互状态
    // @ExcelProperty(value = "转接状态")
    // @ColumnWidth(15)
    // private String transferStatus;

    // @ExcelProperty(value = "邀请状态")
    // @ColumnWidth(15)
    // private String inviteStatus;

    // @ExcelProperty(value = "意图类型")
    // @ColumnWidth(15)
    // private String intentionType;

    // @ExcelProperty(value = "情绪类型")
    // @ColumnWidth(15)
    // private String emotionType;
}
