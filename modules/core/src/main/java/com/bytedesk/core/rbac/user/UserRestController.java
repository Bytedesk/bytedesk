/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-24 13:00:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 09:06:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestControllerOverride;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.push.PushService;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import lombok.extern.slf4j.Slf4j;
import com.bytedesk.core.utils.JwtUtils;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management APIs")
@Description("User Management Controller - User management APIs for user CRUD operations")
public class UserRestController extends BaseRestControllerOverride<UserRequest> {

    private final UserRestService userRestService;

    private final UserService userService;

    private final PushService pushService;

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Override
    public ResponseEntity<?> queryByOrg(UserRequest request) {
        
        Page<UserResponse> userResponse = userRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @Override
    public ResponseEntity<?> queryByUser(UserRequest request) {
        
        Page<UserResponse> userResponse = userRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @Override
    public ResponseEntity<?> queryByUid(UserRequest request) {
        
        UserResponse userResponse = userRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @ActionAnnotation(title = "user", action = "新建", description = "create user info")
    @Override
    public ResponseEntity<?> create(UserRequest request) {
        
        UserResponse userResponse = userRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = "user", action = "更新", description = "update user info")
    @PreAuthorize(UserPermissions.HAS_USER_UPDATE + " or " + RolePermissions.ROLE_SUPER)
    @Override
    public ResponseEntity<?> update(UserRequest request) {

        UserResponse userResponse = userRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @ActionAnnotation(title = "user", action = "删除", description = "delete user info")
    @Override
    public ResponseEntity<?> delete(UserRequest request) {
        
        userRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Override
    public Object export(UserRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            userRestService,
            UserExcel.class,
            "用户",
            "user"
        );
    }

    @GetMapping("/profile")
    @PreAuthorize(UserPermissions.HAS_USER_READ + " or " + RolePermissions.ROLE_SUPER)
    public ResponseEntity<?> getProfile() {

        UserResponse userResponse = userRestService.getProfile();
        
        return ResponseEntity.ok(JsonResult.success(userResponse));
    }
    
    // 用户自己修改密码
    @ActionAnnotation(title = "user", action = "changePassword", description = "changePassword")
    @PostMapping("/change/password")
    public ResponseEntity<?> changePassword(@RequestBody UserRequest userRequest) {

        UserResponse userResponse = userService.changePassword(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    // 管理员修改子成员用户密码
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "admin", action = "changePassword", description = "changePassword")
    @PostMapping("/admin/change/password")
    public ResponseEntity<?> adminChangePassword(@RequestBody UserRequest userRequest) {

        UserResponse userResponse = userService.adminChangePassword(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = "user", action = "changeEmail", description = "changeEmail")
    @PostMapping("/change/email")
    public ResponseEntity<?> changeEmail(@RequestBody UserRequest userRequest, HttpServletRequest request) {
        // validate email & code
        // 验证邮箱验证码
        if (!pushService.validateCode(userRequest.getEmail(), userRequest.getCode(), request)) {
            return ResponseEntity.ok().body(JsonResult.error("validate code failed", -1, false));
        }

        UserResponse userResponse = userService.changeEmail(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = "user", action = "changeMobile", description = "changeMobile")
    @PostMapping("/change/mobile")
    public ResponseEntity<?> changeMobile(@RequestBody UserRequest userRequest, HttpServletRequest request) {

        // 验证手机验证码
        if (!pushService.validateCode(userRequest.getMobile(), userRequest.getCode(), request)) {
            return ResponseEntity.ok().body(JsonResult.error("validate code failed", -1, false));
        }

        UserResponse userResponse = userService.changeMobile(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = "用户", action = BytedeskConsts.ACTION_LOGOUT, description = "logout")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String accessToken = JwtUtils.parseAccessToken(request);
        // log.debug("logout {}", accessToken);

        if (!StringUtils.hasText(accessToken)) {
            return ResponseEntity.ok().body(JsonResult.error("accessToken is empty", -1, false));
        }

        userService.logout(accessToken);

        return ResponseEntity.ok().body(JsonResult.success());
    }

    
}
