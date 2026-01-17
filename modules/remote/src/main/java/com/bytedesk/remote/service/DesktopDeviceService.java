/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Device Service
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.service;

// import java.time.ZonedDateTime;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.bytedesk.core.exception.NotFoundException;
// import com.bytedesk.core.rbac.user.UserEntity;
// import com.bytedesk.core.rbac.user.UserRestService;
// import com.bytedesk.core.uid.UidUtils;
// import com.bytedesk.remote.entity.DesktopDeviceEntity;
// import com.bytedesk.remote.repository.DesktopDeviceRepository;

// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * Desktop Device Service
//  * Handles device registration, heartbeat, and management
//  */
// @Slf4j
// @Service
// @AllArgsConstructor
// public class DesktopDeviceService {

//     private final DesktopDeviceRepository deviceRepository;
//     private final UserRestService userRestService;
//     private final UidUtils uidUtils;

//     /**
//      * Register a new device
//      */
//     @Transactional
//     public DesktopDeviceEntity register(String deviceName, String osType, String osVersion,
//                                        String capabilities, String userUid, String ipAddress) {
//         log.info("Registering device: deviceName={}, userUid={}", deviceName, userUid);

//         // Get user
//         UserEntity user = userRestService.findByUid(userUid)
//                 .orElseThrow(() -> new NotFoundException("User not found: " + userUid));

//         // Check if device with same name exists for user
//         Optional<DesktopDeviceEntity> existingDevice = deviceRepository
//                 .findByDeviceNameAndUserUid(deviceName, userUid);

//         if (existingDevice.isPresent()) {
//             // Update existing device
//             DesktopDeviceEntity device = existingDevice.get();
//             device.setOsType(osType);
//             device.setOsVersion(osVersion);
//             device.setCapabilities(capabilities);
//             device.setIpAddress(ipAddress);
//             device.updateLastOnline();
//             return deviceRepository.save(device);
//         }

//         // Create new device
//         DesktopDeviceEntity device = DesktopDeviceEntity.builder()
//                 .uid(uidUtils.getUid())
//                 .deviceName(deviceName)
//                 .osType(osType)
//                 .osVersion(osVersion)
//                 .capabilities(capabilities)
//                 .ipAddress(ipAddress)
//                 .online(true)
//                 .lastOnline(ZonedDateTime.now())
//                 .user(user)
//                 .build();

//         DesktopDeviceEntity savedDevice = deviceRepository.save(device);
//         log.info("Device registered successfully: uid={}", savedDevice.getUid());
//         return savedDevice;
//     }

//     /**
//      * Update device heartbeat
//      */
//     @Transactional
//     public void updateHeartbeat(String deviceUid, String ipAddress) {
//         log.debug("Updating heartbeat for device: {}", deviceUid);

//         DesktopDeviceEntity device = findByUid(deviceUid);
//         device.updateLastOnline();
//         if (ipAddress != null) {
//             device.setIpAddress(ipAddress);
//         }
//         deviceRepository.save(device);
//     }

//     /**
//      * Mark device as offline
//      */
//     @Transactional
//     public void markOffline(String deviceUid) {
//         log.info("Marking device as offline: {}", deviceUid);

//         DesktopDeviceEntity device = findByUid(deviceUid);
//         device.markOffline();
//         deviceRepository.save(device);
//     }

//     /**
//      * Find device by UID
//      */
//     public DesktopDeviceEntity findByUid(String deviceUid) {
//         return deviceRepository.findByUid(deviceUid)
//                 .orElseThrow(() -> new NotFoundException("Device not found: " + deviceUid));
//     }

//     /**
//      * Find devices by user
//      */
//     public List<DesktopDeviceEntity> findByUserUid(String userUid) {
//         return deviceRepository.findByUserUid(userUid);
//     }

//     /**
//      * Find online devices by user
//      */
//     public List<DesktopDeviceEntity> findOnlineDevicesByUserUid(String userUid) {
//         return deviceRepository.findOnlineDevicesByUserUid(userUid);
//     }

//     /**
//      * Delete device
//      */
//     @Transactional
//     public void delete(String deviceUid) {
//         log.info("Deleting device: {}", deviceUid);

//         DesktopDeviceEntity device = findByUid(deviceUid);
//         deviceRepository.delete(device);
//     }

//     /**
//      * Count devices by user
//      */
//     public long countByUserUid(String userUid) {
//         return deviceRepository.countByUserUid(userUid);
//     }

//     /**
//      * Update device capabilities
//      */
//     @Transactional
//     public DesktopDeviceEntity updateCapabilities(String deviceUid, String capabilities) {
//         DesktopDeviceEntity device = findByUid(deviceUid);
//         device.setCapabilities(capabilities);
//         return deviceRepository.save(device);
//     }
// }
