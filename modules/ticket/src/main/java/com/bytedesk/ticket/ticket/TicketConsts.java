/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 15:00:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-04 12:35:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import com.bytedesk.core.constant.I18Consts;

public class TicketConsts {

    public static final String TICKET_PROCESS_NAME = I18Consts.I18N_PREFIX + "ticket.process.name";

    public static final String TICKET_PROCESS_KEY = "ticketProcess";

    public static final String TICKET_PROCESS_PATH = "processes/ticket-process.bpmn20.xml";

    public static final String TICKET_EXTERNAL_PROCESS_UID_SUFFIX = "_external";
    
    // task definition key
    public static final String TICKET_USER_TASK_CREATE_TICKET = "createTicket";

    public static final String TICKET_USER_TASK_ASSIGN_TO_GROUP = "assignToGroup";

    public static final String TICKET_USER_TASK_CUSTOMER_VERIFY = "customerVerify";

    public static final String TICKET_EXCLUSIVE_GATEWAY_VERIFY_GATEWAY = "verifyGateway";

    // variables
    public static final String TICKET_VARIABLE_TICKET_UID = "ticketUid";

    public static final String TICKET_VARIABLE_DEPARTMENT_UID = "departmentUid";

    public static final String TICKET_VARIABLE_REPORTER_UID = "reporterUid";

    public static final String TICKET_VARIABLE_ORGUID = "orgUid";
    
    // 流程实例名称
    public static final String TICKET_VARIABLE_NAME = "name"; // 流程实例名称

    public static final String TICKET_VARIABLE_DESCRIPTION = "description"; // 流程实例描述

    public static final String TICKET_VARIABLE_START_USER_ID = "startUserId"; // 启动用户

    public static final String TICKET_VARIABLE_BUSINESS_KEY = "businessKey"; // 业务键

    public static final String TICKET_VARIABLE_STATUS = "status"; // 状态

    public static final String TICKET_VARIABLE_PRIORITY = "priority"; // 优先级

    public static final String TICKET_VARIABLE_CATEGORY_UID = "categoryUid"; // 分类

    public static final String TICKET_VARIABLE_START_TIME = "startTime"; // 开始时间

    public static final String TICKET_VARIABLE_SLA_TIME = "slaTime"; // SLA时间

    public static final String TICKET_VARIABLE_ASSIGNEE = "assignee"; // 分配给谁

    public static final String TICKET_VARIABLE_CLAIM_TIME = "claimTime"; // 认领时间

    // 未分配
    public static final String TICKET_FILTER_UNASSIGNED = "UNASSIGNED";

    
}
