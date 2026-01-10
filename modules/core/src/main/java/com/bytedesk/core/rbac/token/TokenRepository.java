/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-08 11:28:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 12:26:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.token;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TokenRepository extends JpaRepository<TokenEntity, Long>, JpaSpecificationExecutor<TokenEntity> {
    
    Optional<TokenEntity> findByUid(String uid);

    /**
     * 通过访问令牌查找Token实体
     * @param accessToken 访问令牌
     * @return Optional<TokenEntity&amp;amp;gt;
     */
    Optional<TokenEntity> findFirstByAccessTokenAndRevokedFalseAndDeletedFalse(String accessToken);
    
    /**
     * 通过用户UID、类型、未撤销、未删除和过期时间查询有效令牌
     * @param userUid 用户UID
     * @param type 令牌类型
     * @param now 当前时间
     * @return 有效令牌列表
     */
    List<TokenEntity> findByUserUidAndTypeAndRevokedFalseAndDeletedFalseAndExpiresAtAfter(String userUid, String type, ZonedDateTime now);

        /**
         * 原子更新 lastActiveAt（仅当超过阈值才更新），避免在高并发鉴权场景下对 detached entity 执行 merge 导致乐观锁异常。
         *
         * 注意：JPQL bulk update 会绕过 @Version 机制（不会递增 version），这里是刻意为之。
         */
        @Transactional
        @Modifying
        @Query("""
                        update TokenEntity t
                             set t.lastActiveAt = :now
                         where t.id = :id
                             and t.revoked = false
                             and t.deleted = false
                             and (t.lastActiveAt is null or t.lastActiveAt <= :threshold)
                        """)
        int updateLastActiveAtIfDue(@Param("id") Long id, @Param("now") ZonedDateTime now, @Param("threshold") ZonedDateTime threshold);
}
