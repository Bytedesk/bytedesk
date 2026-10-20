package com.bytedesk.meet.transcript;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetTranscriptRepository extends JpaRepository<MeetTranscriptEntity, Long>,
        JpaSpecificationExecutor<MeetTranscriptEntity> {

    Optional<MeetTranscriptEntity> findByUid(String uid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from MeetTranscriptEntity t where t.uid = :uid")
    Optional<MeetTranscriptEntity> lockByUid(@Param("uid") String uid);

    Optional<MeetTranscriptEntity> findByOrgUidAndRecordingUid(String orgUid, String recordingUid);

    Page<MeetTranscriptEntity> findByOrgUidAndRoomUidOrderByCreatedAtDesc(String orgUid, String roomUid, Pageable pageable);

    /**
     * 可领取任务：PENDING（未开始或退避到期）或 RUNNING 且租约过期（worker 崩溃恢复）。
     * 排除已失效来源。
     */
    @Query("""
            SELECT t FROM MeetTranscriptEntity t
            WHERE t.invalidatedAt IS NULL
              AND t.deleted = false
              AND (
                (t.status = 'PENDING' AND (t.nextRetryAt IS NULL OR t.nextRetryAt <= :now))
                OR (t.status = 'RUNNING' AND t.leaseUntil IS NOT NULL AND t.leaseUntil < :now)
              )
            ORDER BY t.createdAt ASC
            """)
    List<MeetTranscriptEntity> findClaimableTasks(@Param("now") ZonedDateTime now, Pageable pageable);
}
