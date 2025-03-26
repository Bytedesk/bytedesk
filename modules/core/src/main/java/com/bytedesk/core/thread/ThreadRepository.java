/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-26 14:50:43
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bytedesk.core.rbac.user.UserEntity;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 
 */
@Repository
@Tag(name = "thread - 会话")
public interface ThreadRepository extends JpaRepository<ThreadEntity, Long>, JpaSpecificationExecutor<ThreadEntity> {
        
        Optional<ThreadEntity> findByUid(String uid);

        Boolean existsByUid(String uid);

        /** used for member thread type */
        Optional<ThreadEntity> findFirstByTopicAndOwnerAndDeletedOrderByUpdatedAtDesc(String topic, UserEntity owner, Boolean deleted);

        List<ThreadEntity> findByTopicAndDeleted(String topic, Boolean deleted);

        Optional<ThreadEntity> findFirstByTopicAndDeletedOrderByCreatedAtDesc(String topic, Boolean deleted);

        Optional<ThreadEntity> findFirstByTopicAndStateNotContainingAndDeleted(String topic, String state, Boolean deleted);

        // @Query(value = "select * from core_thread t where t.topic like ?1 and t.state not in ?2 and t.is_deleted = ?3", nativeQuery = true)
        @Query(value = "select * from core_thread t where t.topic = ?1 and t.state not in ?2 and t.is_deleted = ?3 LIMIT 1", nativeQuery = true)
        Optional<ThreadEntity> findTopicAndStatesNotInAndDeleted(String topicWithWildcard,
                        List<String> states,
                        Boolean deleted);

        Page<ThreadEntity> findByOwnerAndHideAndDeleted(UserEntity owner, Boolean hide, Boolean deleted, Pageable pageable);

        List<ThreadEntity> findFirstByTopic(String topic);

        // FIXME: h2不兼容 JSON_EXTRACT
        // FIXME: PostgreSQL ERROR: function json_extract(json, unknown) does not exist
        // @Query(value = "SELECT * FROM core_thread WHERE
        // JSON_EXTRACT(extra,'$.closed') = false", nativeQuery = true)
        List<ThreadEntity> findByStateAndDeleted(String state, Boolean deleted);

        @Query("SELECT t FROM ThreadEntity t WHERE t.state IN :states AND t.deleted = :deleted")
        List<ThreadEntity> findByStatesAndDeleted(@Param("states") List<String> states, Boolean deleted);

        @Query("SELECT t FROM ThreadEntity t WHERE t.type IN :types AND t.state not IN :states AND t.deleted = :deleted")
        List<ThreadEntity> findByTypesInAndStatesNotInAndDeleted(@Param("types") List<String> types, @Param("states") List<String> states, Boolean deleted);

        @Query("SELECT t FROM ThreadEntity t WHERE t.type IN :types AND t.state = :state AND t.deleted = false")
        List<ThreadEntity> findByTypesInAndStateAndDeletedFalse(@Param("types") List<String> types, @Param("state") String state);

        @Query("SELECT t FROM ThreadEntity t WHERE t.type IN :types AND t.state != :state AND t.deleted = false")
        List<ThreadEntity> findByTypesInAndStateNotAndDeletedFalse(@Param("types") List<String> types, @Param("state") String state);

        @Query("SELECT COUNT(*) FROM ThreadEntity t WHERE t.topic = :topic AND t.state = :state AND t.deleted = false")
        int countByTopicAndStateAndDeletedFalse(@Param("topic") String topic, @Param("state") String state);

}
