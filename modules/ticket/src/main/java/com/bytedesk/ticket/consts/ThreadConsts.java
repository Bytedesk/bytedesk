/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-04 12:34:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-04 13:30:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.consts;

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

    public static final String THREAD_VARIABLE_USER_UID = "userUid"; // 用户UID

    public static final String THREAD_VARIABLE_AGENT_UID = "agentUid"; // 客服UID

    public static final String THREAD_VARIABLE_WORKGROUP_UID = "workgroupUid"; // 工作组UID

    public static final String THREAD_VARIABLE_START_TIME = "startTime"; // 开始时间

    public static final String THREAD_VARIABLE_SLA_TIME = "slaTime"; // SLA时间

    public static final String THREAD_VARIABLE_ASSIGNEE = "assignee"; // 分配给谁

    
}
