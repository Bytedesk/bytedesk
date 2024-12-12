package com.bytedesk.ticket.sla;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ticket.ticket.TicketService;
import com.bytedesk.ticket.event.TicketEventService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TicketSLAServiceImpl implements TicketSLAService {

    @Autowired
    private TicketSLARepository slaRepository;
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private TicketEventService eventService;

    private static final LocalTime BUSINESS_HOURS_START = LocalTime.of(9, 0);  // 9:00 AM
    private static final LocalTime BUSINESS_HOURS_END = LocalTime.of(18, 0);   // 6:00 PM

    @Override
    @Transactional
    public TicketSLAEntity createSLA(TicketSLAEntity sla) {
        return slaRepository.save(sla);
    }

    @Override
    @Transactional
    public TicketSLAEntity updateSLA(Long slaId, TicketSLAEntity sla) {
        TicketSLAEntity existingSLA = getSLA(slaId);
        // 更新字段
        existingSLA.setName(sla.getName());
        existingSLA.setDescription(sla.getDescription());
        existingSLA.setFirstResponseTime(sla.getFirstResponseTime());
        existingSLA.setResolutionTime(sla.getResolutionTime());
        existingSLA.setPriority(sla.getPriority());
        existingSLA.setCategoryId(sla.getCategoryId());
        existingSLA.setBusinessHoursOnly(sla.getBusinessHoursOnly());
        existingSLA.setEscalationTime(sla.getEscalationTime());
        existingSLA.setEscalationUserId(sla.getEscalationUserId());
        return slaRepository.save(existingSLA);
    }

    @Override
    @Transactional
    public void deleteSLA(Long slaId) {
        slaRepository.deleteById(slaId);
    }

    @Override
    public TicketSLAEntity getSLA(Long slaId) {
        return slaRepository.findById(slaId)
            .orElseThrow(() -> new RuntimeException("SLA not found"));
    }

    @Override
    public List<TicketSLAEntity> getAllSLAs() {
        return slaRepository.findAll();
    }

    @Override
    public TicketSLAEntity getMatchingSLA(String priority, Long categoryId) {
        return slaRepository.findMatchingSLA(priority, categoryId)
            .orElseThrow(() -> new RuntimeException("No matching SLA found"));
    }

    @Override
    public LocalDateTime calculateDueDate(TicketSLAEntity sla, LocalDateTime startTime) {
        if (!sla.getBusinessHoursOnly()) {
            return startTime.plusMinutes(sla.getResolutionTime());
        }

        LocalDateTime dueDate = startTime;
        long remainingMinutes = sla.getResolutionTime();

        while (remainingMinutes > 0) {
            if (isWithinBusinessHours(dueDate)) {
                dueDate = dueDate.plusMinutes(1);
                remainingMinutes--;
            } else {
                // 跳到下一个工作日开始
                dueDate = dueDate.plusDays(1).with(BUSINESS_HOURS_START);
            }
        }

        return dueDate;
    }

    @Override
    public boolean isWithinBusinessHours(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        return dateTime.toLocalDate().getDayOfWeek().getValue() < 6 && // 不是周末
                time.isAfter(BUSINESS_HOURS_START) &&
                time.isBefore(BUSINESS_HOURS_END);
    }

    @Override
    public long calculateElapsedTime(LocalDateTime startTime, LocalDateTime endTime, boolean businessHoursOnly) {
        if (!businessHoursOnly) {
            return ChronoUnit.MINUTES.between(startTime, endTime);
        }

        long elapsedMinutes = 0;
        LocalDateTime currentTime = startTime;

        while (currentTime.isBefore(endTime)) {
            if (isWithinBusinessHours(currentTime)) {
                elapsedMinutes++;
            }
            currentTime = currentTime.plusMinutes(1);
        }

        return elapsedMinutes;
    }

    @Override
    @Scheduled(fixedRate = 300000) // 每5分钟检查一次
    @Transactional
    public void checkAndEscalateTickets() {
        // 获取所有未解决的工单
        ticketService.getUnresolvedTickets().forEach(ticket -> {
            TicketSLAEntity sla = getMatchingSLA(ticket.getPriority(), ticket.getCategoryId());
            
            // 检查是否超过升级时限
            long elapsedTime = calculateElapsedTime(
                ticket.getCreatedAt(), 
                LocalDateTime.now(), 
                sla.getBusinessHoursOnly()
            );
            
            if (elapsedTime > sla.getEscalationTime() && sla.getEscalationUserId() != null) {
                // 升级工单
                ticketService.assignTicket(ticket.getId(), sla.getEscalationUserId());
                
                // 记录升级事件
                eventService.recordEvent(ticket.getId(), sla.getEscalationUserId(), 
                    "ticket_escalated", null, null,
                    "Ticket escalated due to SLA breach");
            }
        });
    }
} 