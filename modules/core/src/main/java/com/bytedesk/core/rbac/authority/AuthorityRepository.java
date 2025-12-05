/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:25:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk@
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

/**
 * @author im.bytedesk.com
 */
@Tag(name = "authority - 权限")
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long>, JpaSpecificationExecutor<AuthorityEntity> {

    Optional<AuthorityEntity> findByUid(String uid);

    Optional<AuthorityEntity> findByName(String code);

    Optional<AuthorityEntity> findByValue(String value);

    Boolean existsByValue(String value);

    boolean existsById(@NonNull Long id);

    @Transactional
    void deleteById(@NonNull Long id);

    List<AuthorityEntity> findByValueContainingIgnoreCaseAndDeletedFalse(String value);

}
