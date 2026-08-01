/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 17:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:28:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message_unread;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MessageUnreadRepository extends JpaRepository<MessageUnreadEntity, Long>, JpaSpecificationExecutor<MessageUnreadEntity> {

    Optional<MessageUnreadEntity> findByUid(String uid);

    boolean existsByUid(String uid);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM MessageUnreadEntity mu WHERE mu.thread.topic LIKE CONCAT('%', :threadTopic, '%') AND mu.user NOT LIKE CONCAT('%', :userUid, '%')")
    int deleteByThreadTopicContainsAndUserNotContains(@Param("threadTopic") String threadTopic, @Param("userUid") String userUid);

        @Query("SELECT COUNT(mu) FROM MessageUnreadEntity mu WHERE mu.thread.topic = :threadTopic AND mu.orgUid = :orgUid AND mu.userUid <> :userUid AND mu.deleted = false")
        long countByThreadTopicAndOrgUidAndUserUidNotAndDeletedFalse(
            @Param("threadTopic") String threadTopic,
            @Param("orgUid") String orgUid,
            @Param("userUid") String userUid);

        @Query("SELECT mu FROM MessageUnreadEntity mu WHERE mu.thread.topic = :threadTopic AND mu.orgUid = :orgUid AND mu.userUid <> :userUid AND mu.deleted = false ORDER BY mu.createdAt ASC")
        Page<MessageUnreadEntity> findByThreadTopicAndOrgUidAndUserUidNotAndDeletedFalse(
            @Param("threadTopic") String threadTopic,
            @Param("orgUid") String orgUid,
            @Param("userUid") String userUid,
            Pageable pageable);

        @Query("SELECT mu FROM MessageUnreadEntity mu WHERE mu.thread.topic = :threadTopic AND mu.orgUid = :orgUid AND mu.userUid <> :userUid AND mu.deleted = false")
        List<MessageUnreadEntity> findByThreadTopicAndOrgUidAndUserUidNotAndDeletedFalse(
            @Param("threadTopic") String threadTopic,
            @Param("orgUid") String orgUid,
            @Param("userUid") String userUid);

        @Transactional
        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("UPDATE MessageUnreadEntity mu SET mu.deleted = true WHERE mu.thread.topic = :threadTopic AND mu.orgUid = :orgUid AND mu.userUid <> :userUid AND mu.deleted = false")
        int softDeleteByThreadTopicAndOrgUidAndUserUidNotAndDeletedFalse(
            @Param("threadTopic") String threadTopic,
            @Param("orgUid") String orgUid,
            @Param("userUid") String userUid);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM MessageUnreadEntity mu WHERE mu.uid = :uid")
    int deleteByUid(@Param("uid") String uid);
}
