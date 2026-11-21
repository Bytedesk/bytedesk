/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *   contact: 270580156@qq.com 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group.apply;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

public interface GroupMemberApplyRepository
        extends JpaRepository<GroupMemberApplyEntity, Long>, JpaSpecificationExecutor<GroupMemberApplyEntity> {

    Optional<GroupMemberApplyEntity> findByUid(String uid);

    /** 单条当前申请模型：同组织同群同人唯一 */
    Optional<GroupMemberApplyEntity> findByOrgUidAndGroupUidAndUserUidAndDeletedFalse(String orgUid, String groupUid,
            String userUid);

    /** 悲观锁读取当前申请（审批/撤回/失效串行化） */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<GroupMemberApplyEntity> findWithLockByUid(String uid);

    /** 管理端：某组织下多个群的待审批申请 */
    Page<GroupMemberApplyEntity> findByOrgUidAndGroupUidInAndStatusAndDeletedFalse(String orgUid, List<String> groupUids,
            String status, Pageable pageable);

    /** 申请人：自己的申请记录 */
    Page<GroupMemberApplyEntity> findByOrgUidAndUserUidAndDeletedFalse(String orgUid, String userUid, Pageable pageable);

    /** 公开群列表标记：本页群中该用户是否有 PENDING 申请 */
    List<GroupMemberApplyEntity> findByOrgUidAndUserUidAndStatusAndGroupUidInAndDeletedFalse(String orgUid, String userUid,
            String status, List<String> groupUids);

    /** 群维度统计待审批数量 */
    long countByOrgUidAndGroupUidAndStatusAndDeletedFalse(String orgUid, String groupUid, String status);
}
