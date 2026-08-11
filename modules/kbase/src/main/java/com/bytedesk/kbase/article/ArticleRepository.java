/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 09:48:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long>, JpaSpecificationExecutor<ArticleEntity> {

    Optional<ArticleEntity> findByUid(String uid);

    List<ArticleEntity> findByKbase_UidAndDeletedFalse(String kbUid);

    List<ArticleEntity> findByDeletedFalse();

    boolean existsByUid(String uid);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ArticleEntity a set a.elasticStatus = :status where a.uid = :uid")
    int updateElasticStatusByUid(@Param("uid") String uid, @Param("status") String status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ArticleEntity a set a.vectorStatus = :status where a.uid = :uid")
    int updateVectorStatusByUid(@Param("uid") String uid, @Param("status") String status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ArticleEntity a set a.docIdList = :docIdList where a.uid = :uid")
    int updateDocIdListByUid(@Param("uid") String uid, @Param("docIdList") List<String> docIdList);

}
