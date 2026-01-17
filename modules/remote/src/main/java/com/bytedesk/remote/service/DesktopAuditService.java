/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Audit Service
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.service;

// import java.time.ZonedDateTime;
// import java.util.List;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.bytedesk.core.rbac.user.UserEntity;
// import com.bytedesk.core.uid.UidUtils;
// import com.bytedesk.remote.entity.DesktopAuditLogEntity;
// import com.bytedesk.remote.entity.DesktopDeviceEntity;
// import com.bytedesk.remote.entity.DesktopSessionEntity;
// import com.bytedesk.remote.repository.DesktopAuditLogRepository;

// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * Desktop Audit Service
//  * Handles security audit logging for remote desktop sessions
//  */
// @Slf4j
// @Service
// @AllArgsConstructor
// public class DesktopAuditService {

//     private final DesktopAuditLogRepository auditLogRepository;
//     private final UidUtils uidUtils;

//     /**
//      * Log an event
//      */
//     @Transactional
//     public DesktopAuditLogEntity logEvent(DesktopSessionEntity session, String eventType,
//                                          UserEntity user, DesktopDeviceEntity device,
//                                          String details, String ipAddress, boolean sensitive) {
//         DesktopAuditLogEntity auditLog = DesktopAuditLogEntity.builder()
//                 .uid(uidUtils.getUid())
//                 .session(session)
//                 .eventType(eventType)
//                 .user(user)
//                 .device(device)
//                 .details(details)
//                 .ipAddress(ipAddress)
//                 .timestamp(ZonedDateTime.now())
//                 .sensitive(sensitive)
//                 .build();

//         DesktopAuditLogEntity saved = auditLogRepository.save(auditLog);

//         if (sensitive) {
//             log.warn("Sensitive audit event: type={}, session={}, user={}",
//                     eventType, session.getUid(), user != null ? user.getUid() : "N/A");
//         } else {
//             log.debug("Audit event logged: type={}, session={}", eventType, session.getUid());
//         }

//         return saved;
//     }

//     /**
//      * Find audit logs by session
//      */
//     public List<DesktopAuditLogEntity> findBySessionUid(String sessionUid) {
//         return auditLogRepository.findBySessionUid(sessionUid);
//     }

//     /**
//      * Find audit logs by event type
//      */
//     public List<DesktopAuditLogEntity> findByEventType(String eventType) {
//         return auditLogRepository.findByEventType(eventType);
//     }

//     /**
//      * Find sensitive audit logs
//      */
//     public List<DesktopAuditLogEntity> findSensitiveLogs() {
//         return auditLogRepository.findBySensitiveTrue();
//     }

//     /**
//      * Find audit logs by user
//      */
//     public List<DesktopAuditLogEntity> findByUserUid(String userUid) {
//         return auditLogRepository.findByUserUid(userUid);
//     }

//     /**
//      * Find recent audit logs
//      */
//     public List<DesktopAuditLogEntity> findRecentLogs(ZonedDateTime since) {
//         return auditLogRepository.findRecent(since);
//     }

//     /**
//      * Find audit logs within date range
//      */
//     public List<DesktopAuditLogEntity> findByDateRange(ZonedDateTime start, ZonedDateTime end) {
//         return auditLogRepository.findByTimestampBetween(start, end);
//     }

//     /**
//      * Count sensitive events in date range
//      */
//     public long countSensitiveEvents(ZonedDateTime start, ZonedDateTime end) {
//         return auditLogRepository.countSensitiveEventsBetween(start, end);
//     }
// }
