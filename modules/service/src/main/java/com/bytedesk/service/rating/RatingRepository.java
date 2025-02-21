/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 11:37:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 15:43:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.rating;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RatingRepository extends JpaRepository<RatingEntity, Long>, JpaSpecificationExecutor<RatingEntity> {

    List<RatingEntity> findByOrgUidAndCreatedAtBetween(String orgUid, LocalDateTime startTime, LocalDateTime endTime);
    
    // List<RatingEntity> findByOrgUidAndDateAndHour(String orgUid, String date, int hour);

    // List<RatingEntity> findByOrgUidAndDate(String orgUid, String date);

    List<RatingEntity> findByOrgUid(String orgUid);

    // @Query("SELECT AVG(r.rating) FROM RatingEntity r WHERE r.agentUid = :agentUid")
    // Double getAverageRating(@Param("agentUid") String agentUid);
    
    // @Query("SELECT COUNT(CASE WHEN r.rating >= 4 THEN 1 END) * 100.0 / COUNT(*) " +
    //        "FROM RatingEntity r WHERE r.agentUid = :agentUid")
    // Double getSatisfactionRating(@Param("agentUid") String agentUid);
    
    // @Query("SELECT r FROM RatingEntity r WHERE r.threadUid = :threadUid")
    // RatingEntity findByThreadUid(@Param("threadUid") String threadUid);
    
    // @Query("SELECT r FROM RatingEntity r WHERE r.agentUid = :agentUid AND r.threadUid = :threadUid")
    // RatingEntity findByAgentUidAndThreadUid(@Param("agentUid") String agentUid, @Param("threadUid") String threadUid);
} 