/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-21 13:06:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-22 17:51:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.time.Duration;
import java.time.DayOfWeek;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.service.holiday.HolidayEntity;
import com.bytedesk.service.holiday.HolidayRestService;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRepository;
import com.bytedesk.ticket.ticket_settings_sla.TicketSlaSettingsEntity;
import com.bytedesk.ticket.ticket_sla.TicketSlaStatusEnum;
import com.bytedesk.ticket.ticket_sla.TicketSlaTypeEnum;
import com.bytedesk.ticket.ticket_sla_record.TicketSlaRecordEntity;
import com.bytedesk.ticket.ticket_sla_record.TicketSlaRecordRepository;
import com.bytedesk.ticket.ticket_sla_rule.TicketSlaRuleEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TicketSLAService {

    private static final long DEFAULT_CLAIM_MINUTES = 480L;
    private static final long DEFAULT_RESOLUTION_MINUTES = 1440L;
    private static final String DEFAULT_SLA_COUNTRY_CODE = "CN";
    private static final String DEFAULT_SLA_TIMEZONE = "Asia/Shanghai";
    private static final LocalTime DEFAULT_BUSINESS_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_BUSINESS_END_TIME = LocalTime.of(18, 0);

    private final TicketNotificationService notificationService;
    private final TicketSettingsRepository ticketSettingsRepository;
    private final TicketSlaRecordRepository slaRecordRepository;
    private final TicketRepository ticketRepository;
    private final UidUtils uidUtils;
    private final TaskService taskService;
    private final HolidayRestService holidayRestService;
    // private final RuntimeService runtimeService;

    public Map<String, Object> determineSLA(String category, String priority) {
        Map<String, Object> sla = new HashMap<>();
        sla.put("claimTime", resolveFallbackMinutes(TicketSlaTypeEnum.CLAIM, priority));
        sla.put("responseTime", resolveFallbackMinutes(TicketSlaTypeEnum.FIRST_RESPONSE, priority));
        sla.put("resolutionTime", resolveFallbackMinutes(TicketSlaTypeEnum.RESOLUTION, priority) / 60);
        sla.put("resolutionMinutes", resolveFallbackMinutes(TicketSlaTypeEnum.RESOLUTION, priority));
        return sla;
    }

    public Map<String, Object> buildProcessVariables(TicketEntity ticket) {
        Map<String, Object> variables = new HashMap<>();
        long claimMinutes = resolveDurationMinutes(ticket, TicketSlaTypeEnum.CLAIM);
        long firstResponseMinutes = resolveDurationMinutes(ticket, TicketSlaTypeEnum.FIRST_RESPONSE);
        long resolutionMinutes = resolveDurationMinutes(ticket, TicketSlaTypeEnum.RESOLUTION);
        long customerVerifyMinutes = resolveDurationMinutes(ticket, TicketSlaTypeEnum.CUSTOMER_VERIFY);
        variables.put(TicketConsts.TICKET_VARIABLE_SLA_CLAIM_TIME, toIsoDuration(claimMinutes));
        variables.put(TicketConsts.TICKET_VARIABLE_SLA_FIRST_RESPONSE_TIME, toIsoDuration(firstResponseMinutes));
        variables.put(TicketConsts.TICKET_VARIABLE_SLA_RESOLUTION_TIME, toIsoDuration(resolutionMinutes));
        variables.put(TicketConsts.TICKET_VARIABLE_SLA_CUSTOMER_VERIFY_TIME, toIsoDuration(customerVerifyMinutes));
        variables.put(TicketConsts.TICKET_VARIABLE_SLA_TIME, toIsoDuration(resolutionMinutes));
        return variables;
    }

    public void initializeSlaRecords(TicketEntity ticket) {
        if (ticket == null || !StringUtils.hasText(ticket.getUid())) {
            return;
        }
        createRecordIfAbsent(ticket, TicketSlaTypeEnum.CLAIM, BdDateUtils.now());
        createRecordIfAbsent(ticket, TicketSlaTypeEnum.FIRST_RESPONSE, BdDateUtils.now());
        createRecordIfAbsent(ticket, TicketSlaTypeEnum.RESOLUTION, BdDateUtils.now());
    }

    public void completeClaim(TicketEntity ticket, String operatorUid) {
        complete(ticket, TicketSlaTypeEnum.CLAIM, operatorUid);
    }

    public void completeFirstResponse(TicketEntity ticket, String operatorUid) {
        complete(ticket, TicketSlaTypeEnum.FIRST_RESPONSE, operatorUid);
    }

    public void completeResolution(TicketEntity ticket, String operatorUid) {
        complete(ticket, TicketSlaTypeEnum.RESOLUTION, operatorUid);
    }

    public void startCustomerVerify(TicketEntity ticket) {
        if (ticket == null || !StringUtils.hasText(ticket.getUid())) {
            return;
        }
        createRecordIfNoOpenRecord(ticket, TicketSlaTypeEnum.CUSTOMER_VERIFY, BdDateUtils.now());
    }

    public void completeCustomerVerify(TicketEntity ticket, String operatorUid) {
        complete(ticket, TicketSlaTypeEnum.CUSTOMER_VERIFY, operatorUid);
    }

    public void cancelOpenRecords(TicketEntity ticket, String operatorUid) {
        if (ticket == null || !StringUtils.hasText(ticket.getUid())) {
            return;
        }
        List<TicketSlaRecordEntity> records = slaRecordRepository.findByTicketUidAndDeletedFalse(ticket.getUid());
        for (TicketSlaRecordEntity record : records) {
            if (isOpen(record)) {
                record.setStatus(TicketSlaStatusEnum.CANCELED.name());
                record.setCompletedAt(BdDateUtils.now());
                record.setCompletedBy(operatorUid);
                slaRecordRepository.save(record);
            }
        }
    }

    public void pauseOpenRecords(TicketEntity ticket, String operatorUid) {
        if (ticket == null || !StringUtils.hasText(ticket.getUid()) || !shouldPauseOnHold(ticket)) {
            return;
        }
        ZonedDateTime now = BdDateUtils.now();
        List<TicketSlaRecordEntity> records = slaRecordRepository.findByTicketUidAndDeletedFalse(ticket.getUid());
        for (TicketSlaRecordEntity record : records) {
            if (!isOpen(record)) {
                continue;
            }
            record.setStatus(TicketSlaStatusEnum.PAUSED.name());
            record.setPausedAt(now);
            slaRecordRepository.save(record);
            addSlaComment(record, "SLA_PAUSED", operatorUid,
                    "SLA 已暂停: " + record.getSlaType() + ", 暂停时间: " + now);
        }
    }

    public void resumePausedRecords(TicketEntity ticket, String operatorUid) {
        if (ticket == null || !StringUtils.hasText(ticket.getUid()) || !shouldPauseOnHold(ticket)) {
            return;
        }
        ZonedDateTime now = BdDateUtils.now();
        List<TicketSlaRecordEntity> records = slaRecordRepository.findByTicketUidAndDeletedFalse(ticket.getUid());
        for (TicketSlaRecordEntity record : records) {
            if (!TicketSlaStatusEnum.PAUSED.name().equals(record.getStatus()) || record.getPausedAt() == null) {
                continue;
            }
            long pausedSeconds = Math.max(0, Duration.between(record.getPausedAt(), now).getSeconds());
            long totalPausedSeconds = Optional.ofNullable(record.getPausedDurationSeconds()).orElse(0L) + pausedSeconds;
            record.setPausedDurationSeconds(totalPausedSeconds);
            if (record.getDueAt() != null && pausedSeconds > 0) {
                record.setDueAt(record.getDueAt().plusSeconds(pausedSeconds));
            }
            record.setPausedAt(null);
            record.setStatus(TicketSlaStatusEnum.RUNNING.name());
            slaRecordRepository.save(record);
            addSlaComment(record, "SLA_RESUMED", operatorUid,
                    "SLA 已恢复: " + record.getSlaType() + ", 本次暂停秒数: " + pausedSeconds
                            + ", 新截止时间: " + record.getDueAt());
        }
    }

    public boolean markBreachedByProcessInstance(String processInstanceId, TicketSlaTypeEnum slaType, String reason) {
        if (!StringUtils.hasText(processInstanceId) || slaType == null) {
            return false;
        }
        Optional<TicketSlaRecordEntity> recordOptional = slaRecordRepository
            .findFirstByProcessInstanceIdAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(processInstanceId, slaType.name());
        if (recordOptional.isEmpty()) {
            log.warn("SLA record not found for processInstanceId={}, slaType={}", processInstanceId, slaType);
            return false;
        }
        return markBreached(recordOptional.get(), reason);
    }

    public Boolean isSLABreached(TicketEntity ticket) {
        if (ticket == null || !StringUtils.hasText(ticket.getUid())) {
            return false;
        }
        return slaRecordRepository.findByTicketUidAndDeletedFalse(ticket.getUid()).stream()
                .anyMatch(record -> TicketSlaStatusEnum.BREACHED.name().equals(record.getStatus())
                        || Boolean.TRUE.equals(record.getBreached()));
    }

    public boolean isSlaCompliant(TicketEntity ticket) {
        if (ticket == null || !StringUtils.hasText(ticket.getUid())) {
            return false;
        }
        List<TicketSlaRecordEntity> records = slaRecordRepository.findByTicketUidAndDeletedFalse(ticket.getUid());
        if (!records.isEmpty()) {
            return records.stream()
                    .filter(record -> TicketSlaTypeEnum.RESOLUTION.name().equals(record.getSlaType()))
                    .noneMatch(record -> TicketSlaStatusEnum.BREACHED.name().equals(record.getStatus())
                            || Boolean.TRUE.equals(record.getBreached()));
        }
        if (ticket.getResolvedTime() == null) {
            return false;
        }
        long resolutionMinutes = Duration.between(ticket.getCreatedAt(), ticket.getResolvedTime()).toMinutes();
        return resolutionMinutes <= resolveDurationMinutes(ticket, TicketSlaTypeEnum.RESOLUTION);
    }

    public void recordFirstResponse(TicketEntity ticket) {
        completeFirstResponse(ticket, null);
    }

    public int markWarningRecords() {
        ZonedDateTime now = BdDateUtils.now();
        List<TicketSlaRecordEntity> candidates = slaRecordRepository
                .findTop200ByStatusAndDueAtLessThanEqualAndDeletedFalseOrderByDueAtAsc(
                        TicketSlaStatusEnum.RUNNING.name(), now.plusDays(7));
        int updated = 0;
        for (TicketSlaRecordEntity record : candidates) {
            if (shouldWarn(record, now)) {
                record.setStatus(TicketSlaStatusEnum.WARNED.name());
                slaRecordRepository.save(record);
                addSlaComment(record, "SLA_WARNED", "system",
                    "SLA 即将超时: " + record.getSlaType() + ", 截止时间: " + record.getDueAt());
                notificationService.sendSLAWarningNotification(record.getTicketUid(), record.getSlaType(),
                        "SLA 即将超时，截止时间: " + record.getDueAt());
                updated++;
            }
        }
        return updated;
    }

    public int markBreachedDueRecords() {
        ZonedDateTime now = BdDateUtils.now();
        int updated = 0;
        updated += markBreachedDueRecords(TicketSlaStatusEnum.RUNNING, now);
        updated += markBreachedDueRecords(TicketSlaStatusEnum.WARNED, now);
        return updated;
    }

    private void createRecordIfAbsent(TicketEntity ticket, TicketSlaTypeEnum slaType, ZonedDateTime startedAt) {
        Optional<TicketSlaRecordEntity> existing = slaRecordRepository
                .findFirstByTicketUidAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(ticket.getUid(), slaType.name());
        if (existing.isPresent()) {
            return;
        }
        createRecord(ticket, slaType, startedAt);
    }

    private void createRecordIfNoOpenRecord(TicketEntity ticket, TicketSlaTypeEnum slaType, ZonedDateTime startedAt) {
        Optional<TicketSlaRecordEntity> existing = slaRecordRepository
                .findFirstByTicketUidAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(ticket.getUid(), slaType.name());
        if (existing.filter(this::isOpen).isPresent()) {
            return;
        }
        createRecord(ticket, slaType, startedAt);
    }

    private void createRecord(TicketEntity ticket, TicketSlaTypeEnum slaType, ZonedDateTime startedAt) {
        long durationMinutes = resolveDurationMinutes(ticket, slaType);
        TicketSlaRecordEntity record = TicketSlaRecordEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(ticket.getOrgUid())
                .ticketUid(ticket.getUid())
                .processInstanceId(ticket.getProcessInstanceId())
                .slaType(slaType.name())
                .status(TicketSlaStatusEnum.RUNNING.name())
                .priority(ticket.getPriority())
                .categoryUid(ticket.getCategoryUid())
                .durationMinutes(durationMinutes)
                .startedAt(startedAt)
                .dueAt(resolveDueAt(ticket, startedAt, durationMinutes))
                .breached(Boolean.FALSE)
                .build();
        slaRecordRepository.save(record);
    }

    private void complete(TicketEntity ticket, TicketSlaTypeEnum slaType, String operatorUid) {
        if (ticket == null || !StringUtils.hasText(ticket.getUid())) {
            return;
        }
        slaRecordRepository.findFirstByTicketUidAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(ticket.getUid(), slaType.name())
                .filter(this::isOpen)
                .ifPresent(record -> {
                    ZonedDateTime now = BdDateUtils.now();
                    record.setCompletedAt(now);
                    record.setCompletedBy(operatorUid);
                    if (record.getDueAt() != null && now.isAfter(record.getDueAt())) {
                        record.setStatus(TicketSlaStatusEnum.BREACHED.name());
                        record.setBreached(Boolean.TRUE);
                        record.setBreachedAt(record.getBreachedAt() != null ? record.getBreachedAt() : now);
                        record.setBreachReason("completed after SLA due time");
                    } else {
                        record.setStatus(TicketSlaStatusEnum.COMPLETED.name());
                    }
                    slaRecordRepository.save(record);
                });
    }

    private boolean markBreached(TicketSlaRecordEntity record, String reason) {
        if (record == null || !isOpen(record)) {
            return false;
        }
        ZonedDateTime now = BdDateUtils.now();
        if (record.getDueAt() != null && now.isBefore(record.getDueAt())) {
            log.info("SLA breach skipped before dueAt: ticketUid={}, slaType={}, dueAt={}",
                    record.getTicketUid(), record.getSlaType(), record.getDueAt());
            return false;
        }
        record.setStatus(TicketSlaStatusEnum.BREACHED.name());
        record.setBreached(Boolean.TRUE);
        record.setBreachedAt(now);
        record.setBreachReason(StringUtils.hasText(reason) ? reason : "SLA timer fired");
        slaRecordRepository.save(record);
        addSlaComment(record, "SLA_BREACHED", "system",
                "SLA 已超时: " + record.getSlaType() + ", 原因: " + record.getBreachReason());
        notificationService.sendSLABreachNotification(record.getTicketUid(), record.getSlaType(), record.getBreachReason());
        escalateIfEnabled(record);
        return true;
    }

    public int autoCloseBreachedCustomerVerifyRecords() {
        ZonedDateTime now = BdDateUtils.now();
        List<TicketSlaRecordEntity> records = slaRecordRepository
                .findTop200BySlaTypeAndStatusAndDeletedFalseOrderByBreachedAtAsc(
                        TicketSlaTypeEnum.CUSTOMER_VERIFY.name(),
                        TicketSlaStatusEnum.BREACHED.name());
        int closed = 0;
        for (TicketSlaRecordEntity record : records) {
            Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(record.getTicketUid());
            if (ticketOptional.isEmpty()) continue;
            TicketEntity ticket = ticketOptional.get();
            TicketSlaSettingsEntity settings = resolveSettings(ticket).orElse(null);
            if (settings == null || !Boolean.TRUE.equals(settings.getAutoCloseCustomerPendingEnabled()))
                continue;
            int autoCloseHours = settings.getCustomerVerifyAutoCloseHours() != null
                    ? settings.getCustomerVerifyAutoCloseHours()
                    : 168;
            ZonedDateTime breachedAt = record.getBreachedAt() != null
                    ? record.getBreachedAt()
                    : record.getUpdatedAt();
            if (breachedAt == null || now.isBefore(breachedAt.plusHours(autoCloseHours))) continue;
            try {
                ticket.setStatus(TicketStatusEnum.CLOSED.name());
                ticket.setClosedTime(now);
                ticketRepository.save(ticket);
                addSlaComment(record, "SLA_AUTO_CLOSED", "system",
                        "客户验证超时 " + autoCloseHours + " 小时，工单自动关闭");
                notificationService.sendSLABreachNotification(record.getTicketUid(), record.getSlaType(),
                        "客户验证超时，工单已自动关闭");
                closed++;
            } catch (Exception e) {
                log.warn("autoClose breached customer verify failed: ticketUid={}, error={}",
                        ticket.getUid(), e.getMessage());
            }
        }
        return closed;
    }

    private void addSlaComment(TicketSlaRecordEntity record, String type, String userId, String message) {
        if (record == null || !StringUtils.hasText(record.getProcessInstanceId())) {
            return;
        }
        try {
            Task task = taskService.createTaskQuery()
                    .processInstanceId(record.getProcessInstanceId())
                    .orderByTaskCreateTime()
                    .desc()
                    .list()
                    .stream()
                    .findFirst()
                    .orElse(null);
            org.flowable.engine.task.Comment comment = taskService.addComment(
                    task != null ? task.getId() : null,
                    record.getProcessInstanceId(),
                    type,
                    message);
            if (StringUtils.hasText(userId)) {
                comment.setUserId(userId);
                taskService.saveComment(comment);
            }
        } catch (Exception e) {
            log.warn("failed to add SLA process comment, ticketUid={}, slaType={}, type={}",
                    record.getTicketUid(), record.getSlaType(), type, e);
        }
    }

    private void escalateIfEnabled(TicketSlaRecordEntity record) {
        if (record == null || !StringUtils.hasText(record.getTicketUid())) {
            return;
        }
        Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(record.getTicketUid());
        if (ticketOptional.isEmpty()) {
            return;
        }
        TicketEntity ticket = ticketOptional.get();
        TicketSlaSettingsEntity settings = resolveSettings(ticket).orElse(null);
        if (settings == null || !Boolean.TRUE.equals(settings.getAutoEscalateEnabled())
                || !StringUtils.hasText(settings.getEscalateAssigneeUid())) {
            return;
        }
        String escalateUid = settings.getEscalateAssigneeUid();
        String currentAssigneeUid = ticket.getAssignee() != null ? ticket.getAssignee().getUid() : null;
        // Avoid re-escalating to the same assignee
        if (escalateUid.equals(currentAssigneeUid)) {
            log.debug("SLA escalation skipped: ticket already assigned to escalation target, ticketUid={}",
                    ticket.getUid());
            return;
        }
        try {
            Optional<Task> taskOptional = taskService.createTaskQuery()
                    .processInstanceId(ticket.getProcessInstanceId())
                    .active()
                    .list()
                    .stream()
                    .findFirst();
            if (taskOptional.isPresent()) {
                Task task = taskOptional.get();
                taskService.setAssignee(task.getId(), escalateUid);
            }
            UserProtobuf escalateUser = UserProtobuf.builder()
                    .uid(escalateUid)
                    .build();
            ticket.setAssignee(escalateUser.toJson());
            ticketRepository.save(ticket);
            addSlaComment(record, "SLA_ESCALATED", "system",
                    "SLA 超时自动升级转派: " + record.getSlaType()
                            + ", 新处理人: " + escalateUid);
            log.info("SLA escalated: ticketUid={}, slaType={}, newAssignee={}",
                    ticket.getUid(), record.getSlaType(), escalateUid);
        } catch (Exception e) {
            log.warn("SLA escalation failed: ticketUid={}, slaType={}, error={}",
                    ticket.getUid(), record.getSlaType(), e.getMessage());
        }
    }

    private int markBreachedDueRecords(TicketSlaStatusEnum status, ZonedDateTime now) {
        List<TicketSlaRecordEntity> records = slaRecordRepository
                .findTop200ByStatusAndDueAtLessThanEqualAndDeletedFalseOrderByDueAtAsc(status.name(), now);
        int updated = 0;
        for (TicketSlaRecordEntity record : records) {
            if (markBreached(record, "SLA 截止时间已到: " + record.getDueAt())) {
                updated++;
            }
        }
        return updated;
    }

    private boolean isOpen(TicketSlaRecordEntity record) {
        return record != null && (TicketSlaStatusEnum.RUNNING.name().equals(record.getStatus())
                || TicketSlaStatusEnum.WARNED.name().equals(record.getStatus()));
    }

    private boolean shouldPauseOnHold(TicketEntity ticket) {
        TicketSlaSettingsEntity settings = resolveSettings(ticket).orElse(null);
        return settings != null && Boolean.TRUE.equals(settings.getEnabled())
                && Boolean.TRUE.equals(settings.getPauseOnHold());
    }

    private boolean shouldWarn(TicketSlaRecordEntity record, ZonedDateTime now) {
        if (record == null || record.getStartedAt() == null || record.getDueAt() == null
                || record.getDurationMinutes() == null || record.getDurationMinutes() <= 0) {
            return false;
        }
        if (!TicketSlaStatusEnum.RUNNING.name().equals(record.getStatus()) || !now.isBefore(record.getDueAt())) {
            return false;
        }
        Optional<TicketEntity> ticketOptional = findTicket(record);
        long warningElapsedMinutes = resolveWarningElapsedMinutes(record);
        if (warningElapsedMinutes <= 0) {
            return false;
        }
        long elapsedMinutes = Math.min(warningElapsedMinutes, record.getDurationMinutes());
        ZonedDateTime warningAt = ticketOptional
                .filter(this::isBusinessHoursEnabled)
                .map(ticket -> addBusinessMinutes(ticket, record.getStartedAt(), elapsedMinutes))
                .orElse(record.getStartedAt().plusMinutes(elapsedMinutes));
        return !now.isBefore(warningAt);
    }

    private long resolveWarningElapsedMinutes(TicketSlaRecordEntity record) {
        Optional<TicketEntity> ticketOptional = findTicket(record);
        if (ticketOptional.isPresent()) {
            TicketEntity ticket = ticketOptional.get();
            TicketSlaSettingsEntity settings = resolveSettings(ticket).orElse(null);
            if (settings != null && Boolean.TRUE.equals(settings.getEnabled())) {
                if (settings.getRules() != null) {
                    Optional<Long> ruleWarning = settings.getRules().stream()
                            .filter(rule -> Boolean.TRUE.equals(rule.getEnabled()))
                            .filter(rule -> !StringUtils.hasText(rule.getSlaType()) || rule.getSlaType().equals(record.getSlaType()))
                            .filter(rule -> !StringUtils.hasText(rule.getPriority()) || rule.getPriority().equalsIgnoreCase(record.getPriority()))
                            .filter(rule -> !StringUtils.hasText(rule.getCategoryUid()) || rule.getCategoryUid().equals(record.getCategoryUid()))
                            .sorted(Comparator.comparing(rule -> rule.getOrderIndex() == null ? 0 : rule.getOrderIndex()))
                            .map(TicketSlaRuleEntity::getWarningMinutes)
                            .filter(minutes -> minutes != null && minutes > 0)
                            .findFirst();
                    if (ruleWarning.isPresent()) {
                        return ruleWarning.get();
                    }
                }
                if (settings.getWarningPercent() != null && settings.getWarningPercent() > 0) {
                    return Math.max(1, record.getDurationMinutes() * Math.min(settings.getWarningPercent(), 100) / 100);
                }
            }
        }
        return Math.max(1, record.getDurationMinutes() * 80 / 100);
    }

    private Optional<TicketEntity> findTicket(TicketSlaRecordEntity record) {
        return record != null && StringUtils.hasText(record.getTicketUid())
                ? ticketRepository.findByUid(record.getTicketUid())
                : Optional.empty();
    }

    private ZonedDateTime resolveDueAt(TicketEntity ticket, ZonedDateTime startedAt, long durationMinutes) {
        if (startedAt == null) {
            return null;
        }
        if (isBusinessHoursEnabled(ticket)) {
            return addBusinessMinutes(ticket, startedAt, durationMinutes);
        }
        return startedAt.plusMinutes(durationMinutes);
    }

    private boolean isBusinessHoursEnabled(TicketEntity ticket) {
        if (ticket == null) {
            return false;
        }
        TicketSlaSettingsEntity settings = resolveSettings(ticket).orElse(null);
        return settings != null && Boolean.TRUE.equals(settings.getEnabled())
                && Boolean.TRUE.equals(settings.getBusinessHoursEnabled());
    }

    private ZonedDateTime addBusinessMinutes(TicketEntity ticket, ZonedDateTime start, long minutes) {
        TicketSlaSettingsEntity settings = resolveSettings(ticket).orElse(null);
        ZoneId zoneId = resolveBusinessZone(settings);
        LocalTime businessEndTime = resolveBusinessEndTime(settings);
        long remainingSeconds = Math.max(1, minutes) * 60;
        ZonedDateTime cursor = normalizeToBusinessTime(settings, start.withZoneSameInstant(zoneId));
        while (remainingSeconds > 0) {
            if (!isBusinessDay(settings, cursor.toLocalDate())) {
                cursor = nextBusinessStart(settings, cursor.toLocalDate().plusDays(1), zoneId);
                continue;
            }
            ZonedDateTime businessEnd = cursor.with(businessEndTime);
            long availableSeconds = Math.max(0, Duration.between(cursor, businessEnd).getSeconds());
            if (remainingSeconds <= availableSeconds) {
                return cursor.plusSeconds(remainingSeconds);
            }
            remainingSeconds -= availableSeconds;
            cursor = nextBusinessStart(settings, cursor.toLocalDate().plusDays(1), zoneId);
        }
        return cursor;
    }

    private ZonedDateTime normalizeToBusinessTime(TicketSlaSettingsEntity settings, ZonedDateTime dateTime) {
        ZonedDateTime cursor = dateTime;
        LocalTime businessStartTime = resolveBusinessStartTime(settings);
        LocalTime businessEndTime = resolveBusinessEndTime(settings);
        while (!isBusinessDay(settings, cursor.toLocalDate())) {
            cursor = nextBusinessStart(settings, cursor.toLocalDate().plusDays(1), cursor.getZone());
        }
        if (cursor.toLocalTime().isBefore(businessStartTime)) {
            return cursor.with(businessStartTime);
        }
        if (!cursor.toLocalTime().isBefore(businessEndTime)) {
            return nextBusinessStart(settings, cursor.toLocalDate().plusDays(1), cursor.getZone());
        }
        return cursor;
    }

    private ZonedDateTime nextBusinessStart(TicketSlaSettingsEntity settings, LocalDate date, ZoneId zoneId) {
        ZonedDateTime cursor = date.atTime(resolveBusinessStartTime(settings)).atZone(zoneId);
        while (!isBusinessDay(settings, cursor.toLocalDate())) {
            cursor = cursor.plusDays(1);
        }
        return cursor;
    }

    private boolean isBusinessDay(TicketSlaSettingsEntity settings, LocalDate date) {
        Optional<HolidayEntity> holiday = findOfficialHoliday(settings, date);
        if (holiday.isPresent()) {
            return !Boolean.TRUE.equals(holiday.get().getOffDay());
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    private Optional<HolidayEntity> findOfficialHoliday(TicketSlaSettingsEntity settings, LocalDate date) {
        String countryCode = resolveBusinessCountryCode(settings);
        if ("NONE".equals(countryCode)) {
            return Optional.empty();
        }
        try {
            return holidayRestService.findByCountryAndYear(countryCode, date.getYear()).stream()
                    .filter(holiday -> Boolean.TRUE.equals(holiday.getOfficial()))
                    .filter(holiday -> date.equals(holiday.getHolidayDate()))
                    .findFirst();
        } catch (Exception e) {
            log.debug("SLA business day holiday lookup failed, countryCode={}, date={}", countryCode, date, e);
            return Optional.empty();
        }
    }

    private LocalTime resolveBusinessStartTime(TicketSlaSettingsEntity settings) {
        return parseBusinessTime(settings != null ? settings.getBusinessHoursStartTime() : null,
                DEFAULT_BUSINESS_START_TIME);
    }

    private LocalTime resolveBusinessEndTime(TicketSlaSettingsEntity settings) {
        LocalTime startTime = resolveBusinessStartTime(settings);
        LocalTime endTime = parseBusinessTime(settings != null ? settings.getBusinessHoursEndTime() : null,
                DEFAULT_BUSINESS_END_TIME);
        if (endTime.isAfter(startTime)) {
            return endTime;
        }
        if (DEFAULT_BUSINESS_END_TIME.isAfter(startTime)) {
            return DEFAULT_BUSINESS_END_TIME;
        }
        return LocalTime.MAX;
    }

    private LocalTime parseBusinessTime(String value, LocalTime fallback) {
        if (!StringUtils.hasText(value)) {
            return fallback;
        }
        try {
            return LocalTime.parse(value.trim());
        } catch (DateTimeException e) {
            log.debug("Invalid SLA business time: {}", value, e);
            return fallback;
        }
    }

    private ZoneId resolveBusinessZone(TicketSlaSettingsEntity settings) {
        String timezone = settings != null && StringUtils.hasText(settings.getBusinessHoursTimezone())
                ? settings.getBusinessHoursTimezone().trim()
                : DEFAULT_SLA_TIMEZONE;
        try {
            return ZoneId.of(timezone);
        } catch (DateTimeException e) {
            log.debug("Invalid SLA business timezone: {}", timezone, e);
            return ZoneId.of(DEFAULT_SLA_TIMEZONE);
        }
    }

    private String resolveBusinessCountryCode(TicketSlaSettingsEntity settings) {
        return settings != null && StringUtils.hasText(settings.getBusinessHoursCountryCode())
                ? settings.getBusinessHoursCountryCode().trim().toUpperCase()
                : DEFAULT_SLA_COUNTRY_CODE;
    }

    private long resolveDurationMinutes(TicketEntity ticket, TicketSlaTypeEnum slaType) {
        if (ticket == null || slaType == null) {
            return DEFAULT_RESOLUTION_MINUTES;
        }
        TicketSlaSettingsEntity settings = resolveSettings(ticket).orElse(null);
        if (settings == null || Boolean.FALSE.equals(settings.getEnabled()) || settings.getRules() == null) {
            return resolveFallbackMinutes(slaType, ticket.getPriority());
        }
        return settings.getRules().stream()
                .filter(rule -> Boolean.TRUE.equals(rule.getEnabled()))
                .filter(rule -> slaType.name().equals(rule.getSlaType()))
                .filter(rule -> !StringUtils.hasText(rule.getPriority()) || rule.getPriority().equalsIgnoreCase(ticket.getPriority()))
                .filter(rule -> !StringUtils.hasText(rule.getCategoryUid()) || rule.getCategoryUid().equals(ticket.getCategoryUid()))
                .sorted(Comparator.comparing(rule -> rule.getOrderIndex() == null ? 0 : rule.getOrderIndex()))
                .map(TicketSlaRuleEntity::getDurationMinutes)
                .filter(minutes -> minutes != null && minutes > 0)
                .findFirst()
                .orElse(resolveFallbackMinutes(slaType, ticket.getPriority()));
    }

    private Optional<TicketSlaSettingsEntity> resolveSettings(TicketEntity ticket) {
        if (StringUtils.hasText(ticket.getTicketSettingsUid())) {
            Optional<TicketSettingsEntity> settingsOptional = ticketSettingsRepository.findByUid(ticket.getTicketSettingsUid());
            if (settingsOptional.isPresent() && settingsOptional.get().getSlaSettings() != null) {
                return Optional.of(settingsOptional.get().getSlaSettings());
            }
        }
        List<TicketSettingsEntity> defaults = ticketSettingsRepository
                .findByOrgUidAndTypeAndIsDefaultTrue(ticket.getOrgUid(), ticket.getType());
        return defaults.stream()
                .map(TicketSettingsEntity::getSlaSettings)
                .filter(java.util.Objects::nonNull)
                .findFirst();
    }

    private long resolveFallbackMinutes(TicketSlaTypeEnum slaType, String priority) {
        if (TicketSlaTypeEnum.RESOLUTION.equals(slaType)) {
            return switch (StringUtils.hasText(priority) ? priority.toUpperCase() : "") {
                case "CRITICAL" -> 60L;
                case "URGENT" -> 120L;
                case "HIGH" -> 240L;
                case "MEDIUM" -> 480L;
                case "LOW" -> 1440L;
                default -> DEFAULT_RESOLUTION_MINUTES;
            };
        }
        return switch (StringUtils.hasText(priority) ? priority.toUpperCase() : "") {
            case "CRITICAL" -> 30L;
            case "URGENT" -> 60L;
            case "HIGH" -> 120L;
            case "MEDIUM" -> 240L;
            case "LOW" -> 480L;
            default -> DEFAULT_CLAIM_MINUTES;
        };
    }

    private String toIsoDuration(long minutes) {
        return Duration.ofMinutes(Math.max(1, minutes)).toString();
    }

    /**
     * Build SLA compliance breakdown by SLA type for a set of tickets.
     * Returns per-type {total, compliant, breached} counts.
     */
    public List<TicketSlaTypePoint> buildSlaTypePoints(Collection<String> ticketUids) {
        if (ticketUids == null || ticketUids.isEmpty()) {
            return new ArrayList<>();
        }
        List<TicketSlaRecordEntity> records = slaRecordRepository.findByTicketUidInAndDeletedFalse(
                new ArrayList<>(ticketUids));
        Map<String, TicketSlaTypePoint> points = new HashMap<>();
        for (TicketSlaRecordEntity record : records) {
            String slaType = record.getSlaType();
            if (!StringUtils.hasText(slaType)) continue;
            TicketSlaTypePoint point = points.computeIfAbsent(slaType,
                    k -> new TicketSlaTypePoint(slaType, 0L, 0L, 0L));
            point.total++;
            if (TicketSlaStatusEnum.BREACHED.name().equals(record.getStatus())
                    || Boolean.TRUE.equals(record.getBreached())) {
                point.breached++;
            } else if (TicketSlaStatusEnum.COMPLETED.name().equals(record.getStatus())
                    && record.getCompletedAt() != null && record.getDueAt() != null
                    && !record.getCompletedAt().isAfter(record.getDueAt())) {
                point.compliant++;
            } else if (TicketSlaStatusEnum.COMPLETED.name().equals(record.getStatus())) {
                point.breached++;
            }
        }
        return points.values().stream()
                .sorted(Comparator.comparing(TicketSlaTypePoint::getSlaType))
                .collect(Collectors.toList());
    }

    /**
     * SLA type breakdown point for statistics.
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class TicketSlaTypePoint {
        private String slaType;
        private long total;
        private long compliant;
        private long breached;
    }
}