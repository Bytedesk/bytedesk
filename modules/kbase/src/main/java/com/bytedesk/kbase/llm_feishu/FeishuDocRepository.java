/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_feishu;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeishuDocRepository extends JpaRepository<FeishuDocEntity, Long>, JpaSpecificationExecutor<FeishuDocEntity> {

    Optional<FeishuDocEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    /**
     * 幂等 upsert 查找：不带 DeletedFalse 过滤——软删除行复活，避免唯一键冲突
     */
    Optional<FeishuDocEntity> findByFeishuUidAndResourceTypeAndObjToken(String feishuUid, String resourceType, String objToken);

    List<FeishuDocEntity> findByFeishuUidAndDeletedFalse(String feishuUid);

    List<FeishuDocEntity> findByKbUidAndDeletedFalse(String kbUid);

    List<FeishuDocEntity> findByFeishuUidAndSpaceIdAndResourceTypeAndDeletedFalse(String feishuUid, String spaceId, String resourceType);

    // 仅更新索引状态（bulk update 不触发版本号/实体事件，避免乐观锁冲突与索引消息循环）

    @Modifying
    @Query("UPDATE FeishuDocEntity f SET f.elasticStatus = :status, f.updatedAt = CURRENT_TIMESTAMP WHERE f.uid = :uid")
    void updateElasticStatusOnly(@Param("uid") String uid, @Param("status") String status);

    @Modifying
    @Query("UPDATE FeishuDocEntity f SET f.vectorStatus = :status, f.updatedAt = CURRENT_TIMESTAMP WHERE f.uid = :uid")
    void updateVectorStatusOnly(@Param("uid") String uid, @Param("status") String status);

    @Modifying
    @Query("UPDATE FeishuDocEntity f SET f.syncStatus = :status, f.updatedAt = CURRENT_TIMESTAMP WHERE f.uid = :uid")
    void updateSyncStatusOnly(@Param("uid") String uid, @Param("status") String status);
}
