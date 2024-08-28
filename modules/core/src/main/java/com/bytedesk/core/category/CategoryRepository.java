/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:21:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-27 18:10:33
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

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByUid(String uid);
    // 
    List<Category> findByParentAndPlatformAndDeletedOrderByOrderNoAsc(Category parent, String platform,
                    Boolean deleted);
            
    Optional<Category> findByNameAndTypeAndOrgUidAndPlatformAndDeleted(String name, String type, String orgUid, String platform,
                    Boolean deleted);
            
    Optional<Category> findByNameAndTypeAndLevelAndPlatformAndDeleted(String name, String type, String level, String platform,
            Boolean deleted);

    Optional<Category> findByNameAndKbUidAndDeleted(String name, String kbUid, Boolean deleted);

    List<Category> findByKbUidAndDeleted(String kbUid, Boolean deleted);

//     List<Category> findByLevelAndDeleted(String level, Boolean deleted);

//     List<Category> findByLevelAndOrgUidAndDeleted(String level, String orgUid, Boolean deleted);

//     List<Category> findByLevelAndAgentUidAndDeleted(String level, String agentUid, Boolean deleted);
    
}
