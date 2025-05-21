package com.bytedesk.kbase.llm_file;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

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
        // 
        FileEntity clonedFile = SerializationUtils.clone(file);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new FileCreateEvent(clonedFile));
    }

    @PostUpdate
    public void onPostUpdate(FileEntity file) {
        log.info("FileEntityListener: onPostUpdate");
        // 
        FileEntity clonedFile = SerializationUtils.clone(file);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (file.getDeleted()) {
            publisher.publishEvent(new FileDeleteEvent(clonedFile));
        } else {
            publisher.publishEvent(new FileUpdateEvent(clonedFile));
        }
    }
}
