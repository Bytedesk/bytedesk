package com.bytedesk.service.visitor.event;

import com.bytedesk.service.visitor.VisitorEntity;

public class VisitorUpdateEvent extends AbstractVisitorEvent {
    
    private static final long serialVersionUID = 1L;

    public VisitorUpdateEvent(Object source, VisitorEntity visitor) {
        super(source, visitor);
    }
}