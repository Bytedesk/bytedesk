/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-10 11:35:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 11:13:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.flow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
// import java.util.List;

@Repository
public interface FlowRepository extends JpaRepository<FlowEntity, Long>, JpaSpecificationExecutor<FlowEntity> {

    Optional<FlowEntity> findByUid(String uid);

    // @Query("SELECT f FROM FlowEntity f WHERE f.deleted = false AND f.orgUid = ?1")
    // List<FlowEntity> findByOrgUid(String orgUid);

    // @Query("SELECT f FROM FlowEntity f WHERE f.deleted = false AND f.publicId = ?1")
    // FlowEntity findByPublicId(String publicId);

    // @Query("SELECT f FROM FlowEntity f WHERE f.deleted = false AND f.customDomain = ?1")
    // FlowEntity findByCustomDomain(String customDomain);
}
