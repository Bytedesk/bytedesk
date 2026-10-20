package com.bytedesk.meet.minutes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetMinutesRepository extends JpaRepository<MeetMinutesEntity, Long>,
        JpaSpecificationExecutor<MeetMinutesEntity> {

    Optional<MeetMinutesEntity> findByUid(String uid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MeetMinutesEntity m where m.uid = :uid")
    Optional<MeetMinutesEntity> lockByUid(@Param("uid") String uid);

    Optional<MeetMinutesEntity> findByOrgUidAndRequestKey(String orgUid, String requestKey);

    List<MeetMinutesEntity> findByOrgUidAndRoomUidOrderByCreatedAtDesc(String orgUid, String roomUid);

    /**
     * 可领取纪要任务：PENDING（未开始）或 RUNNING 且租约过期。
     */
    @Query("""
            SELECT m FROM MeetMinutesEntity m
            WHERE m.invalidatedAt IS NULL
              AND m.deleted = false
              AND (
                m.status = 'PENDING'
                OR (m.status = 'RUNNING' AND m.leaseUntil IS NOT NULL AND m.leaseUntil < :now)
              )
            ORDER BY m.createdAt ASC
            """)
    List<MeetMinutesEntity> findClaimableTasks(@Param("now") ZonedDateTime now, Pageable pageable);
}
