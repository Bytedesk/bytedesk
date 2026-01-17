/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Device REST Controller
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.controller;

import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Desktop Device REST Controller
 * Provides REST API for device management
 */
@Slf4j
@RestController
@RequestMapping("/api/desktop/devices")
@AllArgsConstructor
public class DesktopDeviceController {

    // private final DesktopDeviceService deviceService;

    // /**
    //  * Register a new device
    //  * POST /api/desktop/devices/register
    //  */
    // @PostMapping("/register")
    // public ResponseEntity<BaseResponse> register(@RequestBody DesktopDeviceRegisterRequest request) {
    //     log.info("Register device: deviceName={}, userUid={}",
    //             request.getDeviceName(), request.getUserUid());

    //     // try {
    //     //     DesktopDeviceEntity device = deviceService.register(
    //     //             request.getDeviceName(),
    //     //             request.getOsType(),
    //     //             request.getOsVersion(),
    //     //             request.getCapabilities(),
    //     //             request.getUserUid(),
    //     //             request.getIpAddress()
    //     //     );

    //     //     return ResponseEntity.ok(BaseResponse.success("Device registered successfully", device));
    //     // } catch (Exception e) {
    //     //     log.error("Failed to register device: {}", e.getMessage(), e);
    //     //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //     //             .body(BaseResponse.error("Failed to register device: " + e.getMessage()));
    //     // }
    // }

    // /**
    //  * Update device heartbeat
    //  * POST /api/desktop/devices/{deviceUid}/heartbeat
    //  */
    // @PostMapping("/{deviceUid}/heartbeat")
    // public ResponseEntity<BaseResponse> updateHeartbeat(
    //         @PathVariable String deviceUid,
    //         @RequestBody(required = false) String ipAddress) {
    //     log.debug("Update heartbeat for device: {}", deviceUid);

    //     try {
    //         deviceService.updateHeartbeat(deviceUid, ipAddress);
    //         return ResponseEntity.ok(BaseResponse.success("Heartbeat updated"));
    //     } catch (Exception e) {
    //         log.error("Failed to update heartbeat: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to update heartbeat: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Get device by UID
    //  * GET /api/desktop/devices/{deviceUid}
    //  */
    // @GetMapping("/{deviceUid}")
    // public ResponseEntity<BaseResponse> getDevice(@PathVariable String deviceUid) {
    //     log.debug("Get device: {}", deviceUid);

    //     try {
    //         DesktopDeviceEntity device = deviceService.findByUid(deviceUid);
    //         return ResponseEntity.ok(BaseResponse.success("Device found", device));
    //     } catch (Exception e) {
    //         log.error("Failed to get device: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                 .body(BaseResponse.error("Device not found: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Get devices by user
    //  * GET /api/desktop/devices/user/{userUid}
    //  */
    // @GetMapping("/user/{userUid}")
    // public ResponseEntity<BaseResponse> getDevicesByUser(@PathVariable String userUid) {
    //     log.debug("Get devices for user: {}", userUid);

    //     try {
    //         List<DesktopDeviceEntity> devices = deviceService.findByUserUid(userUid);
    //         return ResponseEntity.ok(BaseResponse.success("Devices found", devices));
    //     } catch (Exception e) {
    //         log.error("Failed to get devices: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to get devices: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Get online devices by user
    //  * GET /api/desktop/devices/user/{userUid}/online
    //  */
    // @GetMapping("/user/{userUid}/online")
    // public ResponseEntity<BaseResponse> getOnlineDevicesByUser(@PathVariable String userUid) {
    //     log.debug("Get online devices for user: {}", userUid);

    //     try {
    //         List<DesktopDeviceEntity> devices = deviceService.findOnlineDevicesByUserUid(userUid);
    //         return ResponseEntity.ok(BaseResponse.success("Online devices found", devices));
    //     } catch (Exception e) {
    //         log.error("Failed to get online devices: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to get online devices: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Delete device
    //  * DELETE /api/desktop/devices/{deviceUid}
    //  */
    // @DeleteMapping("/{deviceUid}")
    // public ResponseEntity<BaseResponse> deleteDevice(@PathVariable String deviceUid) {
    //     log.info("Delete device: {}", deviceUid);

    //     try {
    //         deviceService.delete(deviceUid);
    //         return ResponseEntity.ok(BaseResponse.success("Device deleted successfully"));
    //     } catch (Exception e) {
    //         log.error("Failed to delete device: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to delete device: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Update device capabilities
    //  * PUT /api/desktop/devices/{deviceUid}/capabilities
    //  */
    // @PutMapping("/{deviceUid}/capabilities")
    // public ResponseEntity<BaseResponse> updateCapabilities(
    //         @PathVariable String deviceUid,
    //         @RequestBody String capabilities) {
    //     log.info("Update capabilities for device: {}", deviceUid);

    //     try {
    //         DesktopDeviceEntity device = deviceService.updateCapabilities(deviceUid, capabilities);
    //         return ResponseEntity.ok(BaseResponse.success("Capabilities updated", device));
    //     } catch (Exception e) {
    //         log.error("Failed to update capabilities: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to update capabilities: " + e.getMessage()));
    //     }
    // }

    // /**
    //  * Count devices by user
    //  * GET /api/desktop/devices/user/{userUid}/count
    //  */
    // @GetMapping("/user/{userUid}/count")
    // public ResponseEntity<BaseResponse> countDevicesByUser(@PathVariable String userUid) {
    //     log.debug("Count devices for user: {}", userUid);

    //     try {
    //         long count = deviceService.countByUserUid(userUid);
    //         return ResponseEntity.ok(BaseResponse.success("Device count", count));
    //     } catch (Exception e) {
    //         log.error("Failed to count devices: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(BaseResponse.error("Failed to count devices: " + e.getMessage()));
    //     }
    // }
}
