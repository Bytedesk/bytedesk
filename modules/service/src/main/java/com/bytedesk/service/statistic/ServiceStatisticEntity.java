/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-10 09:17:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 14:07:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 数据总览:
 实时的写到redis，定时同步到mysql

 1. 正在接待人数： 当前正在咨询的访客数量
 3. 当前排队人数：当前处于排队状态的访客数量
 5. 会话量：本日累计的会话数量，包含了访客来访和客服主动发起会话两种
 6. 未接入会话量：放弃排队的数量
 7. 接入率：今日累计已已接入会话与总会话数的比值
 8. 满意率：满意数量与评价总量的比值
 9. 参评率： 今日参加评价的数量与接入会话数量的比值
 10. 当前在线客服数：当前处于可接待状态的客服数量
 11. 坐席当前接待数量
 12. 当前渠道接待人数
 13. 转接量：转出会话的数量，转出的会话不计入接入会话数中
 14. 客服邀请量：客服邀请其他客服，并成功加入会话的数量
 15. 访客邀请量：客服主动邀请访客，并成功加入会话的数量
 16. 接待人数：区别于会话量，1个访客可以对应多个会话

 //
 数量统计：咨询总量、有效咨询、无效咨询、接通率、已总结咨询数、客服消息条数、会话回合数；
 时间统计：平均首响时间、平均响应时间、平均咨询用时、最长响应时间、最短响应时间；
 客服往来明细：从咨询维度，查询每个客服的详细数据，包含转接、邀请等多人会话的详细数据
 访客来源统计：以折线图、表格的形式，按照时间、访客来源的维度统计pv、uv

// 在线技能组监控
技能组：技能组名称。
对话中用户数量：正在对话中的用户数。
请求最长等待时间：客户请求转人工到成功转到人工最长等待的时间。
平均满意度：满意量/参评量。
平均对话时长：总对话时长/对话量。
在线人数：状态为在线的客服数量。
离线中人数：状态为离线的客服数量。
当前排队量：申请转人工但还没有成功转入人工的客户数量。
总接通量：成功对话的总量。
接通率：成功接通的量/流入量。
转接量：操作转接数量。
转接率：操作转接数量/总接通量。
放弃量：排队中中途放弃的量。
放弃率：排队中中途放弃的量/流入量

// 在线坐席监控
姓名：坐席的姓名。
工号：坐席的工号。
工位号：登录时选择的物理位置。
当前状态：分为在线和离线。
对话中用户数：正在对话中的用户数量。
累计应答量：今日累计服务的用户数。
主动转接量：主动转接给其他坐席的对话量。
超时转接量：回复超时，自动转接到其他客服的会话数量。
平均满意度：满意度/应答量。
平均对话时长：总对话时长/应答量。
离线时间：最近一次的离线时间点。
离线时长：离线状态的时间总和。
今日首次上线时间：今日首次上线的时间点。
累计上线时长：在线状态的时间总和。
*/

/**
 * 客服对话统计数据：
 * 组织、工作组、客服、机器人
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_statistic", uniqueConstraints = {
    @UniqueConstraint(
        columnNames = {"orgUid", "workgroupUid", "agentUid",   "robotUid", "date", "hour"},
        name = "uk_org_uid_workgroup_uid_agent_uid_robot_uid_date_hour"
    )
})
public class ServiceStatisticEntity extends BaseEntity {

    
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

    // 统计类型，org/workgroup/agent/robot
    @Builder.Default
    private String type = ServiceStatisticTypeEnum.ORG.name();

    // 工作组uid
    private String workgroupUid;

    // 客服uid
    private String agentUid;

    // 机器人uid
    private String robotUid;

    // 组织uid, 在baseEntity中
    // private String orgUid;

    // 最细统计粒度，用于展示当天工单趋势变化
    @Builder.Default
    private int hour = 0;

    // 日期，每个orgUid，每个日期一个统计
    private String date;

}
