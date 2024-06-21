/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:21:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-20 17:09:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByUid(String uid);
    List<Category> findByParentAndPlatformOrderByOrderNoAsc(Category parent, PlatformEnum platform);
    Optional<Category> findByNameAndTypeAndOrgUidAndPlatform(String name, String type, String orgUid, 
            PlatformEnum platform);
    Optional<Category> findByNameAndTypeAndLevelAndPlatform(String name, String type, LevelEnum level,
            PlatformEnum platform);
}
