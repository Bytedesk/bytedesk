package com.bytedesk.kbase.settings_trigger;

/**
 * Execution timing constants for AI-related settings.
 */
public final class ExecutionTimingConsts {

    private ExecutionTimingConsts() {
    }

    /**
     * Trigger on each new message.
     */
    public static final String ON_MESSAGE = "ON_MESSAGE";

    /**
     * Trigger when thread/session is closed.
     */
    public static final String THREAD_END = "THREAD_END";
}
