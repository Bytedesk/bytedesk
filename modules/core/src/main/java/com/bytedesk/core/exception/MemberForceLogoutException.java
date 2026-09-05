package com.bytedesk.core.exception;

import lombok.Getter;

/**
 * 成员被管理员禁用（forceLogout）后仍尝试登录时抛出。
 * 覆盖所有登录入口（账密/手机号/邮箱/accessToken 换取），确保禁用后无法登录任何前端。
 */
@Getter
public class MemberForceLogoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String userUid;
    private final String orgUid;

    public MemberForceLogoutException(String userUid, String orgUid, String message) {
        super(message);
        this.userUid = userUid;
        this.orgUid = orgUid;
    }
}
