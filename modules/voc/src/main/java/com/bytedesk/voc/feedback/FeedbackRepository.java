/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:08:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-12 10:42:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {
    
    Page<FeedbackEntity> findByUserId(Long userId, Pageable pageable);
    
    Page<FeedbackEntity> findByType(String type, Pageable pageable);
    
    Page<FeedbackEntity> findByStatus(String status, Pageable pageable);
    
    Page<FeedbackEntity> findByAssignedTo(Long assignedTo, Pageable pageable);
    
    @Query("SELECT f FROM FeedbackEntity f WHERE " +
           "(LOWER(f.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:type IS NULL OR f.type = :type) AND " +
           "(:status IS NULL OR f.status = :status)")
    Page<FeedbackEntity> search(
            @Param("keyword") String keyword,
            @Param("type") String type, 
            @Param("status") String status,
            Pageable pageable);
} 