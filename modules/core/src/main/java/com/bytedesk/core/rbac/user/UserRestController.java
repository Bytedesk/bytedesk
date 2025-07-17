/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-24 13:00:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 11:50:23
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
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.push.PushRestService;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
// import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
// @Tag(name = "User Management", description = "User management APIs")
@Description("User Management Controller - User management APIs for user CRUD operations")
public class UserRestController extends BaseRestController<UserRequest> {

    private final UserRestService userRestService;

    private final UserService userService;

    private final PushRestService pushService;

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

    @ActionAnnotation(title = "user", action = "新建", description = "create user info")
    @Override
    public ResponseEntity<?> create(UserRequest request) {
        
        UserResponse userResponse = userRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = "user", action = "更新", description = "update user info")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserRequest userRequest) {

        UserResponse userResponse = userRestService.update(userRequest);

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
    public ResponseEntity<?> getProfile() {
        UserResponse userResponse = userRestService.getProfile();
        if (userResponse != null) {
            return ResponseEntity.ok(JsonResult.success(userResponse));
        } else {
            return ResponseEntity.ok().body(JsonResult.error("login first", -1, false));
        }
    }
    
    @ActionAnnotation(title = "user", action = "changePassword", description = "changePassword")
    @PostMapping("/change/password")
    public ResponseEntity<?> changePassword(@RequestBody UserRequest userRequest) {

        UserResponse userResponse = userService.changePassword(userRequest);

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

        // validate mobile & code
        // 验证手机验证码
        if (!pushService.validateCode(userRequest.getMobile(), userRequest.getCode(), request)) {
            return ResponseEntity.ok().body(JsonResult.error("validate code failed", -1, false));
        }

        UserResponse userResponse = userService.changeMobile(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = "用户", action = BytedeskConsts.ACTION_LOGOUT, description = "logout")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        userService.logout();

        return ResponseEntity.ok().body(JsonResult.success());
    }
    
}
