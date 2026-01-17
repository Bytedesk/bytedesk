/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Session Repository
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.repository;

// import java.time.ZonedDateTime;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import com.bytedesk.core.rbac.user.UserEntity;
// import com.bytedesk.remote.entity.DesktopSessionEntity;
// import com.bytedesk.remote.protocol.DesktopSessionStatusEnum;
// import com.bytedesk.remote.protocol.DesktopModeEnum;

// /**
//  * Desktop Session Repository
//  * Provides database operations for desktop sessions
//  */
// @Repository
// public interface DesktopSessionRepository extends JpaRepository<DesktopSessionEntity, Long>,
//         JpaSpecificationExecutor<DesktopSessionEntity> {

//     /**
//      * Find session by UID
//      */
//     Optional<DesktopSessionEntity> findByUid(String uid);

//     /**
//      * Find active sessions by status
//      */
//     List<DesktopSessionEntity> findByStatus(DesktopSessionStatusEnum status);

//     /**
//      * Find sessions by host device UID
//      */
//     List<DesktopSessionEntity> findByHostDeviceUid(String deviceUid);

//     /**
//      * Find sessions by viewer user
//      */
//     List<DesktopSessionEntity> findByViewerUser(UserEntity user);

//     /**
//      * Find active session by host device and status
//      */
//     @Query("SELECT s FROM DesktopSessionEntity s WHERE s.hostDevice.uid = :deviceUid " +
//            "AND s.status IN ('CONNECTED', 'CONNECTING')")
//     Optional<DesktopSessionEntity> findActiveSessionByHostDevice(@Param("deviceUid") String deviceUid);

//     /**
//      * Find sessions by mode
//      */
//     List<DesktopSessionEntity> findByMode(DesktopModeEnum mode);

//     /**
//      * Find sessions started after date
//      */
//     List<DesktopSessionEntity> findByStartedAtAfter(ZonedDateTime date);

//     /**
//      * Find sessions by host device user
//      */
//     @Query("SELECT s FROM DesktopSessionEntity s WHERE s.hostDevice.user.uid = :userUid " +
//            "ORDER BY s.startedAt DESC")
//     List<DesktopSessionEntity> findByHostDeviceUserUid(@Param("userUid") String userUid);

//     /**
//      * Count active sessions
//      */
//     @Query("SELECT COUNT(s) FROM DesktopSessionEntity s WHERE s.status = :status")
//     long countByStatus(@Param("status") DesktopSessionStatusEnum status);

//     /**
//      * Find sessions needing attention (long duration, errors, etc.)
//      */
//     @Query("SELECT s FROM DesktopSessionEntity s WHERE s.status = 'CONNECTED' " +
//            "AND s.startedAt < :threshold")
//     List<DesktopSessionEntity> findLongRunningSessions(@Param("threshold") ZonedDateTime threshold);
// }
