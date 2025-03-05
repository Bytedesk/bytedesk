package com.bytedesk.service.visitor.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.service.visitor.VisitorEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class VisitorUpdateEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;

    private VisitorEntity visitor;

    public VisitorUpdateEvent(Object source, VisitorEntity visitor) {
        super(source);
        this.visitor = visitor;
    }
}