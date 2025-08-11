/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:26:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bytedesk.core.rbac.user.UserEntity;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "thread - 会话")
public interface ThreadRepository extends JpaRepository<ThreadEntity, Long>, JpaSpecificationExecutor<ThreadEntity> {
        
        Optional<ThreadEntity> findByUid(String uid);

        Boolean existsByUid(String uid);

        /** used for member thread type */
        Optional<ThreadEntity> findFirstByTopicAndOwnerAndDeletedOrderByUpdatedAtDesc(String topic, UserEntity owner, Boolean deleted);

        List<ThreadEntity> findByTopicAndDeletedOrderByCreatedAtDesc(String topic, Boolean deleted);

        Optional<ThreadEntity> findFirstByTopicAndDeletedOrderByCreatedAtDesc(String topic, Boolean deleted);

        Optional<ThreadEntity> findFirstByTopicAndStatusNotContainingAndDeleted(String topic, String status, Boolean deleted);

        // @Query(value = "select * from core_thread t where t.topic like ?1 and t.status not in ?2 and t.is_deleted = ?3", nativeQuery = true)
        @Query(value = "select * from core_thread t where t.topic = ?1 and t.status not in ?2 and t.is_deleted = ?3 LIMIT 1", nativeQuery = true)
        Optional<ThreadEntity> findTopicAndStatusesNotInAndDeleted(String topicWithWildcard,
                        List<String> statuses,
                        Boolean deleted);

        Page<ThreadEntity> findByOwnerAndHideAndDeleted(UserEntity owner, Boolean hide, Boolean deleted, Pageable pageable);

        List<ThreadEntity> findFirstByTopic(String topic);
        
        List<ThreadEntity> findByStatusAndDeleted(String status, Boolean deleted);

        @Query("SELECT t FROM ThreadEntity t WHERE t.status IN :statuses AND t.deleted = :deleted")
        List<ThreadEntity> findByStatusesAndDeleted(@Param("statuses") List<String> statuses, Boolean deleted);

        @Query("SELECT t FROM ThreadEntity t WHERE t.type IN :types AND t.status not IN :statuses AND t.deleted = :deleted")
        List<ThreadEntity> findByTypesInAndStatusesNotInAndDeleted(@Param("types") List<String> types, @Param("statuses") List<String> statuses, Boolean deleted);

        @Query("SELECT t FROM ThreadEntity t WHERE t.type IN :types AND t.status = :status AND t.deleted = false")
        List<ThreadEntity> findByTypesInAndStatusAndDeletedFalse(@Param("types") List<String> types, @Param("status") String status);

        @Query("SELECT t FROM ThreadEntity t WHERE t.type IN :types AND t.status != :status AND t.deleted = false")
        List<ThreadEntity> findByTypesInAndStatusNotAndDeletedFalse(@Param("types") List<String> types, @Param("status") String status);

        @Query("SELECT COUNT(*) FROM ThreadEntity t WHERE t.topic = :topic AND t.status = :status AND t.deleted = false")
        int countByTopicAndStatusAndDeletedFalse(@Param("topic") String topic, @Param("status") String status);

        // count by topic and status not
        @Query("SELECT COUNT(*) FROM ThreadEntity t WHERE t.topic = :topic AND t.status != :status AND t.deleted = false")
        int countByTopicAndStatusNotAndDeletedFalse(@Param("topic") String topic, @Param("status") String status);

        @Query("SELECT t FROM ThreadEntity t WHERE t.topic = :topic AND t.status != :status AND t.deleted = false")
        List<ThreadEntity> findByTopicAndStatusNotAndDeletedFalse(@Param("topic") String topic, @Param("status") String status);

        @Query("SELECT t FROM ThreadEntity t WHERE t.topic IN :topics AND t.deleted = false")
        List<ThreadEntity> findByTopicsInAndDeletedFalse(@Param("topics") Set<String> topics);

        @Query("SELECT t FROM ThreadEntity t WHERE t.topic IN :topics AND t.deleted = false")
        Page<ThreadEntity> findByTopicsInAndDeletedFalse(@Param("topics") Set<String> topics, Pageable pageable);

        @Query("SELECT t FROM ThreadEntity t WHERE t.topic LIKE :topicPrefix AND t.status = :status AND t.deleted = false ORDER BY t.createdAt ASC")
        List<ThreadEntity> findByTopicStartsWithAndStatusAndDeletedFalse(@Param("topicPrefix") String topicPrefix, @Param("status") String status);

}
