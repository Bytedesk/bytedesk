/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-04 09:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-04 09:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.system_config;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SystemConfigRepository extends JpaRepository<SystemConfigEntity, Long>, JpaSpecificationExecutor<SystemConfigEntity> {

    Optional<SystemConfigEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    Optional<SystemConfigEntity> findByConfigKeyAndOrgUidAndDeletedFalse(String configKey, String orgUid);

    /**
     * 含软删除记录的查询（upsert 用）：软删除记录仍占用 (config_key, org_uid) 唯一约束，
     * 保存新值前须先复用/清理，避免 Duplicate entry 冲突。
     */
    Optional<SystemConfigEntity> findByConfigKeyAndOrgUid(String configKey, String orgUid);

    List<SystemConfigEntity> findByOrgUidAndDeletedFalse(String orgUid);

    List<SystemConfigEntity> findByOrgUidAndConfigKeyInAndDeletedFalse(String orgUid, List<String> configKeys);

    List<SystemConfigEntity> findByConfigKeyAndDeletedFalse(String configKey);
}
