/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Device Repository
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.repository;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import com.bytedesk.core.rbac.user.UserEntity;
// import com.bytedesk.remote.entity.DesktopDeviceEntity;

// /**
//  * Desktop Device Repository
//  * Provides database operations for desktop devices
//  */
// @Repository
// public interface DesktopDeviceRepository extends JpaRepository<DesktopDeviceEntity, Long>,
//         JpaSpecificationExecutor<DesktopDeviceEntity> {

//     /**
//      * Find device by UID
//      */
//     Optional<DesktopDeviceEntity> findByUid(String uid);

//     /**
//      * Find devices by user
//      */
//     List<DesktopDeviceEntity> findByUser(UserEntity user);

//     /**
//      * Find devices by user UID
//      */
//     List<DesktopDeviceEntity> findByUserUid(String userUid);

//     /**
//      * Find online devices by user
//      */
//     @Query("SELECT d FROM DesktopDeviceEntity d WHERE d.user.uid = :userUid AND d.online = true")
//     List<DesktopDeviceEntity> findOnlineDevicesByUserUid(@Param("userUid") String userUid);

//     /**
//      * Find device by name and user
//      */
//     Optional<DesktopDeviceEntity> findByDeviceNameAndUserUid(String deviceName, String userUid);

//     /**
//      * Count devices by user
//      */
//     long countByUserUid(String userUid);

//     /**
//      * Find devices by OS type
//      */
//     List<DesktopDeviceEntity> findByOsType(String osType);

//     /**
//      * Find devices with name containing keyword
//      */
//     List<DesktopDeviceEntity> findByDeviceNameContainingIgnoreCase(String keyword);
// }
