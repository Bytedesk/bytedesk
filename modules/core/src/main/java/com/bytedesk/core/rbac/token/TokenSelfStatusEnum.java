package com.bytedesk.core.rbac.token;

/**
 * 本人登录会话的展示状态，由服务端按撤销/有效期判断，客户端不得自行推断。
 */
public enum TokenSelfStatusEnum {
    VALID,
    EXPIRED,
    REVOKED,
    INVALID
}
