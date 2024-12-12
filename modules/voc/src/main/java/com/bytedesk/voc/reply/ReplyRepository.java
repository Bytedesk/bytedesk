/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:08:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-12 10:42:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.reply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {
    
    Page<ReplyEntity> findByFeedbackId(Long feedbackId, Pageable pageable);
    
    Page<ReplyEntity> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT r FROM ReplyEntity r WHERE r.feedbackId = ?1 AND r.internal = false " +
           "ORDER BY r.createdAt ASC")
    Page<ReplyEntity> findPublicReplies(Long feedbackId, Pageable pageable);
    
    @Query("SELECT r FROM ReplyEntity r WHERE r.feedbackId = ?1 AND r.internal = true " +
           "ORDER BY r.createdAt ASC")
    Page<ReplyEntity> findInternalReplies(Long feedbackId, Pageable pageable);
} 