/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 18:18:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.user;

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

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<JsonResult<?>> createUser(@Valid @RequestBody FreeSwitchUserRequest request) {
        try {
            FreeSwitchUserEntity user = userService.createUser(
                    request.getUsername(),
                    request.getDomain(),
                    request.getPassword(),
                    request.getDisplayName(),
                    request.getEmail(),
                    request.getAccountcode()
            );
            
            FreeSwitchUserResponse response = FreeSwitchUserResponse.fromEntitySafe(user);
            return ResponseEntity.ok(JsonResult.success("用户创建成功", response));
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<JsonResult<?>> getUser(@PathVariable Long id) {
        Optional<FreeSwitchUserEntity> user = userService.findById(id);
        if (user.isPresent()) {
            FreeSwitchUserResponse response = FreeSwitchUserResponse.fromEntitySafe(user.get());
            return ResponseEntity.ok(JsonResult.success("获取用户详情成功", response));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<JsonResult<?>> getUserByUsername(@PathVariable String username) {
        Optional<FreeSwitchUserEntity> user = userService.findByUsername(username);
        if (user.isPresent()) {
            FreeSwitchUserResponse response = FreeSwitchUserResponse.fromEntitySafe(user.get());
            return ResponseEntity.ok(JsonResult.success("获取用户详情成功", response));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据用户名和域名获取用户
     */
    @GetMapping("/username/{username}/domain/{domain}")
    public ResponseEntity<JsonResult<?>> getUserByUsernameAndDomain(
            @PathVariable String username, @PathVariable String domain) {
        Optional<FreeSwitchUserEntity> user = userService.findByUsernameAndDomain(username, domain);
        if (user.isPresent()) {
            FreeSwitchUserResponse response = FreeSwitchUserResponse.fromEntitySafe(user.get());
            return ResponseEntity.ok(JsonResult.success("获取用户详情成功", response));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    public ResponseEntity<JsonResult<?>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<FreeSwitchUserEntity> userPage = userService.findAll(pageable);
        Page<FreeSwitchUserResponse> responsePage = userPage.map(FreeSwitchUserResponse::fromEntitySafe);
        
        return ResponseEntity.ok(JsonResult.success("获取用户列表成功", responsePage));
    }

    /**
     * 根据域名获取用户列表
     */
    @GetMapping("/domain/{domain}")
    public ResponseEntity<JsonResult<?>> getUsersByDomain(@PathVariable String domain) {
        List<FreeSwitchUserEntity> users = userService.findByDomain(domain);
        List<FreeSwitchUserResponse> responses = users.stream()
                .map(FreeSwitchUserResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取域名用户列表成功", responses));
    }

    /**
     * 获取启用的用户列表
     */
    @GetMapping("/enabled")
    public ResponseEntity<JsonResult<?>> getEnabledUsers() {
        List<FreeSwitchUserEntity> users = userService.findEnabledUsers();
        List<FreeSwitchUserResponse> responses = users.stream()
                .map(FreeSwitchUserResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取启用用户列表成功", responses));
    }

    /**
     * 获取在线的用户列表
     */
    @GetMapping("/online")
    public ResponseEntity<JsonResult<?>> getOnlineUsers() {
        List<FreeSwitchUserEntity> users = userService.findOnlineUsers();
        List<FreeSwitchUserResponse> responses = users.stream()
                .map(FreeSwitchUserResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取在线用户列表成功", responses));
    }

    /**
     * 根据邮箱获取用户列表
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<JsonResult<?>> getUsersByEmail(@PathVariable String email) {
        List<FreeSwitchUserEntity> users = userService.findByEmail(email);
        List<FreeSwitchUserResponse> responses = users.stream()
                .map(FreeSwitchUserResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取用户列表成功", responses));
    }

    /**
     * 根据账户代码获取用户列表
     */
    @GetMapping("/accountcode/{accountcode}")
    public ResponseEntity<JsonResult<?>> getUsersByAccountcode(@PathVariable String accountcode) {
        List<FreeSwitchUserEntity> users = userService.findByAccountcode(accountcode);
        List<FreeSwitchUserResponse> responses = users.stream()
                .map(FreeSwitchUserResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取用户列表成功", responses));
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ResponseEntity<JsonResult<?>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody FreeSwitchUserRequest request) {
        try {
            FreeSwitchUserEntity user = userService.updateUser(
                    id,
                    request.getPassword(),
                    request.getDisplayName(),
                    request.getEmail(),
                    request.getAccountcode()
            );
            
            FreeSwitchUserResponse response = FreeSwitchUserResponse.fromEntitySafe(user);
            return ResponseEntity.ok(JsonResult.success("用户更新成功", response));
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 启用用户
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<JsonResult<?>> enableUser(@PathVariable Long id) {
        try {
            userService.enableUser(id);
            return ResponseEntity.ok(JsonResult.success("用户启用成功"));
        } catch (Exception e) {
            log.error("启用用户失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 禁用用户
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<JsonResult<?>> disableUser(@PathVariable Long id) {
        try {
            userService.disableUser(id);
            return ResponseEntity.ok(JsonResult.success("用户禁用成功"));
        } catch (Exception e) {
            log.error("禁用用户失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 更新用户注册信息
     */
    @PutMapping("/registration")
    public ResponseEntity<JsonResult<?>> updateUserRegistration(
            @RequestParam String username,
            @RequestParam String domain,
            @RequestParam String registerIp,
            @RequestParam(required = false) String userAgent) {
        try {
            userService.updateUserRegistration(username, domain, registerIp, userAgent);
            return ResponseEntity.ok(JsonResult.success("用户注册信息更新成功"));
        } catch (Exception e) {
            log.error("更新用户注册信息失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResult<?>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(JsonResult.success("用户删除成功"));
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 验证用户密码
     */
    @PostMapping("/validate")
    public ResponseEntity<JsonResult<?>> validateUserPassword(
            @RequestParam String username,
            @RequestParam String domain,
            @RequestParam String password) {
        boolean valid = userService.validateUserPassword(username, domain, password);
        return ResponseEntity.ok(JsonResult.success("密码验证完成", valid));
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<JsonResult<?>> getUserStats() {
        long totalCount = userService.countTotal();
        long enabledCount = userService.countEnabled();
        long onlineCount = userService.countOnline();
        
        UserStats stats = new UserStats(totalCount, enabledCount, onlineCount);
        return ResponseEntity.ok(JsonResult.success("获取用户统计成功", stats));
    }

    /**
     * 根据域名获取用户统计信息
     */
    @GetMapping("/stats/domain/{domain}")
    public ResponseEntity<JsonResult<?>> getUserStatsByDomain(@PathVariable String domain) {
        long domainCount = userService.countByDomain(domain);
        return ResponseEntity.ok(JsonResult.success("获取域名用户统计成功", domainCount));
    }

    /**
     * 检查用户名和域名组合是否存在
     */
    @GetMapping("/exists")
    public ResponseEntity<JsonResult<?>> checkUserExists(
            @RequestParam String username,
            @RequestParam String domain) {
        boolean exists = userService.existsByUsernameAndDomain(username, domain);
        return ResponseEntity.ok(JsonResult.success("检查用户名成功", exists));
    }

    /**
     * 用户统计信息内部类
     */
    public static class UserStats {
        private final long totalCount;
        private final long enabledCount;
        private final long onlineCount;

        public UserStats(long totalCount, long enabledCount, long onlineCount) {
            this.totalCount = totalCount;
            this.enabledCount = enabledCount;
            this.onlineCount = onlineCount;
        }

        public long getTotalCount() { return totalCount; }
        public long getEnabledCount() { return enabledCount; }
        public long getOnlineCount() { return onlineCount; }
    }
}
