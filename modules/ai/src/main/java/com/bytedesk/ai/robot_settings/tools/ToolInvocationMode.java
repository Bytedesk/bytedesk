package com.bytedesk.ai.robot_settings.tools;

/**
 * Spring AI tool invocation mode for robot tools settings.
 * <p>
 * Mirrors the frontend {@code invocationMode} select values defined in
 * {@code TabTools.tsx}.
 */
public enum ToolInvocationMode {

    /** Model decides when to invoke tools (default). */
    AUTO,

    /** Prompt user for manual confirmation before each tool invocation. */
    MANUAL_CONFIRM,

    /** Always invoke the tool when triggered (no confirmation). */
    ALWAYS

}
