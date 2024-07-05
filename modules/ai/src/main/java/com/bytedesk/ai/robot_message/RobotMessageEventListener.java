package com.bytedesk.ai.robot_message;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.message.MessageCreateEvent;
import com.bytedesk.core.message.MessageUpdateEvent;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Component
public class RobotMessageEventListener {
    
    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        // log.info("robot message unread create event: " + event);
        
    }

    @EventListener
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        // log.info("robot message unread update event: " + event);
    }

}
