package com.bytedesk.ai.service;

/**
 * Optional hook for server-side consumers of SSE message payloads.
 *
 * <p>When an {@link org.springframework.web.servlet.mvc.method.annotation.SseEmitter} implements this interface,
 * {@link SseMessageHelper} will forward each serialized {@code MessageProtobuf} JSON before sending it to the client.
 */
public interface SseMessageJsonConsumer {

    /**
     * Receive a serialized {@code MessageProtobuf} JSON payload.
     */
    void acceptMessageJson(String messageJson);
}
