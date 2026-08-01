package com.bytedesk.ticket.ticket_sla_record;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketSlaRecordResponse {

    private String uid;
    private String ticketUid;
    private String processInstanceId;
    private String slaType;
    private String slaSource;
    private String status;
    private String priority;
    private String categoryUid;
    private Long durationMinutes;
    private ZonedDateTime startedAt;
    private ZonedDateTime dueAt;
    private String taskId;
    private String taskDefinitionKey;
    private ZonedDateTime pausedAt;
    private Long pausedDurationSeconds;
    private ZonedDateTime completedAt;
    private ZonedDateTime breachedAt;
    private String breachReason;
    private String completedBy;
    private Boolean breached;

    public static TicketSlaRecordResponse fromEntity(TicketSlaRecordEntity entity) {
        if (entity == null) {
            return null;
        }
        return TicketSlaRecordResponse.builder()
                .uid(entity.getUid())
                .ticketUid(entity.getTicketUid())
                .processInstanceId(entity.getProcessInstanceId())
                .slaType(entity.getSlaType())
                .slaSource(entity.getSlaSource())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .categoryUid(entity.getCategoryUid())
                .durationMinutes(entity.getDurationMinutes())
                .startedAt(entity.getStartedAt())
                .dueAt(entity.getDueAt())
                .taskId(entity.getTaskId())
                .taskDefinitionKey(entity.getTaskDefinitionKey())
                .pausedAt(entity.getPausedAt())
                .pausedDurationSeconds(entity.getPausedDurationSeconds())
                .completedAt(entity.getCompletedAt())
                .breachedAt(entity.getBreachedAt())
                .breachReason(entity.getBreachReason())
                .completedBy(entity.getCompletedBy())
                .breached(entity.getBreached())
                .build();
    }
}