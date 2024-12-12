package com.bytedesk.service.queue.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import com.bytedesk.service.queue_member.QueueMemberEntity;

@Getter
public class QueueMemberJoinedEvent extends ApplicationEvent {
    
    private final QueueMemberEntity member;
    
    public QueueMemberJoinedEvent(QueueMemberEntity member) {
        super(member);
        this.member = member;
    }
} 