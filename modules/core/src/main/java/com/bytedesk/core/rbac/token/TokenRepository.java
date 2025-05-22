/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-08 11:28:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-22 16:25:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TokenRepository extends JpaRepository<TokenEntity, Long>, JpaSpecificationExecutor<TokenEntity> {
    
    /**
     * 通过访问令牌查找Token实体
     * @param accessToken 访问令牌
     * @return Optional<TokenEntity>
     */
    Optional<TokenEntity> findByAccessToken(String accessToken);
    
    /**
     * 通过用户UID、类型、未撤销和过期时间查询有效令牌
     * @param userUid 用户UID
     * @param type 令牌类型
     * @param now 当前时间
     * @return 有效令牌列表
     */
    List<TokenEntity> findByUserUidAndTypeAndRevokedFalseAndExpiresAtAfter(String userUid, String type, LocalDateTime now);
}
