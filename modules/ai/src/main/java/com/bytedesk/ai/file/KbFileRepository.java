/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:58:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-27 17:33:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 知识库文件
 *
 */
@Repository
@Tag(name = "kbfile info - 知识库文件")
// @PreAuthorize("hasRole('ROLE_ADMIN')")
public interface KbFileRepository extends JpaRepository<KbFile, Long>, JpaSpecificationExecutor<KbFile> {

    
}
