/*
 * @Author: jackning 270580156@qq.com
 * @Description: Base token event with detached snapshot
 */
package com.bytedesk.core.rbac.token.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.rbac.token.TokenEntity;

public abstract class AbstractTokenEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final TokenEntity token;

    protected AbstractTokenEvent(Object source, TokenEntity token) {
        super(source);
        this.token = snapshot(token);
    }

    public TokenEntity getToken() {
        return token;
    }

    private TokenEntity snapshot(TokenEntity source) {
        if (source == null) {
            return null;
        }
        try {
            return SerializationUtils.clone(source);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to snapshot token " + source.getUid(), ex);
        }
    }
}
