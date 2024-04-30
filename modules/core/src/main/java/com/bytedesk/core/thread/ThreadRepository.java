/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-22 22:30:34
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
        Optional<Thread> findByTid(String tid);
        /** used for member thread type */
        Optional<Thread> findFirstByTopicAndOwner(String topic, User owner);

        Optional<Thread> findFirstByTopic(String topic);
        
        Page<Thread> findByOwner(User owner, Pageable pageable);

        // @Query(value="select * from core_thread where extra like %?1% ", nativeQuery = true)
        // public Boolean existByExtra(String vid);

        // FIXME: h2不兼容 JSON_EXTRACT
        // FIXME: PostgreSQL ERROR: function json_extract(json, unknown) does not exist
        // @Query(value = "SELECT * FROM core_thread WHERE JSON_EXTRACT(extra, '$.closed') = false", nativeQuery = true)
        // List<Thread> findByExtraClosed();
        List<Thread> findByStatus(String status);

        // List<Thread> findByUpdatedAtAfter(Date updatedAt);
}
