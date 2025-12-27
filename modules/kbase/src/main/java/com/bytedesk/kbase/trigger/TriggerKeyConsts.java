package com.bytedesk.kbase.trigger;

/**
 * Trigger keys used for identifying runtime trigger behaviors.
 */
public final class TriggerKeyConsts {

    private TriggerKeyConsts() {
    }

    /**
     * Visitor long time no response -> send proactive message.
     */
    public static final String VISITOR_NO_RESPONSE_PROACTIVE_MESSAGE = "visitor_no_response_proactive_message";
}
