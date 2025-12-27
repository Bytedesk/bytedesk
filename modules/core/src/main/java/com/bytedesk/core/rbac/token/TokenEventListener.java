/*
 * @Author: jackning 270580156@qq.com
 * @Description: Token event listener
 */
package com.bytedesk.core.rbac.token;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.token.event.TokenCreateEvent;
import com.bytedesk.core.rbac.token.event.TokenUpdateEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenEventListener {

    @EventListener
    public void handleTokenCreateEvent(TokenCreateEvent event) {
        log.info("Token created: {}", event.getToken());
    }

    @EventListener
    public void handleTokenUpdateEvent(TokenUpdateEvent event) {
        log.info("Token updated: {}", event.getToken());
    }

}
