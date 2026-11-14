/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.jobtitle;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobTitleRepository extends JpaRepository<JobTitleEntity, Long>, JpaSpecificationExecutor<JobTitleEntity> {

    Optional<JobTitleEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    Boolean existsByActiveKey(String activeKey);

    /**
     * 含软删除的记录数：用作组织首次初始化完成标记。
     * > 0 表示该组织已初始化或已被管理，不再自动补建默认职务。
     */
    @Query("SELECT COUNT(e) FROM JobTitleEntity e WHERE e.orgUid = :orgUid")
    long countByOrgUidIncludingDeleted(@Param("orgUid") String orgUid);
}
