package com.bytedesk.call.xmlcurl.enums;


import lombok.Getter;

/**
 * @author danmo
 */
@Getter
public enum SipGatewayParamEnum {
    /**
     * account username (required)
     */
    USERNAME("username", "account username (required)"),

    /**
     * auth realm (same as gateway name, if blank)
     */
    REALM("realm", "auth realm (same as gateway name, if blank)"),

    /**
     * username to use in from (same as username, if blank)
     */
    FROM_USER("from-user", "username to use in from (same as username, if blank)"),

    /**
     * domain to use in from (same as realm, if blank)
     */
    FROM_DOMAIN("from-domain", "domain to use in from (same as realm, if blank)"),

    /**
     * account password (required)
     */
    PASSWORD("password", "account password (required)"),

    /**
     * extension for inbound calls (same as username, if blank)
     */
    EXTENSION("extension", "extension for inbound calls (same as username, if blank)"),

    /**
     * proxy host (same as realm, if blank)
     */
    PROXY("proxy", "proxy host (same as realm, if blank)"),

    /**
     * send register to this proxy (same as proxy, if blank)
     */
    REGISTER_PROXY("register-proxy", "send register to this proxy (same as proxy, if blank)"),

    /**
     * expire in seconds (3600, if blank)
     */
    EXPIRE_SECONDS("expire-seconds", "expire in seconds (3600, if blank)"),

    /**
     * true or false (like when you don't want to register but want to auth to a gateway)
     */
    REGISTER("register", "true or false (like when you don't want to register but want to auth to a gateway)"),

    /**
     * which transport to use for register
     */
    REGISTER_TRANSPORT("register-transport", "which transport to use for register"),

    /**
     * how many seconds before a retry when a failure or timeout occurs
     */
    RETRY_SECONDS("retry-seconds", "how many seconds before a retry when a failure or timeout occurs"),

    /**
     * use the callerid of an inbound call in the from field on outbound calls via this gateway
     */
    CALLER_ID_IN_FROM("caller-id-in-from", "use the callerid of an inbound call in the from field on outbound calls via this gateway"),

    /**
     * extra sip params to send in the contact
     */
    CONTACT_PARAMS("contact-params", "extra sip params to send in the contact"),

    /**
     * put the extension in the contact
     */
    EXTENSION_IN_CONTACT("extension-in-contact", "put the extension in the contact"),

    /**
     * send an options ping every x seconds, failure will unregister and/or mark it down
     */
    PING("ping", "send an options ping every x seconds, failure will unregister and/or mark it down"),

    /**
     * callerid header mechanism
     */
    CID_TYPE("cid-type", "callerid header mechanism");

    /**
     * 配置项值
     */
    public final String key;

    /**
     * 配置项说明
     */
    public final String msg;

    SipGatewayParamEnum(String key, String msg) {
        this.key = key;
        this.msg = msg;
    }

}
