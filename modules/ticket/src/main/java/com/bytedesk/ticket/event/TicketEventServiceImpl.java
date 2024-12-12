package com.bytedesk.ticket.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketEventServiceImpl implements TicketEventService {

    @Autowired
    private TicketEventRepository eventRepository;

    @Override
    @Transactional
    public TicketEventEntity recordEvent(Long ticketId, Long userId, String event, 
            String oldValue, String newValue, String description) {
        TicketEventEntity eventEntity = new TicketEventEntity();
        eventEntity.setTicketId(ticketId);
        eventEntity.setUserId(userId);
        eventEntity.setEvent(event);
        eventEntity.setOldValue(oldValue);
        eventEntity.setNewValue(newValue);
        eventEntity.setDescription(description);
        
        return eventRepository.save(eventEntity);
    }

    @Override
    public Page<TicketEventEntity> getEventsByTicket(Long ticketId, Pageable pageable) {
        return eventRepository.findByTicketId(ticketId, pageable);
    }

    @Override
    public Page<TicketEventEntity> getEventsByUser(Long userId, Pageable pageable) {
        return eventRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<TicketEventEntity> getEventsByTicketAndType(Long ticketId, String event, Pageable pageable) {
        return eventRepository.findByTicketIdAndEvent(ticketId, event, pageable);
    }

    @Override
    public TicketEventEntity getEvent(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
    }
} 