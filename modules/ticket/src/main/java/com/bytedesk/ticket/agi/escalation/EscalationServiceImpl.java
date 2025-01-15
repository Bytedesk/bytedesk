package com.bytedesk.ticket.agi.escalation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ticket.ticket.TicketService;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.agi.notification.TicketNotificationService;
import com.bytedesk.ticket.event.TicketEventService;

import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Service
public class EscalationServiceImpl implements EscalationService {

    @Autowired
    private EscalationRuleRepository ruleRepository;

    // @Autowired
    // private EscalationHistoryRepository historyRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketEventService eventService;

    @Autowired
    private TicketNotificationService notificationService;

    // @Autowired
    // private GroupMemberService groupMemberService;

    @Override
    @Transactional
    public EscalationRuleEntity createRule(EscalationRuleEntity rule) {
        validateRule(rule);
        return ruleRepository.save(rule);
    }

    @Override
    @Transactional
    public EscalationRuleEntity updateRule(Long ruleId, EscalationRuleEntity rule) {
        EscalationRuleEntity existingRule = getRule(ruleId);
        validateRule(rule);

        existingRule.setName(rule.getName());
        existingRule.setDescription(rule.getDescription());
        existingRule.setCategoryId(rule.getCategoryId());
        existingRule.setPriority(rule.getPriority());
        existingRule.setConditionType(rule.getConditionType());
        existingRule.setConditionValue(rule.getConditionValue());
        existingRule.setTargetUserId(rule.getTargetUserId());
        existingRule.setTargetGroupId(rule.getTargetGroupId());
        existingRule.setNotifyUsers(rule.getNotifyUsers());
        existingRule.setEnabled(rule.getEnabled());

        return ruleRepository.save(existingRule);
    }

    private void validateRule(EscalationRuleEntity rule) {
        if (rule.getTargetUserId() == null && rule.getTargetGroupId() == null) {
            throw new IllegalArgumentException("Either target user or target group must be specified");
        }

        if (rule.getConditionType() == null) {
            throw new IllegalArgumentException("Condition type must be specified");
        }

        if (rule.getConditionValue() != null && rule.getConditionValue() <= 0) {
            throw new IllegalArgumentException("Condition value must be positive");
        }
    }

    @Override
    @Transactional
    public void deleteRule(Long ruleId) {
        ruleRepository.deleteById(ruleId);
    }

    @Override
    public EscalationRuleEntity getRule(Long ruleId) {
        return ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Escalation rule not found"));
    }

    @Override
    public List<EscalationRuleEntity> getAllRules() {
        return ruleRepository.findAll();
    }

    @Override
    public List<EscalationRuleEntity> getMatchingRules(Long categoryId, String priority) {
        return ruleRepository.findMatchingRules(categoryId, priority);
    }

    @Override
    @Transactional
    public void checkEscalation(TicketEntity ticket) {
        if (isTicketClosed(ticket)) {
            return;
        }

        List<EscalationRuleEntity> rules = getMatchingRules(ticket.getCategoryId(), ticket.getPriority());

        for (EscalationRuleEntity rule : rules) {
            if (shouldEscalate(ticket, rule)) {
                performEscalation(ticket, rule);
                break; // 只执行第一个匹配的规则
            }
        }
    }

    private boolean isTicketClosed(TicketEntity ticket) {
        return "closed".equals(ticket.getStatus()) || "resolved".equals(ticket.getStatus());
    }

    private boolean shouldEscalate(TicketEntity ticket, EscalationRuleEntity rule) {
        // 检查是否已经升级过
        // Optional<EscalationHistoryEntity> lastEscalation =
        // historyRepository.findTopByTicketIdOrderByCreatedAtDesc(ticket.getId());
        // if (lastEscalation.isPresent()) {
        // // 如果最后一次升级是由同一规则触发的，且在24小时内，则不再升级
        // if (lastEscalation.get().getRuleId().equals(rule.getId()) &&
        // ChronoUnit.HOURS.between(lastEscalation.get().getCreatedAt(),
        // LocalDateTime.now()) < 24) {
        // return false;
        // }
        // }

        // LocalDateTime now = LocalDateTime.now();

        // return switch (rule.getConditionType()) {
        // case NO_RESPONSE -> {
        // if (ticket.getFirstResponseTime() == null) {
        // long minutes = ChronoUnit.MINUTES.between(ticket.getCreatedAt(), now);
        // yield minutes >= rule.getConditionValue();
        // }
        // yield false;
        // }
        // case NO_UPDATE -> {
        // LocalDateTime lastUpdate = ticket.getUpdatedAt();
        // long minutes = ChronoUnit.MINUTES.between(lastUpdate, now);
        // yield minutes >= rule.getConditionValue();
        // }
        // case OVERDUE -> ticket.getDueDate() != null &&
        // now.isAfter(ticket.getDueDate());
        // case SLA_BREACH -> ticket.getSlaBreached() != null &&
        // ticket.getSlaBreached();
        // };

        return true;
    }

    @Transactional
    protected void performEscalation(TicketEntity ticket, EscalationRuleEntity rule) {
        Long targetUserId = rule.getTargetUserId();
        if (targetUserId == null && rule.getTargetGroupId() != null) {
            targetUserId = selectUserFromGroup(rule.getTargetGroupId());
        }

        if (targetUserId != null) {
            // 记录原始处理人
            Long originalAssignee = ticket.getAssignedTo();

            // 更新工单分配
            ticketService.assignTicket(ticket.getId(), targetUserId);

            // 记录升级历史
            // EscalationHistoryEntity history = saveEscalationHistory(ticket, rule,
            // originalAssignee, targetUserId);

            // 记录事件
            eventService.recordEvent(ticket.getId(), targetUserId,
                    "ticket_escalated",
                    originalAssignee != null ? originalAssignee.toString() : null,
                    targetUserId.toString(),
                    "Ticket escalated due to: " + rule.getConditionType());

            // 发送通知
            if (rule.getNotifyUsers() != null && !rule.getNotifyUsers().isEmpty()) {
                sendEscalationNotifications(ticket.getId(), rule.getId(),
                        rule.getNotifyUsers().split(","));
            }
        }
    }

    private Long selectUserFromGroup(Long groupId) {
        // Optional<Long> selectedUser =
        // groupMemberService.selectAvailableMember(groupId);
        // if (selectedUser.isPresent()) {
        // groupMemberService.updateLastAssignedTime(groupId, selectedUser.get());
        // return selectedUser.get();
        // }
        log.warn("No available member found in group: {}", groupId);
        return null;
    }

    // private EscalationHistoryEntity saveEscalationHistory(TicketEntity ticket,
    // EscalationRuleEntity rule,
    // Long fromUserId, Long toUserId) {
    // EscalationHistoryEntity history = new EscalationHistoryEntity();
    // history.setTicketId(ticket.getId());
    // history.setRuleId(rule.getId());
    // history.setFromUserId(fromUserId);
    // history.setToUserId(toUserId);
    // history.setReason(rule.getConditionType().toString());
    // history.setNotifiedUsers(rule.getNotifyUsers());
    // return historyRepository.save(history);
    // }

    @Override
    public void sendEscalationNotifications(Long ticketId, Long ruleId, String[] recipients) {
        for (String recipient : recipients) {
            try {
                notificationService.sendEscalationNotification(
                        Long.valueOf(recipient.trim()),
                        ticketId,
                        ruleId);
            } catch (Exception e) {
                log.error("Failed to send escalation notification to user: " + recipient, e);
            }
        }
    }

    @Override
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    @Transactional
    public void checkEscalations() {
        ticketService.getUnresolvedTickets().forEach(this::checkEscalation);
    }

    @Override
    @Scheduled(fixedRate = 900000) // 每15分钟执行一次
    @Transactional
    public void resendFailedNotifications() {
        // TODO: 实现重发失败通知的逻辑
    }

    @Override
    @Transactional
    public void escalateTicket(Long ticketId, Long ruleId, String reason) {
        // 获取工单和规则
        TicketEntity ticket = ticketService.getTicket(ticketId);
        EscalationRuleEntity rule = getRule(ruleId);

        // 验证是否可以升级
        if (!shouldEscalate(ticket, rule)) {
            log.warn("Ticket {} does not meet escalation criteria for rule {}", ticketId, ruleId);
            return;
        }

        // 执行升级操作
        if (rule.getTargetUserId() != null) {
            // 分配给指定用户
            ticketService.assignTicket(ticketId, rule.getTargetUserId());
        } else if (rule.getTargetGroupId() != null) {
            // 从组内选择用户
            Long selectedUser = selectUserFromGroup(rule.getTargetGroupId());
            if (selectedUser != null) {
                ticketService.assignTicket(ticketId, selectedUser);
            }
        }

        // 记录升级历史
        // EscalationHistoryEntity history = createEscalationHistory(ticket, rule, reason);

        // 发送通知
        if (rule.getNotifyUsers() != null && !rule.getNotifyUsers().isEmpty()) {
            sendEscalationNotifications(ticketId, ruleId, rule.getNotifyUsers().split(","));
        }

        log.info("Ticket {} escalated with rule {}", ticketId, ruleId);
    }

    @Override
    public EscalationHistoryEntity getLastEscalation(Long ticketId) {
        // return historyRepository.findTopByTicketIdOrderByCreatedAtDesc(ticketId)
        //         .orElse(null);
        return null;
    }

    @Override
    public List<EscalationHistoryEntity> getEscalationHistory(Long ticketId) {
        // return historyRepository.findByTicketId(ticketId);
        return null;
    }

    // ... 实现其他方法
}