package com.bytedesk.ai.robot.group;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface GroupRobotExecutionRepository extends JpaRepository<GroupRobotExecutionEntity, Long> {

    Optional<GroupRobotExecutionEntity> findByUid(String uid);

    /**
     * 幂等查询：同一源消息+同一机器人是否已存在任务。
     */
    Optional<GroupRobotExecutionEntity> findByOrgUidAndGroupUidAndSourceMessageUidAndRobotUid(
            String orgUid, String groupUid, String sourceMessageUid, String robotUid);

    List<GroupRobotExecutionEntity> findByOrgUidAndGroupUidAndSourceMessageUid(
            String orgUid, String groupUid, String sourceMessageUid);

    /**
     * 领取待执行任务：CAS 更新 PENDING -> RUNNING，仅当租约过期或为空时可领取。
     * 返回 1 表示领取成功。
     */
    @Transactional
    @Modifying
    @Query("UPDATE GroupRobotExecutionEntity e SET e.status = 'RUNNING', "
            + "e.attempt = e.attempt + 1, e.leaseUntil = :leaseUntil, e.leaseToken = :leaseToken, "
            + "e.statusUpdatedAt = :now "
            + "WHERE e.uid = :uid AND (e.status = 'PENDING' "
            + "OR (e.status = 'RUNNING' AND e.leaseUntil IS NOT NULL AND e.leaseUntil < :now))")
    int claimTask(@Param("uid") String uid, @Param("leaseUntil") ZonedDateTime leaseUntil,
            @Param("leaseToken") String leaseToken, @Param("now") ZonedDateTime now);

    /**
     * 归还过期 RUNNING 任务为 PENDING（实例崩溃恢复，由定时器调用）。
     */
    @Transactional
    @Modifying
    @Query("UPDATE GroupRobotExecutionEntity e SET e.status = 'PENDING', e.statusUpdatedAt = :now "
            + "WHERE e.status = 'RUNNING' AND e.leaseUntil IS NOT NULL AND e.leaseUntil < :now")
    int reclaimExpiredRunning(@Param("now") ZonedDateTime now);

    /**
     * 持租约完成任务写回：仅当任务仍为 RUNNING 且租约令牌匹配时更新为终态。
     * 防止过期 worker（租约被其他实例接管后）覆盖最新状态。
     */
    @Transactional
    @Modifying
    @Query("UPDATE GroupRobotExecutionEntity e SET e.status = :status, e.replyMessageUid = :replyMessageUid, "
            + "e.errorCode = :errorCode, e.leaseUntil = NULL, e.leaseToken = NULL, e.statusUpdatedAt = :now "
            + "WHERE e.uid = :uid AND e.status = 'RUNNING' AND e.leaseToken = :leaseToken")
    int completeWithLease(@Param("uid") String uid, @Param("leaseToken") String leaseToken,
            @Param("status") String status, @Param("replyMessageUid") String replyMessageUid,
            @Param("errorCode") String errorCode, @Param("now") ZonedDateTime now);

    /**
     * 持租约释放回 PENDING（源消息未落库等临时情况）：回退本次 attempt 计数，
     * 仅当租约令牌匹配时生效。
     */
    @Transactional
    @Modifying
    @Query("UPDATE GroupRobotExecutionEntity e SET e.status = 'PENDING', "
            + "e.attempt = CASE WHEN e.attempt > 0 THEN e.attempt - 1 ELSE 0 END, "
            + "e.leaseUntil = NULL, e.leaseToken = NULL, e.statusUpdatedAt = :now "
            + "WHERE e.uid = :uid AND e.status = 'RUNNING' AND e.leaseToken = :leaseToken")
    int releaseToPendingWithLease(@Param("uid") String uid, @Param("leaseToken") String leaseToken,
            @Param("now") ZonedDateTime now);

    /**
     * 将达到重试上限的 PENDING 任务置为 FAILED（幂等：仅 PENDING 态生效）。
     */
    @Transactional
    @Modifying
    @Query("UPDATE GroupRobotExecutionEntity e SET e.status = 'FAILED', e.errorCode = :errorCode, "
            + "e.leaseUntil = NULL, e.leaseToken = NULL, e.statusUpdatedAt = :now "
            + "WHERE e.uid = :uid AND e.status = 'PENDING'")
    int markFailedIfPending(@Param("uid") String uid, @Param("errorCode") String errorCode,
            @Param("now") ZonedDateTime now);

    /**
     * 查询可领取的 PENDING 任务（按创建时间升序，限量）。
     */
    @Query("SELECT e FROM GroupRobotExecutionEntity e WHERE e.status = 'PENDING' "
            + "ORDER BY e.createdAt ASC")
    List<GroupRobotExecutionEntity> findClaimableTasks(org.springframework.data.domain.Pageable pageable);
}
