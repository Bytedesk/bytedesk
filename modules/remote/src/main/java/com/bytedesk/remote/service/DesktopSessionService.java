/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Session Service
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.service;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.bytedesk.core.exception.NotFoundException;
// import com.bytedesk.core.rbac.user.UserEntity;
// import com.bytedesk.core.rbac.user.UserRestService;
// import com.bytedesk.core.uid.UidUtils;
// import com.bytedesk.remote.entity.DesktopDeviceEntity;
// import com.bytedesk.remote.entity.DesktopSessionEntity;
// import com.bytedesk.remote.protocol.DesktopModeEnum;
// import com.bytedesk.remote.protocol.DesktopSessionStatusEnum;
// import com.bytedesk.remote.protocol.QualityLevelEnum;
// import com.bytedesk.remote.repository.DesktopSessionRepository;

// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * Desktop Session Service
//  * Handles remote desktop session lifecycle
//  */
// @Slf4j
// @Service
// @AllArgsConstructor
// public class DesktopSessionService {

//     private final DesktopSessionRepository sessionRepository;
//     private final DesktopDeviceService deviceService;
//     private final DesktopAuditService auditService;
//     private final UserRestService userRestService;
//     private final UidUtils uidUtils;

//     /**
//      * Create a new remote desktop session
//      */
//     @Transactional
//     public DesktopSessionEntity create(String hostDeviceUid, String viewerUserUid,
//                                        DesktopModeEnum mode, QualityLevelEnum quality) {
//         log.info("Creating desktop session: hostDevice={}, viewer={}, mode={}",
//                 hostDeviceUid, viewerUserUid, mode);

//         // Get host device
//         DesktopDeviceEntity hostDevice = deviceService.findByUid(hostDeviceUid);

//         // Check if device is online
//         // if (!hostDevice.isOnline()) {
//         //     throw new IllegalStateException("Device is not online: " + hostDeviceUid);
//         // }

//         // Get viewer user
//         UserEntity viewerUser = userRestService.findByUid(viewerUserUid)
//                 .orElseThrow(() -> new NotFoundException("User not found: " + viewerUserUid));

//         // Check if there's already an active session for this device
//         Optional<DesktopSessionEntity> existingSession = sessionRepository
//                 .findActiveSessionByHostDevice(hostDeviceUid);
//         if (existingSession.isPresent()) {
//             throw new IllegalStateException("Active session already exists for device: " + hostDeviceUid);
//         }

//         // Create session
//         DesktopSessionEntity session = DesktopSessionEntity.builder()
//                 .uid(uidUtils.getUid())
//                 .hostDevice(hostDevice)
//                 .viewerUser(viewerUser)
//                 .mode(mode)
//                 .status(DesktopSessionStatusEnum.INITIALIZED)
//                 .controlGranted(mode == DesktopModeEnum.REMOTE) // Full control for remote access
//                 .qualityLevel(quality != null ? quality : QualityLevelEnum.MEDIUM)
//                 .build();

//         DesktopSessionEntity savedSession = sessionRepository.save(session);

//         // Log session creation
//         // auditService.logEvent(savedSession, "SESSION_CREATED", viewerUser, hostDevice,
//         //         "Session created with mode: " + mode, null, null);

//         log.info("Session created successfully: uid={}", savedSession.getUid());
//         return savedSession;
//     }

//     /**
//      * Connect session
//      */
//     @Transactional
//     public DesktopSessionEntity connect(String sessionUid) {
//         log.info("Connecting session: {}", sessionUid);

//         DesktopSessionEntity session = findByUid(sessionUid);
//         session.setStatus(DesktopSessionStatusEnum.CONNECTING);
//         sessionRepository.save(session);

//         return session;
//     }

//     /**
//      * Confirm session is connected
//      */
//     @Transactional
//     public DesktopSessionEntity confirmConnected(String sessionUid) {
//         log.info("Confirming session connected: {}", sessionUid);

//         DesktopSessionEntity session = findByUid(sessionUid);
//         session.startSession();
//         sessionRepository.save(session);

//         // Log session connection
//         auditService.logEvent(session, "SESSION_CONNECTED", session.getViewerUser(),
//                 session.getHostDevice(), null, null, false);

//         return session;
//     }

//     /**
//      * Terminate session
//      */
//     @Transactional
//     public void terminate(String sessionUid, String reason) {
//         log.info("Terminating session: {}, reason: {}", sessionUid, reason);

//         DesktopSessionEntity session = findByUid(sessionUid);
//         session.endSession(reason);
//         sessionRepository.save(session);

//         // Log session termination
//         auditService.logEvent(session, "SESSION_TERMINATED", session.getViewerUser(),
//                 session.getHostDevice(), "Reason: " + reason, null, true);
//     }

//     /**
//      * Grant control permission
//      */
//     @Transactional
//     public void grantControl(String sessionUid) {
//         log.info("Granting control for session: {}", sessionUid);

//         DesktopSessionEntity session = findByUid(sessionUid);
//         session.grantControl();
//         sessionRepository.save(session);

//         // Log control grant
//         auditService.logEvent(session, "CONTROL_GRANTED", session.getViewerUser(),
//                 session.getHostDevice(), null, null, true);
//     }

//     /**
//      * Revoke control permission
//      */
//     @Transactional
//     public void revokeControl(String sessionUid) {
//         log.info("Revoking control for session: {}", sessionUid);

//         DesktopSessionEntity session = findByUid(sessionUid);
//         session.revokeControl();
//         sessionRepository.save(session);

//         // Log control revoke
//         auditService.logEvent(session, "CONTROL_REVOKED", session.getViewerUser(),
//                 session.getHostDevice(), null, null, true);
//     }

//     /**
//      * Adjust quality level
//      */
//     @Transactional
//     public void adjustQuality(String sessionUid, QualityLevelEnum quality) {
//         log.info("Adjusting quality for session: {}, quality: {}", sessionUid, quality);

//         DesktopSessionEntity session = findByUid(sessionUid);
//         session.adjustQuality(quality);
//         sessionRepository.save(session);

//         // Log quality change
//         auditService.logEvent(session, "QUALITY_CHANGED", session.getViewerUser(),
//                 session.getHostDevice(), "Quality: " + quality.getCode(), null, false);
//     }

//     /**
//      * Update session statistics
//      */
//     @Transactional
//     public void updateStats(String sessionUid, Long bytes, Long frames, Integer latency) {
//         DesktopSessionEntity session = findByUid(sessionUid);
//         session.updateStats(bytes, frames, latency);
//         sessionRepository.save(session);
//     }

//     /**
//      * Find session by UID
//      */
//     public DesktopSessionEntity findByUid(String sessionUid) {
//         return sessionRepository.findByUid(sessionUid)
//                 .orElseThrow(() -> new NotFoundException("Session not found: " + sessionUid));
//     }

//     /**
//      * Find active sessions
//      */
//     public List<DesktopSessionEntity> findActiveSessions() {
//         return sessionRepository.findByStatus(DesktopSessionStatusEnum.CONNECTED);
//     }

//     /**
//      * Find sessions by viewer
//      */
//     // public List<DesktopSessionEntity> findByViewerUserUid(String viewerUserUid) {
//     //     return sessionRepository.findByViewerUserUid(viewerUserUid);
//     // }

//     /**
//      * Find sessions by host device
//      */
//     public List<DesktopSessionEntity> findByHostDeviceUid(String deviceUid) {
//         return sessionRepository.findByHostDeviceUid(deviceUid);
//     }

//     /**
//      * Find sessions by mode
//      */
//     public List<DesktopSessionEntity> findByMode(DesktopModeEnum mode) {
//         return sessionRepository.findByMode(mode);
//     }

//     /**
//      * Count active sessions
//      */
//     public long countActiveSessions() {
//         return sessionRepository.countByStatus(DesktopSessionStatusEnum.CONNECTED);
//     }
// }
