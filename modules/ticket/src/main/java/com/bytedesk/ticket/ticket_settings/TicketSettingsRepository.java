/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-20 12:52:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_settings;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketSettingsRepository extends JpaRepository<TicketSettingsEntity, Long>, JpaSpecificationExecutor<TicketSettingsEntity> {

    Optional<TicketSettingsEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    Optional<TicketSettingsEntity> findByNameAndOrgUidAndDeletedFalse(String name, String orgUid);

    List<TicketSettingsEntity> findByOrgUidAndIsDefaultTrue(String orgUid);

    /**
     * 悲观锁读取 org 默认 TicketSettings，保证并发下只创建一个默认。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TicketSettingsEntity t where t.orgUid = :orgUid and t.isDefault = true and t.deleted = false")
    Optional<TicketSettingsEntity> findDefaultForUpdate(@Param("orgUid") String orgUid);

    // 2025-11: workgroupUid 已移除，按工作组查询请先通过 BindingRepository 解析 settingsUid

    // Boolean existsByPlatform(String platform);
}
