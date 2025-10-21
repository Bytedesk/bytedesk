package com.bytedesk.call.xml_curl.xml.param;

import lombok.Getter;

@Getter
public enum ParamEnum {
    /**
     * domain
     */
    DIAL_STRING("dial-string", ""),

    JSONRPC_ALLOWED_METHODS("jsonrpc-allowed-methods", ""),

    JSONRPC_ALLOWED_EVENT_CHANNELS("jsonrpc-allowed-event-channels", ""),

    /**
     * user
     */
    PASSWORD("password", "SIP password"),

    VM_PASSWORD("vm-password", ""),

    A1_HASH("a1-hash", ""),

    // Some endpoints require authentication for certain types of requests (ex. SIP NOTIFY for resync). You can specify the credentials used for this digest authentication.
    REVERSE_AUTH_USER("reverse-auth-user", ""),

    REVERSE_AUTH_PASS("reverse-auth-pass", ""),

    /**
     * user.gateway
     */
    USERNAME("username", ""),

    REALM("realm", ""),
    //todo
    ;

    /**
     * 配置项值
     */
    public final String key;

    /**
     * 配置项说明
     */
    public final String msg;

    ParamEnum(String key, String msg) {
        this.key = key;
        this.msg = msg;
    }

}
