package com.bytedesk.freeswitch.controller;

import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.freeswitch.dto.*;
import com.bytedesk.freeswitch.model.FreeSwitchUserEntity;
import com.bytedesk.freeswitch.service.FreeSwitchUserService;
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
import java.util.Optional;

/**
 * FreeSwitch用户管理REST API控制器
 */
@Slf4j
@RestController
@RequestMapping("/freeswitch/api/v1/users")
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true")
public class FreeSwitchUserController {

    private final FreeSwitchUserService userService;
    private final FreeSwitchDtoMapper dtoMapper;

    /**
     * 创建FreeSwitch用户
     */
    @PostMapping
    public ResponseEntity<JsonResult<FreeSwitchUserDto>> createUser(
            @Valid @RequestBody CreateFreeSwitchUserRequest request,
            UserEntity currentUser) {
        
        try {
            FreeSwitchUserEntity entity = dtoMapper.toUserEntity(request);
            FreeSwitchUserEntity created = userService.createUser(
                    entity.getUsername(),
                    entity.getPassword(),
                    entity.getDomain(),
                    entity.getEmail(),
                    entity.getDisplayName()
            );
            
            FreeSwitchUserDto dto = dtoMapper.toUserDto(created);
            return ResponseEntity.ok(JsonResult.success(dto, "用户创建成功"));
        } catch (Exception e) {
            log.error("创建FreeSwitch用户失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取用户列表
     */
    @GetMapping
    public ResponseEntity<JsonResult<Page<FreeSwitchUserDto>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) Boolean online) {
        
        try {
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? 
                    Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<FreeSwitchUserEntity> entities;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                entities = userService.searchUsers(keyword, pageable);
            } else if (domain != null) {
                entities = userService.findByDomain(domain, pageable);
            } else if (enabled != null) {
                entities = userService.findByEnabled(enabled, pageable);
            } else if (online != null && online) {
                entities = userService.findOnlineUsers(pageable);
            } else {
                entities = userService.findAll(pageable);
            }
            
            Page<FreeSwitchUserDto> dtos = entities.map(dtoMapper::toUserDto);
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("获取FreeSwitch用户列表失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<JsonResult<FreeSwitchUserDto>> getUserById(@PathVariable Long id) {
        try {
            Optional<FreeSwitchUserEntity> entity = userService.findById(id);
            if (entity.isPresent()) {
                FreeSwitchUserDto dto = dtoMapper.toUserDto(entity.get());
                return ResponseEntity.ok(JsonResult.success(dto));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取FreeSwitch用户详情失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据用户名获取用户详情
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<JsonResult<FreeSwitchUserDto>> getUserByUsername(
            @PathVariable String username,
            @RequestParam(required = false) String domain) {
        
        try {
            Optional<FreeSwitchUserEntity> entity;
            if (domain != null) {
                entity = userService.findByUsernameAndDomain(username, domain);
            } else {
                entity = userService.findByUsername(username);
            }
            
            if (entity.isPresent()) {
                FreeSwitchUserDto dto = dtoMapper.toUserDto(entity.get());
                return ResponseEntity.ok(JsonResult.success(dto));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取FreeSwitch用户详情失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<JsonResult<FreeSwitchUserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody CreateFreeSwitchUserRequest request,
            @CurrentUser UserEntity currentUser) {
        
        try {
            FreeSwitchUserEntity updated = userService.updateUser(
                    id,
                    request.getUsername(),
                    request.getPassword(),
                    request.getEmail(),
                    request.getDisplayName(),
                    request.getEnabled()
            );
            
            FreeSwitchUserDto dto = dtoMapper.toUserDto(updated);
            return ResponseEntity.ok(JsonResult.success(dto, "用户更新成功"));
        } catch (Exception e) {
            log.error("更新FreeSwitch用户失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResult<Void>> deleteUser(
            @PathVariable Long id,
            @CurrentUser UserEntity currentUser) {
        
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(JsonResult.success("用户删除成功"));
        } catch (Exception e) {
            log.error("删除FreeSwitch用户失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 启用/禁用用户
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<JsonResult<Void>> toggleUserStatus(
            @PathVariable Long id,
            @CurrentUser UserEntity currentUser) {
        
        try {
            userService.toggleUserStatus(id);
            return ResponseEntity.ok(JsonResult.success("用户状态切换成功"));
        } catch (Exception e) {
            log.error("切换FreeSwitch用户状态失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 重置用户密码
     */
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<JsonResult<Void>> resetPassword(
            @PathVariable Long id,
            @RequestParam String newPassword,
            @CurrentUser UserEntity currentUser) {
        
        try {
            userService.resetPassword(id, newPassword);
            return ResponseEntity.ok(JsonResult.success("密码重置成功"));
        } catch (Exception e) {
            log.error("重置FreeSwitch用户密码失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 验证用户认证
     */
    @PostMapping("/authenticate")
    public ResponseEntity<JsonResult<Boolean>> authenticateUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String domain) {
        
        try {
            boolean authenticated = userService.authenticateUser(username, password, domain);
            return ResponseEntity.ok(JsonResult.success(authenticated, 
                    authenticated ? "认证成功" : "认证失败"));
        } catch (Exception e) {
            log.error("FreeSwitch用户认证失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 更新用户在线状态
     */
    @PatchMapping("/{id}/online-status")
    public ResponseEntity<JsonResult<Void>> updateOnlineStatus(
            @PathVariable Long id,
            @RequestParam Boolean online,
            @RequestParam(required = false) String ipAddress) {
        
        try {
            if (online) {
                userService.userLogin(id, ipAddress);
            } else {
                userService.userLogout(id);
            }
            return ResponseEntity.ok(JsonResult.success("在线状态更新成功"));
        } catch (Exception e) {
            log.error("更新FreeSwitch用户在线状态失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<JsonResult<FreeSwitchUserService.UserStatistics>> getUserStatistics() {
        try {
            FreeSwitchUserService.UserStatistics statistics = userService.getStatistics();
            return ResponseEntity.ok(JsonResult.success(statistics));
        } catch (Exception e) {
            log.error("获取FreeSwitch用户统计失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取在线用户列表
     */
    @GetMapping("/online")
    public ResponseEntity<JsonResult<Page<FreeSwitchUserDto>>> getOnlineUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("lastLoginTime").descending());
            Page<FreeSwitchUserEntity> entities = userService.findOnlineUsers(pageable);
            Page<FreeSwitchUserDto> dtos = entities.map(dtoMapper::toUserDto);
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("获取在线FreeSwitch用户失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }
}
