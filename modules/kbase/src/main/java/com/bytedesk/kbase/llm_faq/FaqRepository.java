/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 11:10:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;

public interface FaqRepository extends JpaRepository<FaqEntity, Long>, JpaSpecificationExecutor<FaqEntity> {

    Optional<FaqEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    List<FaqEntity> findByKbase_UidAndDeletedFalse(String kbUid);

    List<FaqEntity> findByDeletedFalse();

    // auto complete, 根据问题关键字查询
    List<FaqEntity> findByQuestionContains(String question);
    
    Boolean existsByQuestionAndAnswerAndKbase_UidAndOrgUidAndDeletedFalse(String question, String answer, String kbUid, String orgUid);

    /**
     * 获取随机FAQ，用于测试
     * 
     * @param limit 限制返回的数量
     * @return 随机FAQ列表
     */
    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM faq WHERE deleted = false ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<FaqEntity> findRandomFaq(@org.springframework.data.repository.query.Param("limit") int limit);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update FaqEntity f set f.elasticStatus = :status where f.uid = :uid")
    int updateElasticStatusByUid(@Param("uid") String uid, @Param("status") String status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update FaqEntity f set f.vectorStatus = :status where f.uid = :uid")
    int updateVectorStatusByUid(@Param("uid") String uid, @Param("status") String status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update FaqEntity f set f.docIdList = :docIdList where f.uid = :uid")
    int updateDocIdListByUid(@Param("uid") String uid, @Param("docIdList") List<String> docIdList);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update FaqEntity f set f.kbase = :kbase where f.uid = :uid")
    int updateKbaseByUid(@Param("uid") String uid, @Param("kbase") com.bytedesk.kbase.kbase.KbaseEntity kbase);

    // ==================== 计数类原子更新（P1 方案 A：消除并发读-改-写乐观锁冲突） ====================
    // bulk update 不会自动维护 @Version，需显式递增 version，
    // 避免并发读-改-写基于旧 version 覆盖计数；coalesce 兼容历史 NULL 行

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update FaqEntity f set f.clickCount = coalesce(f.clickCount, 0) + 1, f.version = f.version + 1 where f.uid = :uid")
    int increaseClickCountByUid(@Param("uid") String uid);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update FaqEntity f set f.upCount = coalesce(f.upCount, 0) + 1, f.version = f.version + 1 where f.uid = :uid")
    int increaseUpCountByUid(@Param("uid") String uid);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update FaqEntity f set f.downCount = coalesce(f.downCount, 0) + 1, f.version = f.version + 1 where f.uid = :uid")
    int increaseDownCountByUid(@Param("uid") String uid);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update FaqEntity f set f.feedbackCount = coalesce(f.feedbackCount, 0) + 1, f.version = f.version + 1 where f.uid = :uid")
    int increaseFeedbackCountByUid(@Param("uid") String uid);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update FaqEntity f set f.transferCount = coalesce(f.transferCount, 0) + 1, f.version = f.version + 1 where f.uid = :uid")
    int increaseTransferCountByUid(@Param("uid") String uid);

    /**
     * 查询已过期但仍残留在索引中的 FAQ（endDate < now 且 elastic/vector 状态为 SUCCESS）
     * 供过期清理定时任务分页使用
     */
    @Query(value = "select f from FaqEntity f where f.deleted = false and f.endDate < :now "
            + "and (f.elasticStatus = 'SUCCESS' or f.vectorStatus = 'SUCCESS')",
           countQuery = "select count(f) from FaqEntity f where f.deleted = false and f.endDate < :now "
            + "and (f.elasticStatus = 'SUCCESS' or f.vectorStatus = 'SUCCESS')")
    Page<FaqEntity> findExpiredIndexed(@Param("now") ZonedDateTime now, Pageable pageable);
}
