/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 13:57:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-23 17:50:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Embeddable
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StatisticIndex {

    ////////////////////////////// 人工会话数据 ///////////////////////////////
    // 正在排队人数/会话数
    @Builder.Default
    private int queuingThreadCount = 0;

    // 人工正在接待人数
    @Builder.Default
    private int currentThreadCount = 0;

    // 人工已接待人数
    @Builder.Default
    private int totalVisitorCount = 0;

    // 人工已接待会话数
    @Builder.Default
    private int totalThreadCount = 0;

    // 预警会话量
    @Builder.Default
    private int warningThreadCount = 0;

    // 待处理留言量
    @Builder.Default
    private int unprocessedMessageCount = 0;

    // 当前在线接待客服数
    @Builder.Default
    private Integer onlineAgentCount = 0;

    ///////////////////////////////// 机器人会话数据 ///////////////////////////////

    // 机器人接待人次
    @Builder.Default
    private int robotVisitorCount = 0;

    // 机器人接待会话量
    @Builder.Default
    private int robotThreadCount = 0;

    //////////////////////////////////// 绩效考核指标//////////////////////////////

    // 3分钟人工回复率
    @Builder.Default
    private int threeMinuteReplyRate = 0;

    // 平均人工首次响应时长
    @Builder.Default
    private int firstResponseTime = 0;

    // 平均人工响应时长
    @Builder.Default
    private int averageResponseTime = 0;

    // 平均人工服务时长
    @Builder.Default
    private int averageServiceTime = 0;

    // 评价满意率
    @Builder.Default
    private int rateSatisfactionRate = 0;

    // 问题解决率
    @Builder.Default
    private int problemSolveRate = 0;

    //////////////////////////////////// 工单数据 ////////////////////////////////

    // 待分配工单
    @Builder.Default
    private int unassignedTicketCount = 0;

    // 处理中工单
    @Builder.Default
    private int processingTicketCount = 0;

    // 已完成工单
    @Builder.Default
    private int completedTicketCount = 0;

    ///////////////////////////////////////////////////////////////////////////

    // // 会话量：本日累计的会话数量，包含了访客来访和客服主动发起会话两种
    // private Long threadCount;

    // 未接入会话量：放弃排队的数量
    // private Long unattendedThreadCount;

    // 接入率：今日累计已已接入会话与总会话数的比值
    // private Double attendeeRate;

    // 满意度：满意数量与评价总量的比值
    // private Double satisfactionRate;

    // 参评率： 今日参加评价的数量与接入会话数量的比值
    // private Double evaluationRate;

    ///////////////////////////////////////////////////////////////////////////

    public void incrementThreadCount() {
        this.currentThreadCount++;
    }

    public void decrementThreadCount() {
        this.currentThreadCount--;
    }

}
