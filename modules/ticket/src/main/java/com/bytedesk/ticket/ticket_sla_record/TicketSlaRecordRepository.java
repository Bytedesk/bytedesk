package com.bytedesk.ticket.ticket_sla_record;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface TicketSlaRecordRepository extends JpaRepository<TicketSlaRecordEntity, Long>, JpaSpecificationExecutor<TicketSlaRecordEntity> {

    Optional<TicketSlaRecordEntity> findByUid(String uid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from TicketSlaRecordEntity r where r.uid = :uid and r.deleted = false")
    Optional<TicketSlaRecordEntity> findByUidForSlaUpdate(@Param("uid") String uid);

    List<TicketSlaRecordEntity> findByTicketUidAndDeletedFalse(String ticketUid);
    List<TicketSlaRecordEntity> findTop200ByStatusAndDueAtLessThanEqualAndDeletedFalseOrderByDueAtAsc(String status, java.time.ZonedDateTime dueAt);
    @Query("select r from TicketSlaRecordEntity r where r.status = :status and r.dueAt <= :dueAt "
            + "and r.deleted = false and exists (select t.uid from bytedesk_ticket t "
            + "where t.uid = r.ticketUid and t.deleted = false and t.status not in :terminalStatuses) "
            + "order by r.dueAt asc")
    List<TicketSlaRecordEntity> findMonitorCandidates(@Param("status") String status,
            @Param("dueAt") java.time.ZonedDateTime dueAt,
            @Param("terminalStatuses") List<String> terminalStatuses, Pageable pageable);
    @Query("select r from TicketSlaRecordEntity r where r.deleted = false "
            + "and r.status in :statuses and (:afterUid is null or r.uid > :afterUid) "
            + "and exists (select t.uid from bytedesk_ticket t where t.uid = r.ticketUid "
            + "and t.deleted = false and t.status in :terminalStatuses) order by r.uid asc")
    List<TicketSlaRecordEntity> findTerminalReconcileCandidates(@Param("afterUid") @Nullable String afterUid,
            @Param("statuses") List<String> statuses,
            @Param("terminalStatuses") List<String> terminalStatuses, Pageable pageable);
    List<TicketSlaRecordEntity> findTop200BySlaTypeAndStatusAndDeletedFalseOrderByBreachedAtAsc(String slaType, String status);
    Optional<TicketSlaRecordEntity> findFirstByTicketUidAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(String ticketUid, String slaType);
    Optional<TicketSlaRecordEntity> findFirstByProcessInstanceIdAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(String processInstanceId, String slaType);
    Optional<TicketSlaRecordEntity> findFirstByTaskIdAndDeletedFalseOrderByCreatedAtDesc(String taskId);
    Optional<TicketSlaRecordEntity> findFirstByProcessInstanceIdAndTaskDefinitionKeyAndDeletedFalseOrderByCreatedAtDesc(String processInstanceId, String taskDefinitionKey);
    List<TicketSlaRecordEntity> findByTicketUidInAndDeletedFalse(List<String> ticketUids);
}