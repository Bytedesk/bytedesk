/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop WebSocket Controller
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.websocket;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.bytedesk.remote.protocol.DesktopMessageProto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Desktop WebSocket Controller
 * Handles real-time remote desktop communication via STOMP/WebSocket
 *
 * WebSocket Topics:
 * - /app/desktop/connect - Initiate desktop connection
 * - /app/desktop/{sessionId}/screen - Send screen frame data
 * - /app/desktop/{sessionId}/input - Send input events
 * - /topic/desktop/{sessionId}/screen - Subscribe to screen data
 * - /topic/desktop/{sessionId}/control - Subscribe to control events
 */
@Slf4j
@Controller
@AllArgsConstructor
public class DesktopWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle desktop connection initiation
     * Client sends: /app/desktop/connect
     *
     * @param principal Authenticated user
     * @param message DesktopMessageProto JSON
     */
    @MessageMapping("/desktop/connect")
    public void handleConnect(Principal principal, String message) {
        log.info("Desktop connection request from user: {}, message: {}",
                principal != null ? principal.getName() : "anonymous", message);

        DesktopMessageProto proto = DesktopMessageProto.fromJson(message);

        // Process connection request
        // TODO: Validate session, handle authentication, send response

        // Send response back to client
        messagingTemplate.convertAndSend("/topic/desktop/" + proto.getSessionId() + "/connected", message);
    }

    /**
     * Handle screen frame data from host
     * Client sends: /app/desktop/{sessionId}/screen
     * Viewers subscribe: /topic/desktop/{sessionId}/screen
     *
     * @param sessionId Session identifier
     * @param message Base64 encoded screen frame
     */
    @MessageMapping("/desktop/{sessionId}/screen")
    @SendTo("/topic/desktop/{sessionId}/screen")
    public String handleScreenFrame(@DestinationVariable String sessionId, String message) {
        log.debug("Screen frame received for session: {}, size: {} bytes",
                sessionId, message.length());

        DesktopMessageProto proto = DesktopMessageProto.fromJson(message);

        // Forward screen frame to all viewers
        // The @SendTo annotation automatically routes to the topic

        return proto.toJson();
    }

    /**
     * Handle input events from viewer
     * Client sends: /app/desktop/{sessionId}/input
     * Host subscribes: /topic/desktop/{sessionId}/input
     *
     * @param sessionId Session identifier
     * @param message Input event (mouse/keyboard)
     */
    @MessageMapping("/desktop/{sessionId}/input")
    @SendTo("/topic/desktop/{sessionId}/input")
    public String handleInputEvent(@DestinationVariable String sessionId, String message) {
        log.debug("Input event received for session: {}", sessionId);

        DesktopMessageProto proto = DesktopMessageProto.fromJson(message);

        // Forward input event to host
        // The @SendTo annotation automatically routes to the topic

        return proto.toJson();
    }

    /**
     * Handle control permission request
     * Client sends: /app/desktop/{sessionId}/control/request
     *
     * @param sessionId Session identifier
     * @param message Control request message
     */
    @MessageMapping("/desktop/{sessionId}/control/request")
    public void handleControlRequest(@DestinationVariable String sessionId, String message) {
        log.info("Control request for session: {}", sessionId);

        // DesktopMessageProto proto = DesktopMessageProto.fromJson(message);

        // TODO: Process control request
        // Send to host for approval
        messagingTemplate.convertAndSend("/topic/desktop/" + sessionId + "/control/request", message);
    }

    /**
     * Handle control permission response
     * Client sends: /app/desktop/{sessionId}/control/response
     *
     * @param sessionId Session identifier
     * @param message Control response message (granted/denied)
     */
    @MessageMapping("/desktop/{sessionId}/control/response")
    @SendTo("/topic/desktop/{sessionId}/control/response")
    public String handleControlResponse(@DestinationVariable String sessionId, String message) {
        log.info("Control response for session: {}", sessionId);

        DesktopMessageProto proto = DesktopMessageProto.fromJson(message);

        // Forward control response to viewer
        return proto.toJson();
    }

    /**
     * Handle quality adjustment
     * Client sends: /app/desktop/{sessionId}/quality
     *
     * @param sessionId Session identifier
     * @param message Quality adjustment message
     */
    @MessageMapping("/desktop/{sessionId}/quality")
    @SendTo("/topic/desktop/{sessionId}/quality")
    public String handleQualityAdjust(@DestinationVariable String sessionId, String message) {
        log.info("Quality adjustment for session: {}", sessionId);

        DesktopMessageProto proto = DesktopMessageProto.fromJson(message);

        // Forward quality adjustment to both host and viewer
        return proto.toJson();
    }

    /**
     * Handle session heartbeat
     * Client sends: /app/desktop/{sessionId}/heartbeat
     *
     * @param sessionId Session identifier
     * @param message Heartbeat message
     */
    @MessageMapping("/desktop/{sessionId}/heartbeat")
    public void handleHeartbeat(@DestinationVariable String sessionId, String message) {
        log.trace("Heartbeat for session: {}", sessionId);

        // Update session last activity timestamp
        // TODO: Call session service to update heartbeat
    }

    /**
     * Handle session termination
     * Client sends: /app/desktop/{sessionId}/terminate
     *
     * @param sessionId Session identifier
     * @param message Termination message
     */
    @MessageMapping("/desktop/{sessionId}/terminate")
    @SendTo("/topic/desktop/{sessionId}/terminated")
    public String handleTerminate(@DestinationVariable String sessionId, String message) {
        log.info("Session termination requested: {}", sessionId);

        DesktopMessageProto proto = DesktopMessageProto.fromJson(message);

        // TODO: Clean up session, notify all parties
        // Forward termination notification
        return proto.toJson();
    }

    /**
     * Handle clipboard synchronization
     * Client sends: /app/desktop/{sessionId}/clipboard
     *
     * @param sessionId Session identifier
     * @param message Clipboard data
     */
    @MessageMapping("/desktop/{sessionId}/clipboard")
    @SendTo("/topic/desktop/{sessionId}/clipboard")
    public String handleClipboard(@DestinationVariable String sessionId, String message) {
        log.debug("Clipboard sync for session: {}", sessionId);

        DesktopMessageProto proto = DesktopMessageProto.fromJson(message);

        // Forward clipboard data
        return proto.toJson();
    }
}
