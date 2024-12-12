/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 11:17:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-07 20:17:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.thread;

import java.util.List;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.service.agent.AgentEntity;

/**
 * 会话分配服务
 * 负责将会话分配给合适的客服
 */
public interface ThreadAssignmentService {

    /**
     * 处理新会话分配
     * @param thread 会话
     */
    public void handleNewThread(ThreadEntity thread);

    /**
     * 从可用客服中选择最合适的客服
     * @param availableAgents 可用客服列表
     * @param thread 待分配的会话
     * @return 选中的客服
     */
    AgentEntity selectAgent(List<AgentEntity> availableAgents, ThreadEntity thread);

    /**
     * 将会话分配给指定客服
     * @param thread 会话
     * @param agent 客服
     */
    void assignToAgent(ThreadEntity thread, AgentEntity agent);

    /**
     * 取消会话分配
     * @param thread 会话
     */
    void unassignThread(ThreadEntity thread);

    /**
     * 重新分配会话
     * @param thread 会话
     * @return 是否成功重新分配
     */
    boolean reassignThread(ThreadEntity thread);
}
