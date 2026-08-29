/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:40:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-03 10:57:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActionRepository extends JpaRepository<ActionEntity, Long>, JpaSpecificationExecutor<ActionEntity> {

    Optional<ActionEntity> findByUid(String uid);

    /**
     * 分页查询时联动抓取 user，保证会话关闭后仍可读取用户昵称，
     * 与前端 ActionTable 用户列及导出内容保持一致
     */
    @Override
    @EntityGraph(attributePaths = "user")
    Page<ActionEntity> findAll(Specification<ActionEntity> spec, Pageable pageable);
}