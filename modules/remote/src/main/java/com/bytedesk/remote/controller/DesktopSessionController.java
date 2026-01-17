/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Session REST Controller
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.controller;

import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Desktop Session REST Controller
 * Provides REST API for session management
 */
@Slf4j
@RestController
@RequestMapping("/api/desktop/sessions")
@AllArgsConstructor
public class DesktopSessionController {

    // private final DesktopSessionService sessionService;

    // /**
    //  * Create a new remote desktop session
    //  * POST /api/desktop/sessions/create
    //  */
    // @PostMapping("/create")
    // public ResponseEntity<BaseResponse> createSession(@RequestBody DesktopSessionCreateRequest request) {
    //     log.info("Create session: hostDevice={}, viewer={}, mode={}",
    //             request.getHostDeviceUid(), request.getViewerUserUid(), request.getMode());

    //     try {
    //         DesktopSessionEntity session = sessionService.create(
    //                 request.getHostDeviceUid(),
    //                 request.getViewerUserUid(),
    //                 request.getMode(),
    //                 request.getQuality()
    //         );

    //         DesktopSessionResponse response = convertToResponse(session);
    //         return ResponseEntity.ok(BaseResponse.success("Session created successfully", response));
    //     } catch (Exception e) {
    //         log.error("Failed to create session: {}", e.getMessage(), e);
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to create session: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Connect to a session
    //  * POST /api/desktop/sessions/{sessionUid}/connect
    //  */
    // @PostMapping("/{sessionUid}/connect")
    // public ResponseEntity<BaseResponse> connectSession(@PathVariable String sessionUid) {
    //     log.info("Connect to session: {}", sessionUid);

    //     try {
    //         DesktopSessionEntity session = sessionService.connect(sessionUid);
    //         DesktopSessionResponse response = convertToResponse(session);
    //         return ResponseEntity.ok(BaseResponse.success("Session connected", response));
    //     } catch (Exception e) {
    //         log.error("Failed to connect session: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to connect session: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Confirm session connected
    //  * POST /api/desktop/sessions/{sessionUid}/confirm
    //  */
    // @PostMapping("/{sessionUid}/confirm")
    // public ResponseEntity<BaseResponse> confirmConnected(@PathVariable String sessionUid) {
    //     log.info("Confirm session connected: {}", sessionUid);

    //     try {
    //         DesktopSessionEntity session = sessionService.confirmConnected(sessionUid);
    //         DesktopSessionResponse response = convertToResponse(session);
    //         return ResponseEntity.ok(BaseResponse.success("Session confirmed connected", response));
    //     } catch (Exception e) {
    //         log.error("Failed to confirm session: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to confirm session: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Terminate a session
    //  * POST /api/desktop/sessions/{sessionUid}/terminate
    //  */
    // @PostMapping("/{sessionUid}/terminate")
    // public ResponseEntity<BaseResponse> terminateSession(
    //         @PathVariable String sessionUid,
    //         @RequestParam(defaultValue = "user_initiated") String reason) {
    //     log.info("Terminate session: {}, reason: {}", sessionUid, reason);

    //     try {
    //         sessionService.terminate(sessionUid, reason);
    //         return ResponseEntity.ok(BaseResponse.success("Session terminated successfully"));
    //     } catch (Exception e) {
    //         log.error("Failed to terminate session: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to terminate session: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Grant control permission
    //  * POST /api/desktop/sessions/{sessionUid}/control/grant
    //  */
    // @PostMapping("/{sessionUid}/control/grant")
    // public ResponseEntity<BaseResponse> grantControl(@PathVariable String sessionUid) {
    //     log.info("Grant control for session: {}", sessionUid);

    //     try {
    //         sessionService.grantControl(sessionUid);
    //         return ResponseEntity.ok(BaseResponse.success("Control granted successfully"));
    //     } catch (Exception e) {
    //         log.error("Failed to grant control: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to grant control: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Revoke control permission
    //  * POST /api/desktop/sessions/{sessionUid}/control/revoke
    //  */
    // @PostMapping("/{sessionUid}/control/revoke")
    // public ResponseEntity<BaseResponse> revokeControl(@PathVariable String sessionUid) {
    //     log.info("Revoke control for session: {}", sessionUid);

    //     try {
    //         sessionService.revokeControl(sessionUid);
    //         return ResponseEntity.ok(BaseResponse.success("Control revoked successfully"));
    //     } catch (Exception e) {
    //         log.error("Failed to revoke control: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to revoke control: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Adjust quality level
    //  * POST /api/desktop/sessions/{sessionUid}/quality
    //  */
    // @PostMapping("/{sessionUid}/quality")
    // public ResponseEntity<BaseResponse> adjustQuality(
    //         @PathVariable String sessionUid,
    //         @RequestParam QualityLevelEnum quality) {
    //     log.info("Adjust quality for session: {}, quality: {}", sessionUid, quality);

    //     try {
    //         sessionService.adjustQuality(sessionUid, quality);
    //         return ResponseEntity.ok(BaseResponse.success("Quality adjusted successfully"));
    //     } catch (Exception e) {
    //         log.error("Failed to adjust quality: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to adjust quality: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Get session by UID
    //  * GET /api/desktop/sessions/{sessionUid}
    //  */
    // @GetMapping("/{sessionUid}")
    // public ResponseEntity<BaseResponse> getSession(@PathVariable String sessionUid) {
    //     log.debug("Get session: {}", sessionUid);

    //     try {
    //         DesktopSessionEntity session = sessionService.findByUid(sessionUid);
    //         DesktopSessionResponse response = convertToResponse(session);
    //         return ResponseEntity.ok(BaseResponse.success("Session found", response));
    //     } catch (Exception e) {
    //         log.error("Failed to get session: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                 .body(BaseResponse.error("Session not found: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Get active sessions
    //  * GET /api/desktop/sessions/active
    //  */
    // @GetMapping("/active")
    // public ResponseEntity<BaseResponse> getActiveSessions() {
    //     log.debug("Get active sessions");

    //     try {
    //         List<DesktopSessionEntity> sessions = sessionService.findActiveSessions();
    //         return ResponseEntity.ok(BaseResponse.success("Active sessions found", sessions));
    //     } catch (Exception e) {
    //         log.error("Failed to get active sessions: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to get active sessions: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Get sessions by viewer
    //  * GET /api/desktop/sessions/viewer/{viewerUserUid}
    //  */
    // @GetMapping("/viewer/{viewerUserUid}")
    // public ResponseEntity<BaseResponse> getSessionsByViewer(@PathVariable String viewerUserUid) {
    //     log.debug("Get sessions for viewer: {}", viewerUserUid);

    //     try {
    //         List<DesktopSessionEntity> sessions = sessionService.findByViewerUserUid(viewerUserUid);
    //         return ResponseEntity.ok(BaseResponse.success("Sessions found", sessions));
    //     } catch (Exception e) {
    //         log.error("Failed to get sessions: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to get sessions: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Get sessions by host device
    //  * GET /api/desktop/sessions/host/{hostDeviceUid}
    //  */
    // @GetMapping("/host/{hostDeviceUid}")
    // public ResponseEntity<BaseResponse> getSessionsByHost(@PathVariable String hostDeviceUid) {
    //     log.debug("Get sessions for host device: {}", hostDeviceUid);

    //     try {
    //         List<DesktopSessionEntity> sessions = sessionService.findByHostDeviceUid(hostDeviceUid);
    //         return ResponseEntity.ok(BaseResponse.success("Sessions found", sessions));
    //     } catch (Exception e) {
    //         log.error("Failed to get sessions: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to get sessions: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Get sessions by mode
    //  * GET /api/desktop/sessions/mode/{mode}
    //  */
    // @GetMapping("/mode/{mode}")
    // public ResponseEntity<BaseResponse> getSessionsByMode(@PathVariable DesktopModeEnum mode) {
    //     log.debug("Get sessions for mode: {}", mode);

    //     try {
    //         List<DesktopSessionEntity> sessions = sessionService.findByMode(mode);
    //         return ResponseEntity.ok(BaseResponse.success("Sessions found", sessions));
    //     } catch (Exception e) {
    //         log.error("Failed to get sessions: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to get sessions: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Count active sessions
    //  * GET /api/desktop/sessions/count/active
    //  */
    // @GetMapping("/count/active")
    // public ResponseEntity<BaseResponse> countActiveSessions() {
    //     log.debug("Count active sessions");

    //     try {
    //         long count = sessionService.countActiveSessions();
    //         return ResponseEntity.ok(BaseResponse.success("Active session count", count));
    //     } catch (Exception e) {
    //         log.error("Failed to count sessions: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to count sessions: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Convert entity to response DTO
    //  */
    // private DesktopSessionResponse convertToResponse(DesktopSessionEntity session) {
    //     return DesktopSessionResponse.builder()
    //             .uid(session.getUid())
    //             .hostDeviceUid(session.getHostDevice().getUid())
    //             .hostDeviceName(session.getHostDevice().getDeviceName())
    //             .viewerUserUid(session.getViewerUser().getUid())
    //             .viewerUserName(session.getViewerUser().getNickname())
    //             .mode(session.getMode())
    //             .status(session.getStatus())
    //             .controlGranted(session.getControlGranted())
    //             .qualityLevel(session.getQualityLevel())
    //             .resolution(session.getResolution())
    //             .frameRate(session.getFrameRate())
    //             .startedAt(session.getStartedAt())
    //             .endedAt(session.getEndedAt())
    //             .durationSeconds(session.getDurationSeconds())
    //             .averageLatencyMs(session.getAverageLatencyMs())
    //             .build();
    // }
}
