package com.bytedesk.kbase.llm_file;

import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.llm_file.event.FileCreateEvent;
import com.bytedesk.kbase.llm_file.event.FileDeleteEvent;
import com.bytedesk.kbase.llm_file.event.FileUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileEntityListener {

    @PostPersist
    public void onPostPersist(FileEntity file) {
        log.info("FileEntityListener: onPostPersist");
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new FileCreateEvent(this, file));
    }

    @PostUpdate
    public void onPostUpdate(FileEntity file) {
        log.info("FileEntityListener: onPostUpdate");
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (file.isDeleted()) {
            publisher.publishEvent(new FileDeleteEvent(this, file));
        } else {
            publisher.publishEvent(new FileUpdateEvent(this, file));
        }
    }
}
