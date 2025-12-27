/*
 * @Author: jackning 270580156@qq.com
 * @Description: Token entity listener
 */
package com.bytedesk.core.rbac.token;

import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.rbac.token.event.TokenCreateEvent;
import com.bytedesk.core.rbac.token.event.TokenUpdateEvent;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenEntityListener {

    @PostPersist
    public void onPostPersist(TokenEntity token) {
        log.info("onPostPersist: {}", token);
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new TokenCreateEvent(token));
    }

    @PostUpdate
    public void onPostUpdate(TokenEntity token) {
        log.info("onPostUpdate: {}", token);
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new TokenUpdateEvent(token));
    }

}
