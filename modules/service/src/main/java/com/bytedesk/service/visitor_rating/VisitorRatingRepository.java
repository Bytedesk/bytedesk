/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 11:37:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 17:27:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_rating;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VisitorRatingRepository extends JpaRepository<VisitorRatingEntity, Long>, JpaSpecificationExecutor<VisitorRatingEntity> {

    Optional<VisitorRatingEntity> findByUid(String uid);

    Optional<VisitorRatingEntity> findByThreadUid(String threadUid);

    List<VisitorRatingEntity> findByOrgUidAndCreatedAtBetween(String orgUid, LocalDateTime startTime, LocalDateTime endTime);
    
    // List<VisitorRatingEntity> findByOrgUidAndDateAndHour(String orgUid, String date, int hour);

    // List<VisitorRatingEntity> findByOrgUidAndDate(String orgUid, String date);

    List<VisitorRatingEntity> findByOrgUid(String orgUid);

    // @Query("SELECT AVG(r.VisitorRating) FROM VisitorRatingEntity r WHERE r.agentUid = :agentUid")
    // Double getAverageVisitorRating(@Param("agentUid") String agentUid);
    
    // @Query("SELECT COUNT(CASE WHEN r.VisitorRating >= 4 THEN 1 END) * 100.0 / COUNT(*) " +
    //        "FROM VisitorRatingEntity r WHERE r.agentUid = :agentUid")
    // Double getSatisfactionVisitorRating(@Param("agentUid") String agentUid);
    
    // @Query("SELECT r FROM VisitorRatingEntity r WHERE r.threadUid = :threadUid")
    // VisitorRatingEntity findByVisitorUid(@Param("threadUid") String threadUid);
    
    // @Query("SELECT r FROM VisitorRatingEntity r WHERE r.agentUid = :agentUid AND r.threadUid = :threadUid")
    // VisitorRatingEntity findByAgentUidAndVisitorUid(@Param("agentUid") String agentUid, @Param("threadUid") String threadUid);
} 