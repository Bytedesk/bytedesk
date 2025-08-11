/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-14 11:26:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:24:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.queue;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Call队列数据访问接口
 */
public interface CallQueueRepository extends JpaRepository<CallQueueEntity, Long> {
    
    /**
     * 根据队列名称查找队列
     */
    Optional<CallQueueEntity> findByName(String name);
    
    /**
     * 根据队列类型查找队列列表
     */
    List<CallQueueEntity> findByType(CallQueueEntity.QueueType type);
    
    /**
     * 根据队列状态查找队列列表
     */
    List<CallQueueEntity> findByStatus(CallQueueEntity.QueueStatus status);
    
    /**
     * 检查队列名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据队列名称删除队列
     */
    void deleteByName(String name);
    
    /**
     * 根据队列类型和状态查找队列
     */
    List<CallQueueEntity> findByTypeAndStatus(
        CallQueueEntity.QueueType type,
        CallQueueEntity.QueueStatus status);
} 