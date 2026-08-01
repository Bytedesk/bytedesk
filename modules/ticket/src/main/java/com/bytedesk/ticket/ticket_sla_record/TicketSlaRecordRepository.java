package com.bytedesk.ticket.ticket_sla_record;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketSlaRecordRepository extends JpaRepository<TicketSlaRecordEntity, Long>, JpaSpecificationExecutor<TicketSlaRecordEntity> {

    Optional<TicketSlaRecordEntity> findByUid(String uid);

    List<TicketSlaRecordEntity> findByTicketUidAndDeletedFalse(String ticketUid);
    List<TicketSlaRecordEntity> findTop200ByStatusAndDueAtLessThanEqualAndDeletedFalseOrderByDueAtAsc(String status, java.time.ZonedDateTime dueAt);
    List<TicketSlaRecordEntity> findTop200BySlaTypeAndStatusAndDeletedFalseOrderByBreachedAtAsc(String slaType, String status);
    Optional<TicketSlaRecordEntity> findFirstByTicketUidAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(String ticketUid, String slaType);
    Optional<TicketSlaRecordEntity> findFirstByProcessInstanceIdAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(String processInstanceId, String slaType);
    Optional<TicketSlaRecordEntity> findFirstByTaskIdAndDeletedFalseOrderByCreatedAtDesc(String taskId);
    Optional<TicketSlaRecordEntity> findFirstByProcessInstanceIdAndTaskDefinitionKeyAndDeletedFalseOrderByCreatedAtDesc(String processInstanceId, String taskDefinitionKey);
    List<TicketSlaRecordEntity> findByTicketUidInAndDeletedFalse(List<String> ticketUids);
}