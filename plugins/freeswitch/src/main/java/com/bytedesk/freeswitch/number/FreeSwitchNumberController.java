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
package com.bytedesk.freeswitch.number;

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
public class FreeSwitchNumberController {

    private final FreeSwitchNumberService userService;

    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<JsonResult<?>> createNumber(@Valid @RequestBody FreeSwitchNumberRequest request) {
        try {
            FreeSwitchNumberEntity user = userService.createNumber(
                    request.getUsername(),
                    request.getDomain(),
                    request.getPassword(),
                    request.getDisplayName(),
                    request.getEmail(),
                    request.getAccountcode()
            );
            
            FreeSwitchNumberResponse response = FreeSwitchNumberResponse.fromEntitySafe(user);
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
    public ResponseEntity<JsonResult<?>> getNumber(@PathVariable Long id) {
        Optional<FreeSwitchNumberEntity> user = userService.findById(id);
        if (user.isPresent()) {
            FreeSwitchNumberResponse response = FreeSwitchNumberResponse.fromEntitySafe(user.get());
            return ResponseEntity.ok(JsonResult.success("获取用户详情成功", response));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<JsonResult<?>> getNumberByNumbername(@PathVariable String username) {
        Optional<FreeSwitchNumberEntity> user = userService.findByNumbername(username);
        if (user.isPresent()) {
            FreeSwitchNumberResponse response = FreeSwitchNumberResponse.fromEntitySafe(user.get());
            return ResponseEntity.ok(JsonResult.success("获取用户详情成功", response));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据用户名和域名获取用户
     */
    @GetMapping("/username/{username}/domain/{domain}")
    public ResponseEntity<JsonResult<?>> getNumberByNumbernameAndDomain(
            @PathVariable String username, @PathVariable String domain) {
        Optional<FreeSwitchNumberEntity> user = userService.findByNumbernameAndDomain(username, domain);
        if (user.isPresent()) {
            FreeSwitchNumberResponse response = FreeSwitchNumberResponse.fromEntitySafe(user.get());
            return ResponseEntity.ok(JsonResult.success("获取用户详情成功", response));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    public ResponseEntity<JsonResult<?>> getNumbers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<FreeSwitchNumberEntity> userPage = userService.findAll(pageable);
        Page<FreeSwitchNumberResponse> responsePage = userPage.map(FreeSwitchNumberResponse::fromEntitySafe);
        
        return ResponseEntity.ok(JsonResult.success("获取用户列表成功", responsePage));
    }

    /**
     * 根据域名获取用户列表
     */
    @GetMapping("/domain/{domain}")
    public ResponseEntity<JsonResult<?>> getNumbersByDomain(@PathVariable String domain) {
        List<FreeSwitchNumberEntity> users = userService.findByDomain(domain);
        List<FreeSwitchNumberResponse> responses = users.stream()
                .map(FreeSwitchNumberResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取域名用户列表成功", responses));
    }

    /**
     * 获取启用的用户列表
     */
    @GetMapping("/enabled")
    public ResponseEntity<JsonResult<?>> getEnabledNumbers() {
        List<FreeSwitchNumberEntity> users = userService.findEnabledNumbers();
        List<FreeSwitchNumberResponse> responses = users.stream()
                .map(FreeSwitchNumberResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取启用用户列表成功", responses));
    }

    /**
     * 获取在线的用户列表
     */
    @GetMapping("/online")
    public ResponseEntity<JsonResult<?>> getOnlineNumbers() {
        List<FreeSwitchNumberEntity> users = userService.findOnlineNumbers();
        List<FreeSwitchNumberResponse> responses = users.stream()
                .map(FreeSwitchNumberResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取在线用户列表成功", responses));
    }

    /**
     * 根据邮箱获取用户列表
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<JsonResult<?>> getNumbersByEmail(@PathVariable String email) {
        List<FreeSwitchNumberEntity> users = userService.findByEmail(email);
        List<FreeSwitchNumberResponse> responses = users.stream()
                .map(FreeSwitchNumberResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取用户列表成功", responses));
    }

    /**
     * 根据账户代码获取用户列表
     */
    @GetMapping("/accountcode/{accountcode}")
    public ResponseEntity<JsonResult<?>> getNumbersByAccountcode(@PathVariable String accountcode) {
        List<FreeSwitchNumberEntity> users = userService.findByAccountcode(accountcode);
        List<FreeSwitchNumberResponse> responses = users.stream()
                .map(FreeSwitchNumberResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取用户列表成功", responses));
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ResponseEntity<JsonResult<?>> updateNumber(
            @PathVariable Long id,
            @Valid @RequestBody FreeSwitchNumberRequest request) {
        try {
            FreeSwitchNumberEntity user = userService.updateNumber(
                    id,
                    request.getPassword(),
                    request.getDisplayName(),
                    request.getEmail(),
                    request.getAccountcode()
            );
            
            FreeSwitchNumberResponse response = FreeSwitchNumberResponse.fromEntitySafe(user);
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
    public ResponseEntity<JsonResult<?>> enableNumber(@PathVariable Long id) {
        try {
            userService.enableNumber(id);
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
    public ResponseEntity<JsonResult<?>> disableNumber(@PathVariable Long id) {
        try {
            userService.disableNumber(id);
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
    public ResponseEntity<JsonResult<?>> updateNumberRegistration(
            @RequestParam String username,
            @RequestParam String domain,
            @RequestParam String registerIp,
            @RequestParam(required = false) String userAgent) {
        try {
            userService.updateNumberRegistration(username, domain, registerIp, userAgent);
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
    public ResponseEntity<JsonResult<?>> deleteNumber(@PathVariable Long id) {
        try {
            userService.deleteNumber(id);
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
    public ResponseEntity<JsonResult<?>> validateNumberPassword(
            @RequestParam String username,
            @RequestParam String domain,
            @RequestParam String password) {
        boolean valid = userService.validateNumberPassword(username, domain, password);
        return ResponseEntity.ok(JsonResult.success("密码验证完成", valid));
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<JsonResult<?>> getNumberStats() {
        long totalCount = userService.countTotal();
        long enabledCount = userService.countEnabled();
        long onlineCount = userService.countOnline();
        
        NumberStats stats = new NumberStats(totalCount, enabledCount, onlineCount);
        return ResponseEntity.ok(JsonResult.success("获取用户统计成功", stats));
    }

    /**
     * 根据域名获取用户统计信息
     */
    @GetMapping("/stats/domain/{domain}")
    public ResponseEntity<JsonResult<?>> getNumberStatsByDomain(@PathVariable String domain) {
        long domainCount = userService.countByDomain(domain);
        return ResponseEntity.ok(JsonResult.success("获取域名用户统计成功", domainCount));
    }

    /**
     * 检查用户名和域名组合是否存在
     */
    @GetMapping("/exists")
    public ResponseEntity<JsonResult<?>> checkNumberExists(
            @RequestParam String username,
            @RequestParam String domain) {
        boolean exists = userService.existsByNumbernameAndDomain(username, domain);
        return ResponseEntity.ok(JsonResult.success("检查用户名成功", exists));
    }

    /**
     * 用户统计信息内部类
     */
    public static class NumberStats {
        private final long totalCount;
        private final long enabledCount;
        private final long onlineCount;

        public NumberStats(long totalCount, long enabledCount, long onlineCount) {
            this.totalCount = totalCount;
            this.enabledCount = enabledCount;
            this.onlineCount = onlineCount;
        }

        public long getTotalCount() { return totalCount; }
        public long getEnabledCount() { return enabledCount; }
        public long getOnlineCount() { return onlineCount; }
    }
}
