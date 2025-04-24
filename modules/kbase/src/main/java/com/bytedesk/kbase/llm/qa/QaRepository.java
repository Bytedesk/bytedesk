/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-24 17:02:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QaRepository extends JpaRepository<QaEntity, Long>, JpaSpecificationExecutor<QaEntity> {

    Optional<QaEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    List<QaEntity> findByKbase_Uid(String kbUid);

    // auto complete, 根据问题关键字查询
    List<QaEntity> findByQuestionContains(String question);

    Boolean existsByQuestionAndAnswerAndKbase_UidAndOrgUidAndDeletedFalse(String question, String answer, String kbUid, String orgUid);
    

}
