/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Audit Log Repository
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.repository;

// import java.time.ZonedDateTime;
// import java.util.List;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import com.bytedesk.remote.entity.DesktopAuditLogEntity;

// /**
//  * Desktop Audit Log Repository
//  * Provides database operations for audit logs
//  */
// @Repository
// public interface DesktopAuditLogRepository extends JpaRepository<DesktopAuditLogEntity, Long>,
//         JpaSpecificationExecutor<DesktopAuditLogEntity> {

//     /**
//      * Find audit logs by session UID
//      */
//     List<DesktopAuditLogEntity> findBySessionUid(String sessionUid);

//     /**
//      * Find audit logs by event type
//      */
//     List<DesktopAuditLogEntity> findByEventType(String eventType);

//     /**
//      * Find sensitive audit logs
//      */
//     List<DesktopAuditLogEntity> findBySensitiveTrue();

//     /**
//      * Find audit logs within date range
//      */
//     @Query("SELECT a FROM DesktopAuditLogEntity a WHERE a.timestamp BETWEEN :start AND :end " +
//            "ORDER BY a.timestamp DESC")
//     List<DesktopAuditLogEntity> findByTimestampBetween(@Param("start") ZonedDateTime start,
//                                                         @Param("end") ZonedDateTime end);

//     /**
//      * Find audit logs by user UID
//      */
//     @Query("SELECT a FROM DesktopAuditLogEntity a WHERE a.user.uid = :userUid " +
//            "ORDER BY a.timestamp DESC")
//     List<DesktopAuditLogEntity> findByUserUid(@Param("userUid") String userUid);

//     /**
//      * Find recent audit logs (last N days)
//      */
//     @Query("SELECT a FROM DesktopAuditLogEntity a WHERE a.timestamp >= :since " +
//            "ORDER BY a.timestamp DESC")
//     List<DesktopAuditLogEntity> findRecent(@Param("since") ZonedDateTime since);

//     /**
//      * Count sensitive events in date range
//      */
//     @Query("SELECT COUNT(a) FROM DesktopAuditLogEntity a WHERE a.sensitive = true " +
//            "AND a.timestamp BETWEEN :start AND :end")
//     long countSensitiveEventsBetween(@Param("start") ZonedDateTime start,
//                                       @Param("end") ZonedDateTime end);
// }
