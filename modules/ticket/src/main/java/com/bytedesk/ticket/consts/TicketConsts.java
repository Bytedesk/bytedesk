/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 15:00:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-17 15:25:08
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

public class TicketConsts {

    public  static final String TICKET_PROCESS_NAME_GROUP = I18Consts.I18N_PREFIX + "ticket.process.name.group";

    public static final String TICKET_PROCESS_NAME_GROUP_SIMPLE = I18Consts.I18N_PREFIX + "ticket.process.name.group.simple";

    public static final String TICKET_PROCESS_NAME_AGENT = I18Consts.I18N_PREFIX + "ticket.process.name.agent";

    public static final String TICKET_PROCESS_KEY = "ticketProcess";

    public static final String TICKET_PROCESS_KEY_AGENT = "agentTicketProcess";

    public static final String TICKET_PROCESS_KEY_GROUP = "groupTicketProcess";

    public static final String TICKET_PROCESS_KEY_GROUP_SIMPLE = "groupTicketSimpleProcess";

    public static final String TICKET_PROCESS_GROUP_PATH = "processes/group-ticket-process.bpmn20.xml";

    public static final String TICKET_PROCESS_GROUP_PATH_SIMPLE = "processes/group-ticket-simple-process.bpmn20.xml";

    public static final String TICKET_PROCESS_AGENT_PATH = "processes/agent-ticket-process.bpmn20.xml";

    // 未分配
    public static final String TICKET_FILTER_UNASSIGNED = "UNASSIGNED";


    
}
