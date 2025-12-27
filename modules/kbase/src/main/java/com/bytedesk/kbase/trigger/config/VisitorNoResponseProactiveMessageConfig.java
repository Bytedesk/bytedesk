package com.bytedesk.kbase.trigger.config;

/**
 * Config schema for triggerKey: visitor_no_response_proactive_message
 */
@SuppressWarnings("unused")
public class VisitorNoResponseProactiveMessageConfig {

    public static final int DEFAULT_NO_RESPONSE_TIMEOUT = 300;

    public static final String DEFAULT_PROACTIVE_MESSAGE = "您好，看起来您有一段时间没有互动了。请问还需要帮助吗？";

    /** seconds */
    public Integer noResponseTimeout;

    /** message content */
    public String proactiveMessage;

    public static VisitorNoResponseProactiveMessageConfig defaults() {
        VisitorNoResponseProactiveMessageConfig config = new VisitorNoResponseProactiveMessageConfig();
        config.noResponseTimeout = DEFAULT_NO_RESPONSE_TIMEOUT;
        config.proactiveMessage = DEFAULT_PROACTIVE_MESSAGE;
        return config;
    }
}
