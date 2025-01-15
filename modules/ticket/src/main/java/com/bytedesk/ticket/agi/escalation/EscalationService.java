package com.bytedesk.ticket.agi.escalation;

import com.bytedesk.ticket.ticket.TicketEntity;
import java.util.List;

public interface EscalationService {
    
    // 规则管理
    EscalationRuleEntity createRule(EscalationRuleEntity rule);
    EscalationRuleEntity updateRule(Long ruleId, EscalationRuleEntity rule);
    void deleteRule(Long ruleId);
    EscalationRuleEntity getRule(Long ruleId);
    List<EscalationRuleEntity> getAllRules();
    List<EscalationRuleEntity> getMatchingRules(Long categoryId, String priority);
    
    // 升级检查
    void checkEscalation(TicketEntity ticket);
    void checkEscalations(); // 批量检查所有需要升级的工单
    
    // 手动升级
    void escalateTicket(Long ticketId, Long targetUserId, String reason);
    
    // 升级历史
    List<EscalationHistoryEntity> getEscalationHistory(Long ticketId);
    EscalationHistoryEntity getLastEscalation(Long ticketId);
    
    // 通知相关
    void sendEscalationNotifications(Long ticketId, Long ruleId, String[] recipients);
    void resendFailedNotifications(); // 重试发送失败的通知
} 