/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-09 10:41:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-09 10:50:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.quality;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface QualityInspectionRepository extends JpaRepository<QualityInspectionEntity, Long> {

    Page<QualityInspectionEntity> findByAgentUid(String agentUid, Pageable pageable);
    
    Page<QualityInspectionEntity> findByInspectorUid(String inspectorUid, Pageable pageable);
    
    @Query("SELECT AVG(i.score) FROM QualityInspectionEntity i WHERE i.agentUid = ?1 " +
           "AND i.createdAt BETWEEN ?2 AND ?3")
    Double getAverageScore(String agentUid, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT i FROM QualityInspectionEntity i WHERE i.score < ?1 ORDER BY i.score ASC")
    List<QualityInspectionEntity> findLowScoreInspections(Integer threshold);
} 