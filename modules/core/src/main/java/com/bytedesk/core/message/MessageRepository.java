/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:25:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<MessageEntity, Long>, JpaSpecificationExecutor<MessageEntity> {

    Optional<MessageEntity> findByUid(String uid);

    // 或者更好的选择：使用JPQL查询，更灵活且性能更可控
    @Query("SELECT m FROM MessageEntity m WHERE m.type = :type AND m.content LIKE %:messageUid%")
    Optional<MessageEntity> findTransferMessage(
            @Param("type") String type, 
            @Param("messageUid") String messageUid
            );

    // 根据thread.uid查询最新一条消息
    Optional<MessageEntity> findFirstByThread_UidOrderByCreatedAtDesc(String threadUid);

    List<MessageEntity> findByThread_UidOrderByCreatedAtAsc(String threadUid);

    // 根据threadTopic查询最新n条消息
    @Query("SELECT m FROM MessageEntity m WHERE m.thread.topic = :threadTopic ORDER BY m.createdAt DESC")
    List<MessageEntity> findLatestByThreadTopicOrderByCreatedAtDesc(@Param("threadTopic") String threadTopic, org.springframework.data.domain.Pageable pageable);

    // thread.uid + type + user contains uid
    Optional<MessageEntity> findFirstByThread_UidAndTypeAndUserContainsOrderByCreatedAtDesc(
            @Param("threadUid") String threadUid, 
            @Param("type") String type, 
            @Param("userUid") String userUid);

    /**
     * 根据会话UID和状态列表查询消息，按创建时间升序排列
     * 
     * @param threadUid 会话UID
     * @param statuses 状态列表
     * @return 消息列表
     */
    List<MessageEntity> findByThread_UidAndStatusInOrderByCreatedAtAsc(String threadUid, List<String> statuses);

    boolean existsByUid(String uid);
}
