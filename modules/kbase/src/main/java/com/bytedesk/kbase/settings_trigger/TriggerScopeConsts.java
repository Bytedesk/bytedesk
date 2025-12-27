package com.bytedesk.kbase.settings_trigger;

/**
 * Trigger scope for AI assistants.
 */
public class TriggerScopeConsts {

    private TriggerScopeConsts() {
    }

    /**
     * Only visitor messages will trigger.
     */
    public static final String VISITOR_ONLY = "VISITOR_ONLY";

    /**
     * Only agent messages will trigger.
     */
    public static final String AGENT_ONLY = "AGENT_ONLY";

    /**
     * Any non-system message will trigger.
     */
    public static final String ALL = "ALL";
}
