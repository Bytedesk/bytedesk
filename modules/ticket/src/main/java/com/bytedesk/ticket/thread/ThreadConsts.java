/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-04 12:34:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-05 14:50:30
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

public class ThreadConsts {

    // thread-process
    public static final String THREAD_PROCESS_NAME = I18Consts.I18N_PREFIX + "thread.process.name";
    
    public static final String THREAD_PROCESS_KEY = "threadProcess";

    public static final String THREAD_PROCESS_PATH = "processes/thread-process.bpmn20.xml";

    // variables
    public static final String THREAD_VARIABLE_THREAD_UID = "threadUid";

    public static final String THREAD_VARIABLE_ORGUID = "orgUid";

    public static final String THREAD_VARIABLE_STATUS = "status"; // 状态
    
    public static final String THREAD_VARIABLE_THREAD_STATUS = "status"; // 线程状态，与STATUS相同，用于流程控制

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

    // 流程网关 ID 常量
    public static final String THREAD_GATEWAY_IS_ROBOT_ENABLED = "isRobotEnabled"; // 机器人接待网关
    
    public static final String THREAD_GATEWAY_TRANSFER_TO_HUMAN = "transferToHuman"; // 转人工网关
    
    public static final String THREAD_GATEWAY_IS_AGENTS_OFFLINE = "isAgentsOffline"; // 客服是否离线网关
    
    public static final String THREAD_GATEWAY_IS_AGENTS_BUSY = "isAgentsBusy"; // 坐席是否繁忙网关

    // 从流程中提取的状态值
    public static final String THREAD_STATUS_INVITE = "INVITE"; // 邀请协助状态
    
    public static final String THREAD_STATUS_RESOLVED = "RESOLVED"; // 已解决状态
    
    public static final String THREAD_STATUS_TRANSFER = "TRANSFER"; // 转接状态
    
    public static final String THREAD_STATUS_FINISHED = "FINISHED"; // 结束状态
}
