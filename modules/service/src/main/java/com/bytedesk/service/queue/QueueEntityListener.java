package com.bytedesk.service.queue;

import org.springframework.stereotype.Component;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QueueEntityListener {

    @PostPersist
    public void postPersist(QueueEntity queue) {
        log.info("QueueEntity postPersist {}", queue.getUid());
    }

    @PostUpdate
    public void postUpdate(QueueEntity queue) {
        log.info("QueueEntity postUpdate {}", queue.getUid());
    }


}
