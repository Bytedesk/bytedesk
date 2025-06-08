package com.bytedesk.freeswitch.controller;

import com.bytedesk.core.rbac.annotation.CurrentUser;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.freeswitch.dto.*;
import com.bytedesk.freeswitch.model.FreeSwitchConferenceEntity;
import com.bytedesk.freeswitch.service.FreeSwitchConferenceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FreeSwitch会议室管理REST API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/freeswitch/conferences")
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true")
public class FreeSwitchConferenceController {

    private final FreeSwitchConferenceService conferenceService;
    private final FreeSwitchDtoMapper dtoMapper;

    /**
     * 创建会议室
     */
    @PostMapping
    public ResponseEntity<JsonResult<FreeSwitchConferenceDto>> createConference(
            @Valid @RequestBody CreateFreeSwitchConferenceRequest request,
            @CurrentUser UserEntity currentUser) {
        
        try {
            FreeSwitchConferenceEntity created = conferenceService.createConference(
                    request.getName(),
                    request.getDescription(),
                    request.getModeratorPin(),
                    request.getMemberPin(),
                    request.getMaxMembers()
            );
            
            FreeSwitchConferenceDto dto = dtoMapper.toConferenceDto(created);
            return ResponseEntity.ok(JsonResult.success(dto, "会议室创建成功"));
        } catch (Exception e) {
            log.error("创建FreeSwitch会议室失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取会议室列表
     */
    @GetMapping
    public ResponseEntity<JsonResult<Page<FreeSwitchConferenceDto>>> getConferences(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean withMembers) {
        
        try {
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? 
                    Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<FreeSwitchConferenceEntity> entities;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                entities = conferenceService.searchConferences(keyword, pageable);
            } else {
                entities = conferenceService.searchConferences("", pageable);
            }
            
            Page<FreeSwitchConferenceDto> dtos = entities.map(dtoMapper::toConferenceDto);
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("获取FreeSwitch会议室列表失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据ID获取会议室详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<JsonResult<FreeSwitchConferenceDto>> getConferenceById(@PathVariable Long id) {
        try {
            Optional<FreeSwitchConferenceEntity> entity = conferenceService.findById(id);
            if (entity.isPresent()) {
                FreeSwitchConferenceDto dto = dtoMapper.toConferenceDto(entity.get());
                return ResponseEntity.ok(JsonResult.success(dto));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取FreeSwitch会议室详情失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据名称获取会议室详情
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<JsonResult<FreeSwitchConferenceDto>> getConferenceByName(@PathVariable String name) {
        try {
            Optional<FreeSwitchConferenceEntity> entity = conferenceService.findByName(name);
            if (entity.isPresent()) {
                FreeSwitchConferenceDto dto = dtoMapper.toConferenceDto(entity.get());
                return ResponseEntity.ok(JsonResult.success(dto));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取FreeSwitch会议室详情失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 更新会议室信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<JsonResult<FreeSwitchConferenceDto>> updateConference(
            @PathVariable Long id,
            @Valid @RequestBody CreateFreeSwitchConferenceRequest request,
            @CurrentUser UserEntity currentUser) {
        
        try {
            FreeSwitchConferenceEntity updated = conferenceService.updateConference(
                    id,
                    request.getName(),
                    request.getDescription(),
                    request.getModeratorPin(),
                    request.getMemberPin(),
                    request.getMaxMembers(),
                    request.getActive()
            );
            
            FreeSwitchConferenceDto dto = dtoMapper.toConferenceDto(updated);
            return ResponseEntity.ok(JsonResult.success(dto, "会议室更新成功"));
        } catch (Exception e) {
            log.error("更新FreeSwitch会议室失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 删除会议室
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResult<Void>> deleteConference(
            @PathVariable Long id,
            @CurrentUser UserEntity currentUser) {
        
        try {
            conferenceService.deleteConference(id);
            return ResponseEntity.ok(JsonResult.success("会议室删除成功"));
        } catch (Exception e) {
            log.error("删除FreeSwitch会议室失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 激活/停用会议室
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<JsonResult<Void>> toggleConferenceStatus(
            @PathVariable Long id,
            @CurrentUser UserEntity currentUser) {
        
        try {
            conferenceService.toggleConferenceStatus(id);
            return ResponseEntity.ok(JsonResult.success("会议室状态切换成功"));
        } catch (Exception e) {
            log.error("切换FreeSwitch会议室状态失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 验证会议室访问权限
     */
    @PostMapping("/{name}/validate-access")
    public ResponseEntity<JsonResult<Boolean>> validateAccess(
            @PathVariable String name,
            @RequestParam String pin,
            @RequestParam(defaultValue = "false") boolean isModerator) {
        
        try {
            boolean hasAccess = conferenceService.validateAccess(name, pin, isModerator);
            return ResponseEntity.ok(JsonResult.success(hasAccess, 
                    hasAccess ? "访问验证成功" : "访问验证失败"));
        } catch (Exception e) {
            log.error("验证FreeSwitch会议室访问权限失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 成员加入会议室
     */
    @PostMapping("/{name}/join")
    public ResponseEntity<JsonResult<Boolean>> joinConference(
            @PathVariable String name,
            @RequestParam String memberUuid) {
        
        try {
            boolean joined = conferenceService.joinConference(name, memberUuid);
            return ResponseEntity.ok(JsonResult.success(joined, 
                    joined ? "成功加入会议室" : "加入会议室失败"));
        } catch (Exception e) {
            log.error("加入FreeSwitch会议室失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 成员离开会议室
     */
    @PostMapping("/{name}/leave")
    public ResponseEntity<JsonResult<Void>> leaveConference(
            @PathVariable String name,
            @RequestParam String memberUuid) {
        
        try {
            conferenceService.leaveConference(name, memberUuid);
            return ResponseEntity.ok(JsonResult.success("成功离开会议室"));
        } catch (Exception e) {
            log.error("离开FreeSwitch会议室失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 重置会议室成员数
     */
    @PatchMapping("/{id}/reset-members")
    public ResponseEntity<JsonResult<Void>> resetMemberCount(
            @PathVariable Long id,
            @CurrentUser UserEntity currentUser) {
        
        try {
            conferenceService.resetMemberCount(id);
            return ResponseEntity.ok(JsonResult.success("会议室成员数重置成功"));
        } catch (Exception e) {
            log.error("重置FreeSwitch会议室成员数失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取活跃会议室列表
     */
    @GetMapping("/active")
    public ResponseEntity<JsonResult<List<FreeSwitchConferenceDto>>> getActiveConferences() {
        try {
            List<FreeSwitchConferenceEntity> entities = conferenceService.getActiveConferences();
            List<FreeSwitchConferenceDto> dtos = entities.stream()
                    .map(dtoMapper::toConferenceDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("获取活跃FreeSwitch会议室失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取当前有成员的会议室
     */
    @GetMapping("/with-members")
    public ResponseEntity<JsonResult<List<FreeSwitchConferenceDto>>> getConferencesWithMembers() {
        try {
            List<FreeSwitchConferenceEntity> entities = conferenceService.getConferencesWithMembers();
            List<FreeSwitchConferenceDto> dtos = entities.stream()
                    .map(dtoMapper::toConferenceDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("获取有成员的FreeSwitch会议室失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据最大成员数过滤会议室
     */
    @GetMapping("/by-capacity")
    public ResponseEntity<JsonResult<List<FreeSwitchConferenceDto>>> getConferencesByCapacity(
            @RequestParam Integer minCapacity) {
        
        try {
            List<FreeSwitchConferenceEntity> entities = conferenceService.findByMaxMembersGreaterThanEqual(minCapacity);
            List<FreeSwitchConferenceDto> dtos = entities.stream()
                    .map(dtoMapper::toConferenceDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("根据容量获取FreeSwitch会议室失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取会议室统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<JsonResult<FreeSwitchConferenceService.ConferenceStatistics>> getStatistics() {
        try {
            FreeSwitchConferenceService.ConferenceStatistics statistics = conferenceService.getStatistics();
            return ResponseEntity.ok(JsonResult.success(statistics));
        } catch (Exception e) {
            log.error("获取FreeSwitch会议室统计失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }
}
