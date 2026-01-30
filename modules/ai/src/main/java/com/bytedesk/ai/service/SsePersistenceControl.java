package com.bytedesk.ai.service;

/**
 * Optional flag for controlling whether {@link SseMessageHelper} should persist messages.
 *
 * <p>This is useful for external platform integrations (e.g. Feishu) that need streaming generation
 * but do not want to write to the internal message persistence pipeline.
 */
public interface SsePersistenceControl {

    /**
     * @return true to enable persistence (default behavior), false to disable persistence.
     */
    boolean isPersistenceEnabled();
}
