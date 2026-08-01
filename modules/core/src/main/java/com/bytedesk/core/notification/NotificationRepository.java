/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-01 09:29:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-17 09:46:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notification;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>, JpaSpecificationExecutor<NotificationEntity> {
    
    Optional<NotificationEntity> findByUid(String uid);

    // find by extra contains messageUid: ''
    Optional<NotificationEntity> findByExtraContains(String messageUid);

    // status and extra contains messageUid: ''
    Optional<NotificationEntity> findByStatusAndExtraContains(String status, String messageUid);

    Optional<NotificationEntity> findByUidAndUserUidAndDeletedFalse(String uid, String userUid);

    long countByUserUidAndStatusAndDeletedFalse(String userUid, String status);

    List<NotificationEntity> findByUserUidAndStatusAndDeletedFalse(String userUid, String status);

    /**
     * 通过 visitorUid（前端自定义标识如 visitor_001）+ orgUid 查询未读通知数。
     * 使用 native query join bytedesk_service_visitor 表将 visitorUid 解析为系统 uid，
     * 避免 modules/core 对 modules/service 的编译期依赖。
     */
    @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) "
            + "FROM bytedesk_core_notification n "
            + "INNER JOIN bytedesk_service_visitor v ON n.user_uid = v.uuid "
            + "WHERE v.visitor_uid = :visitorUid "
            + "AND v.org_uid = :orgUid "
            + "AND n.notification_status = :status "
            + "AND n.is_deleted = false", nativeQuery = true)
    long countByVisitorUidAndOrgUidAndStatusAndDeletedFalse(
            @org.springframework.data.repository.query.Param("visitorUid") String visitorUid,
            @org.springframework.data.repository.query.Param("orgUid") String orgUid,
            @org.springframework.data.repository.query.Param("status") String status);
}
