/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-04 09:24:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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

import com.bytedesk.core.rbac.user.User;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 
 */
@Repository
@Tag(name = "thread - 会话")
public interface ThreadRepository extends JpaRepository<Thread, Long>, JpaSpecificationExecutor<Thread> {
        Optional<Thread> findByUid(String uid);

        Boolean existsByUid(String uid);

        /** used for member thread type */
        Optional<Thread> findFirstByTopicAndOwnerAndDeleted(String topic, User owner, Boolean deleted);

        Optional<Thread> findFirstByTopicAndDeleted(String topic, Boolean deleted);

        Optional<Thread> findFirstByTopicAndStatusNotContainingAndDeleted(String topic, String status, Boolean deleted);

        // @Query(value = "select * from core_thread t where t.topic like ?1 and t.status not in ?2 and t.is_deleted = ?3", nativeQuery = true)
        @Query(value = "select * from core_thread t where t.topic = ?1 and t.status not in ?2 and t.is_deleted = ?3", nativeQuery = true)
        Optional<Thread> findFirstTopicAndStatusesNotInAndDeleted(String topicWithWildcard,
                        List<String> statuses,
                        Boolean deleted);

        Page<Thread> findByOwnerAndHideAndDeleted(User owner, Boolean hide, Boolean deleted, Pageable pageable);

        List<Thread> findByTopic(String topic);

        // FIXME: h2不兼容 JSON_EXTRACT
        // FIXME: PostgreSQL ERROR: function json_extract(json, unknown) does not exist
        // @Query(value = "SELECT * FROM core_thread WHERE
        // JSON_EXTRACT(extra,'$.closed') = false", nativeQuery = true)
        List<Thread> findByStatusAndDeleted(String status, Boolean deleted);

        @Query("SELECT t FROM Thread t WHERE t.status IN :statuses AND t.deleted = :deleted")
        List<Thread> findByStatusesAndDeleted(@Param("statuses") List<String> statuses, Boolean deleted);

        @Query("SELECT t FROM Thread t WHERE t.type IN :types AND t.status not IN :statuses AND t.deleted = :deleted")
        List<Thread> findByTypesInAndStatusesNotInAndDeleted(@Param("types") List<String> types, @Param("statuses") List<String> statuses, Boolean deleted);

}
