/*
 * @Author: jackning 270580156@qq.com
 * @Description: Event published when a token is created
 */
package com.bytedesk.core.rbac.token.event;

import com.bytedesk.core.rbac.token.TokenEntity;

public class TokenCreateEvent extends AbstractTokenEvent {

    private static final long serialVersionUID = 1L;

    public TokenCreateEvent(TokenEntity token) {
        super(token, token);
    }
}
