/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 19:52:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.cdr;

import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.JsonResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * FreeSwitch通话详单REST API控制器
 */
@Slf4j
@RestController
@RequestMapping("/freeswitch/api/v1/cdr")
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true")
public class FreeSwitchCdrController {

    private final FreeSwitchCdrService cdrService;

    /**
     * 获取通话详单列表
     */
    @GetMapping
    public ResponseEntity<?> getCdrList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startStamp") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String callerNumber,
            @RequestParam(required = false) String destinationNumber,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @AuthenticationPrincipal UserEntity currentUser) {
        
        try {
            // Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? 
            //         Sort.Direction.ASC : Sort.Direction.DESC;
            // Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            // Page<FreeSwitchCdrEntity> cdrPage = cdrService.findAllCdr(pageable);
            
            return ResponseEntity.ok(JsonResult.success());
        } catch (Exception e) {
            log.error("获取FreeSwitch CDR列表失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据ID获取CDR详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCdrById(@PathVariable Long id) {
        try {
            Optional<FreeSwitchCdrEntity> cdr = cdrService.findById(id);
            if (cdr.isPresent()) {
                return ResponseEntity.ok(JsonResult.success(cdr.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取FreeSwitch CDR详情失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据UUID获取CDR详情
     */
    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<?> getCdrByUuid(@PathVariable String uuid) {
        try {
            Optional<FreeSwitchCdrEntity> cdr = cdrService.findByUuid(uuid);
            if (cdr.isPresent()) {
                return ResponseEntity.ok(JsonResult.success(cdr.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取FreeSwitch CDR详情失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 删除CDR记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCdr(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity currentUser) {
        
        try {
            cdrService.deleteCdr(id);
            return ResponseEntity.ok(JsonResult.success("CDR记录删除成功"));
        } catch (Exception e) {
            log.error("删除FreeSwitch CDR记录失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据主叫号码查询通话记录
     */
    @GetMapping("/caller/{callerNumber}")
    public ResponseEntity<?> getCdrByCallerNumber(
            @PathVariable String callerNumber) {
        
        try {
            List<FreeSwitchCdrEntity> cdrList = cdrService.findByCallerNumber(callerNumber);
            return ResponseEntity.ok(JsonResult.success(cdrList));
        } catch (Exception e) {
            log.error("根据主叫号码查询FreeSwitch CDR失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据被叫号码查询通话记录
     */
    @GetMapping("/destination/{destinationNumber}")
    public ResponseEntity<?> getCdrByDestinationNumber(
            @PathVariable String destinationNumber) {
        
        try {
            List<FreeSwitchCdrEntity> cdrList = cdrService.findByDestinationNumber(destinationNumber);
            return ResponseEntity.ok(JsonResult.success(cdrList));
        } catch (Exception e) {
            log.error("根据被叫号码查询FreeSwitch CDR失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取最近通话记录
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentCalls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<FreeSwitchCdrEntity> recentCalls = cdrService.getRecentCalls(pageable);
            return ResponseEntity.ok(JsonResult.success(recentCalls));
        } catch (Exception e) {
            log.error("获取最近FreeSwitch通话记录失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取已应答的通话记录
     */
    @GetMapping("/answered")
    public ResponseEntity<?> getAnsweredCalls() {
        try {
            List<FreeSwitchCdrEntity> answeredCalls = cdrService.getAnsweredCalls();
            return ResponseEntity.ok(JsonResult.success(answeredCalls));
        } catch (Exception e) {
            log.error("获取已应答FreeSwitch通话记录失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取有录音文件的通话记录
     */
    @GetMapping("/recorded")
    public ResponseEntity<?> getRecordedCalls() {
        try {
            List<FreeSwitchCdrEntity> recordedCalls = cdrService.getRecordedCalls();
            return ResponseEntity.ok(JsonResult.success(recordedCalls));
        } catch (Exception e) {
            log.error("获取有录音的FreeSwitch通话记录失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取通话统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getCallStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        try {
            FreeSwitchCdrService.CallStatistics statistics = cdrService.getCallStatistics(startTime, endTime);
            return ResponseEntity.ok(JsonResult.success(statistics));
        } catch (Exception e) {
            log.error("获取FreeSwitch通话统计失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 清理过期的CDR记录
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<?> cleanupOldCdr(
            @RequestParam int daysToKeep,
            @AuthenticationPrincipal UserEntity currentUser) {
        
        try {
            // cdrService.cleanupOldCdr(daysToKeep);
            return ResponseEntity.ok(JsonResult.success("过期CDR记录清理成功"));
        } catch (Exception e) {
            log.error("清理过期FreeSwitch CDR记录失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }
}