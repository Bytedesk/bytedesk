/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 11:37:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-19 15:56:39
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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bytedesk.core.thread.ThreadEntity;

import java.util.List;

public interface ThreadAgentRepository extends JpaRepository<ThreadEntity, Long> {

    // 统计客服的活跃会话数
    @Query("SELECT COUNT(t) FROM ThreadEntity t WHERE t.agentUid = :agentUid AND t.state = 'active'")
    int countActiveThreadsByAgent(@Param("agentUid") String agentUid);
    
    // 获取客服的活跃会话列表
    @Query("SELECT t.uid FROM ThreadEntity t WHERE t.agentUid = :agentUid AND t.state = 'active'")
    List<String> findActiveThreadsByAgent(@Param("agentUid") String agentUid);
    
    // 获取会话分配的客服
    @Query("SELECT t.agentUid FROM ThreadEntity t WHERE t.uid = :threadUid")
    String getAssignedAgent(@Param("threadUid") String threadUid);
    
    // 更新会话分配的客服
    @Modifying
    @Query("UPDATE ThreadEntity t SET t.agentUid = :agentUid WHERE t.uid = :threadUid")
    void updateAssignedAgent(@Param("threadUid") String threadUid, @Param("agentUid") String agentUid);
    
    // 获取客服的平均响应时间
    // @Query("SELECT AVG(t.firstResponseTime) FROM ThreadEntity t WHERE t.agentUid = :agentUid")
    // Long getAverageResponseTime(@Param("agentUid") String agentUid);
    
    // 统计客服的总会话数
    @Query("SELECT COUNT(t) FROM ThreadEntity t WHERE t.agentUid = :agentUid")
    int countTotalThreadsByAgent(@Param("agentUid") String agentUid);
    
    // 统计客服的已解决会话数
    @Query("SELECT COUNT(t) FROM ThreadEntity t WHERE t.agentUid = :agentUid AND t.state = 'resolved'")
    int countResolvedThreadsByAgent(@Param("agentUid") String agentUid);
} 