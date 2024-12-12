/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 11:30:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-07 13:06:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.util.List;
import java.util.Map;

/**
 * 客服服务接口
 * 提供客服状态管理、分配策略等功能
 */
public interface AgentService {

    /**
     * 获取可用客服列表
     * @return 可用客服ID列表
     */
    List<AgentEntity> getAvailableAgents();
    
    /**
     * 根据技能获取客服
     * @param skills 所需技能列表
     * @return 符合技能要求的客服ID列表
     */
    List<String> getAgentsBySkills(List<String> skills);
    
    /**
     * 获取在线客服列表
     * @return 在线客服ID列表
     */
    List<String> getOnlineAgents();
    
    /**
     * 获取客服工作负载
     * @param agentId 客服ID
     * @return 当前会话数
     */
    int getAgentWorkload(String agentUid);
    
    /**
     * 获取客服评分
     * @param agentId 客服ID
     * @return 客服评分(1-5)
     */
    double getAgentRating(String agentUid);
    
    /**
     * 获取客服平均响应时间
     * @param agentId 客服ID
     * @return 平均响应时间(毫秒)
     */
    long getAverageResponseTime(String agentUid);
    
    /**
     * 更新客服状态
     * @param agentId 客服ID
     * @param status 新状态
     */
    void updateAgentStatus(String agentUid, String status);
    
    /**
     * 分配会话给客服
     * @param threadId 会话ID
     * @param agentId 客服ID
     */
    void assignThread(String threadUid, String agentUid);
    
    /**
     * 取消分配会话
     * @param threadId 会话ID
     */
    void unassignThread(String threadUid);
    
    /**
     * 获取客服统计信息
     * @param agentId 客服ID
     * @return 统计信息
     */
    Map<String, Object> getAgentStats(String agentUid);
} 