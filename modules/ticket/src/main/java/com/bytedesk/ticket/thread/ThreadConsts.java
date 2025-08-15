/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-04 12:34:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 10:30:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.thread.enums.ThreadInviteStatusEnum;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTransferStatusEnum;

public class ThreadConsts {

    // thread-process
    public static final String THREAD_PROCESS_NAME = I18Consts.I18N_PREFIX + "thread.process.name";
    
    public static final String THREAD_PROCESS_KEY = "threadProcess";

    public static final String THREAD_PROCESS_PATH = "processes/thread-process.bpmn20.xml";

    // variables
    public static final String THREAD_VARIABLE_THREAD_UID = "threadUid";

    public static final String THREAD_VARIABLE_ORGUID = "orgUid";

    public static final String THREAD_VARIABLE_STATUS = "status"; // 状态
    
    public static final String THREAD_VARIABLE_THREAD_STATUS = "threadStatus"; // 线程状态，用于流程控制

    public static final String THREAD_VARIABLE_USER_UID = "userUid"; // 用户UID

    public static final String THREAD_VARIABLE_AGENT_UID = "agentUid"; // 客服UID

    public static final String THREAD_VARIABLE_WORKGROUP_UID = "workgroupUid"; // 工作组UID

    public static final String THREAD_VARIABLE_START_TIME = "startTime"; // 开始时间

    public static final String THREAD_VARIABLE_SLA_TIME = "slaTime"; // SLA时间

    public static final String THREAD_VARIABLE_ASSIGNEE = "assignee"; // 分配给谁
    
    // 从流程中提取的变量名
    public static final String THREAD_VARIABLE_ROBOT_ENABLED = "robotEnabled"; // 是否启用机器人
    
    public static final String THREAD_VARIABLE_NEED_HUMAN_SERVICE = "needHumanService"; // 是否需要人工服务
    
    public static final String THREAD_VARIABLE_AGENTS_OFFLINE = "agentsOffline"; // 客服是否离线
    
    public static final String THREAD_VARIABLE_AGENTS_BUSY = "agentsBusy"; // 坐席是否繁忙
    
    public static final String THREAD_VARIABLE_ROBOT_IDLE_TIMEOUT = "robotIdleTimeout"; // 机器人接待访客超时时间
    
    public static final String THREAD_VARIABLE_HUMAN_IDLE_TIMEOUT = "humanIdleTimeout"; // 人工接待访客超时时间
    
    public static final String THREAD_VARIABLE_LAST_VISITOR_MESSAGE_TIME = "lastVisitorMessageTime"; // 最后访客消息时间
    
    public static final String THREAD_VARIABLE_LAST_VISITOR_ACTIVITY_TIME = "lastVisitorActivityTime"; // 最后访客活动时间
    
    // 会话类型相关常量
    public static final String THREAD_VARIABLE_THREAD_TYPE = "threadType"; // 会话类型
    public static final String THREAD_TYPE_AGENT = "agent";                // 一对一客服类型
    public static final String THREAD_TYPE_WORKGROUP = "workgroup";        // 工作组类型
    public static final String THREAD_TYPE_ROBOT = "robot";                // 机器人类型

    // 流程网关 ID 常量
    public static final String THREAD_GATEWAY_IS_ROBOT_ENABLED = "isRobotEnabled"; // 机器人接待网关
    
    public static final String THREAD_GATEWAY_TRANSFER_TO_HUMAN = "transferToHuman"; // 转人工网关
    
    public static final String THREAD_GATEWAY_IS_AGENTS_OFFLINE = "isAgentsOffline"; // 客服是否离线网关
    
    public static final String THREAD_GATEWAY_IS_AGENTS_BUSY = "isAgentsBusy"; // 坐席是否繁忙网关

    

    // 机器人服务相关常量
    public static final String THREAD_VARIABLE_ROBOT_SERVICE_EXECUTION_COUNT = "robotServiceExecutionCount"; // 机器人服务执行计数
    public static final String THREAD_VARIABLE_ROBOT_SERVICE_START_TIME = "robotServiceStartTime"; // 机器人服务开始时间
    public static final String THREAD_VARIABLE_ROBOT_SERVICE_END_TIME = "robotServiceEndTime"; // 机器人服务结束时间
    public static final String THREAD_VARIABLE_ROBOT_SERVICE_DURATION = "robotServiceDuration"; // 机器人服务持续时间
    public static final String THREAD_VARIABLE_ROBOT_SERVICE_SUMMARY = "robotServiceSummary"; // 机器人服务摘要
    public static final String THREAD_VARIABLE_ROBOT_SERVICE_ERROR = "robotServiceError"; // 机器人服务错误信息
    public static final String THREAD_VARIABLE_TRANSFER_REASON = "transferReason"; // 转人工原因
    public static final String THREAD_VARIABLE_TRANSFER_PRIORITY = "transferPriority"; // 转人工优先级
    
    // 机器人服务执行次数上限
    public static final int THREAD_MAX_ROBOT_EXECUTION_COUNT = 3; // 最大机器人服务执行次数

    // 活动类型常量
    public static final String ACTIVITY_TYPE_SEQUENCE_FLOW = "sequenceFlow";
    public static final String ACTIVITY_TYPE_EXCLUSIVE_GATEWAY = "exclusiveGateway";
    public static final String ACTIVITY_TYPE_COMMENT = "comment";
    
    // 活动ID常量
    public static final String ACTIVITY_ID_TRANSFER_TO_HUMAN_TASK = "transferToHumanTask";
    public static final String ACTIVITY_ID_AGENTS_OFFLINE_SERVICE = "agentsOfflineService";
    public static final String ACTIVITY_ID_END = "end"; // BPMN流程中的结束节点ID
    public static final String ACTIVITY_ID_ROBOT_SERVICE = "robotService";
    public static final String ACTIVITY_ID_HUMAN_SERVICE = "humanService";
    public static final String ACTIVITY_ID_QUEUE_SERVICE = "queueService";
    
    // 默认超时时间常量
    public static final int DEFAULT_SLA_TIME = 30 * 60 * 1000;           // 默认SLA时间 - 30分钟
    public static final int DEFAULT_HUMAN_IDLE_TIMEOUT = 15 * 60 * 1000; // 默认人工客服空闲超时 - 15分钟
    public static final int DEFAULT_ROBOT_IDLE_TIMEOUT = 5 * 60 * 1000;  // 默认机器人空闲超时 - 5分钟
    
    // 定时器变量名常量 - 格式化为Flowable可识别的ISO格式
    public static final String THREAD_VARIABLE_HUMAN_IDLE_TIMEOUT_ISO = "humanIdleTimeoutISO";  // ISO格式的人工客服空闲超时
    public static final String THREAD_VARIABLE_ROBOT_IDLE_TIMEOUT_ISO = "robotIdleTimeoutISO";  // ISO格式的机器人空闲超时
    public static final String THREAD_VARIABLE_SLA_TIME_ISO = "slaTimeISO";  // ISO格式的SLA时间
    
    // 线程状态常量 - 使用 ThreadProcessStatusEnum 中的值
    public static final String THREAD_STATUS_NEW = ThreadProcessStatusEnum.NEW.name();       // 新会话状态
    public static final String THREAD_STATUS_WAITING = ThreadProcessStatusEnum.QUEUING.name();   // 排队中状态
    public static final String THREAD_STATUS_ONGOING = ThreadProcessStatusEnum.CHATTING.name();  // 对话中状态
    public static final String THREAD_STATUS_CLOSED = ThreadProcessStatusEnum.CLOSED.name();     // 会话已结束状态
    // 
    public static final String THREAD_STATUS_TRANSFERRED = "TRANSFERRED"; // 转接状态 (枚举中没有，保留原值)
    
    // 添加缺失的常量
    public static final String THREAD_STATUS_QUEUING = ThreadProcessStatusEnum.QUEUING.name();   // 排队中状态
    public static final String THREAD_STATUS_OFFLINE = "OFFLINE"; //ThreadProcessStatusEnum.OFFLINE.name();   // 离线状态
    
    // 从流程中提取的状态值 - 使用新的 enum 类型
    public static final String THREAD_STATUS_INVITE = ThreadInviteStatusEnum.INVITE_PENDING.name(); // 邀请协助状态
    // 
    // public static final String THREAD_STATUS_RESOLVED = ThreadResolvedStatusEnum.RESOLVED.name(); // 已解决状态
    public static final String THREAD_STATUS_TRANSFER = ThreadTransferStatusEnum.TRANSFER_PENDING.name(); // 转接状态
    // public static final String THREAD_STATUS_FINISHED = ThreadResolvedStatusEnum.FINISHED.name(); // 结束状态
    // public static final String THREAD_STATUS_ROBOT_FINISHED = ThreadResolvedStatusEnum.ROBOT_FINISHED.name(); // 纯机器人会话结束状态
    
    // 机器人相关变量常量
    public static final String THREAD_VARIABLE_ROBOT_UNANSWERED_COUNT = "robotUnansweredCount";  // 机器人未回答计数
    public static final String THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER = "visitorRequestedTransfer"; // 访客请求转人工标志
    
    // 排队相关变量常量
    public static final String THREAD_VARIABLE_QUEUE_START_TIME = "queueStartTime";  // 排队开始时间
    
    // 转人工方式相关常量
    public static final String THREAD_VARIABLE_TRANSFER_TYPE = "transferType"; // 转人工方式
    public static final String TRANSFER_TYPE_UI = "UI"; // 通过UI按钮转人工
    public static final String TRANSFER_TYPE_KEYWORD = "KEYWORD"; // 通过关键词转人工
    public static final String TRANSFER_TYPE_TIMEOUT = "TIMEOUT"; // 通过超时转人工
}
