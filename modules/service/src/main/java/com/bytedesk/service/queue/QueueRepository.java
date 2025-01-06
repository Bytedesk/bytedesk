/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:03:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-24 12:35:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface QueueRepository extends JpaRepository<QueueEntity, Long>, JpaSpecificationExecutor<QueueEntity> {

    Optional<QueueEntity> findByUid(String uid);

    Optional<QueueEntity> findByDay(String day);
    
    // Optional<QueueEntity> findByThreadUid(String threadUid);

    Optional<QueueEntity> findFirstByTopicAndDayAndDeletedFalseOrderByCreatedAtDesc(
        String topic, 
        String day
    );
    
    List<QueueEntity> findByStatus(String status);
    
//     List<QueueEntity> findByStatusOrderByPriorityDesc(String status);
    
    int countByStatus(String status);
    
//     int countByPriorityGreaterThan(int priority);
    
    // @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, q.queueTime, q.endTime)) " +
    //        "FROM QueueEntity q WHERE q.status = 'COMPLETED'")
    // Double calculateAverageWaitTime();
    
    // @Query("SELECT MAX(TIMESTAMPDIFF(MINUTE, q.queueTime, q.endTime)) " +
    //        "FROM QueueEntity q WHERE q.status = 'COMPLETED'")
    // Integer calculateMaxWaitTime();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM QueueEntity q WHERE q.topic = :topic AND q.day = :day AND q.deleted = false")
    Optional<QueueEntity> findByTopicAndDayAndDeletedFalse(
        @Param("topic") String topic, 
        @Param("day") String day
    );
}
