package com.bytedesk.service.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RatingRepository extends JpaRepository<RatingEntity, Long>, JpaSpecificationExecutor<RatingEntity> {

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