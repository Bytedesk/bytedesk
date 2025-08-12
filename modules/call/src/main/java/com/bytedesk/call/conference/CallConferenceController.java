/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-09 10:54:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.conference;

import com.bytedesk.core.utils.JsonResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Call会议室管理REST API控制器
 */
@Slf4j
@RestController
@RequestMapping("/freeswitch/api/v1/conferences")
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.call.freeswitch.enabled", havingValue = "true")
public class CallConferenceController {

    private final CallConferenceService conferenceService;

    /**
     * 创建会议室
     */
    @PostMapping
    public ResponseEntity<?> createConference(@RequestBody CallConferenceRequest request) {
        
        try {
            CallConferenceEntity created = conferenceService.createConference(
                    request.getConferenceName(),
                    request.getDescription(),
                    request.getPassword(),
                    request.getMaxMembers()
            );
            
            CallConferenceResponse response = CallConferenceResponse.fromEntity(created);
            return ResponseEntity.ok(JsonResult.success("会议室创建成功", response));
        } catch (Exception e) {
            log.error("创建会议室失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }

    /**
     * 分页查询会议室列表
     */
    @GetMapping
    public ResponseEntity<?> listConferences(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String keyword) {
        
        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<CallConferenceEntity> entities;
            if (keyword != null && !keyword.trim().isEmpty()) {
                entities = conferenceService.findByConferenceNameContaining(keyword.trim(), pageable);
            } else {
                entities = conferenceService.findAll(pageable);
            }
            
            Page<CallConferenceResponse> responses = entities.map(CallConferenceResponse::fromEntity);
            return ResponseEntity.ok(JsonResult.success("查询成功", responses));
        } catch (Exception e) {
            log.error("查询会议室列表失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }

    /**
     * 根据ID获取会议室详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getConference(@PathVariable Long id) {
        try {
            Optional<CallConferenceEntity> entity = conferenceService.findById(id);
            if (entity.isPresent()) {
                CallConferenceResponse response = CallConferenceResponse.fromEntity(entity.get());
                return ResponseEntity.ok(JsonResult.success("查询成功", response));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取会议室详情失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }

    /**
     * 根据名称获取会议室详情
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getConferenceByName(@PathVariable String name) {
        try {
            Optional<CallConferenceEntity> entity = conferenceService.findByConferenceName(name);
            if (entity.isPresent()) {
                CallConferenceResponse response = CallConferenceResponse.fromEntity(entity.get());
                return ResponseEntity.ok(JsonResult.success("查询成功", response));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("根据名称获取会议室详情失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }

    /**
     * 更新会议室信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateConference(
            @PathVariable Long id,
            @RequestBody CallConferenceRequest request) {
        
        try {
            CallConferenceEntity updated = conferenceService.updateConference(
                    id,
                    request.getConferenceName(),
                    request.getDescription(),
                    request.getPassword(),
                    request.getMaxMembers(),
                    request.getRecordEnabled()
            );
            
            CallConferenceResponse response = CallConferenceResponse.fromEntity(updated);
            return ResponseEntity.ok(JsonResult.success("会议室更新成功", response));
        } catch (Exception e) {
            log.error("更新会议室失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }

    /**
     * 删除会议室
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConference(@PathVariable Long id) {
        try {
            conferenceService.deleteById(id);
            return ResponseEntity.ok(JsonResult.success("会议室删除成功", "会议室删除成功"));
        } catch (Exception e) {
            log.error("删除会议室失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }

    /**
     * 切换会议室状态
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleConferenceStatus(@PathVariable Long id) {
        try {
            conferenceService.toggleStatus(id);
            return ResponseEntity.ok(JsonResult.success("会议室状态切换成功", "会议室状态切换成功"));
        } catch (Exception e) {
            log.error("切换会议室状态失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }

    /**
     * 验证会议室访问权限
     */
    @PostMapping("/{name}/validate-access")
    public ResponseEntity<JsonResult<Boolean>> validateAccess(
            @PathVariable String name,
            @RequestParam String pin) {
        
        try {
            boolean hasAccess = conferenceService.validateAccess(name, pin);
            return ResponseEntity.ok(JsonResult.success(hasAccess ? "访问验证成功" : "访问验证失败", hasAccess));
        } catch (Exception e) {
            log.error("验证会议室访问权限失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取所有启用的会议室
     */
    @GetMapping("/enabled")
    public ResponseEntity<?> getEnabledConferences() {
        try {
            List<CallConferenceEntity> entities = conferenceService.findByEnabled(true);
            List<CallConferenceResponse> responses = entities.stream()
                    .map(CallConferenceResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(JsonResult.success("查询成功", responses));
        } catch (Exception e) {
            log.error("获取启用会议室列表失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }

    /**
     * 根据最大成员数查询会议室
     */
    @GetMapping("/by-capacity")
    public ResponseEntity<?> getConferencesByCapacity(
            @RequestParam Integer minCapacity) {
        
        try {
            List<CallConferenceEntity> entities = conferenceService.findByMaxMembersGreaterThanEqual(minCapacity);
            List<CallConferenceResponse> responses = entities.stream()
                    .map(CallConferenceResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(JsonResult.success("查询成功", responses));
        } catch (Exception e) {
            log.error("根据容量查询会议室失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }
}