/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-28 13:40:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.push.PushService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

/**
 * 
 */
@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthService authService;

    private final PushService pushService;
    
    /**
     * get user info
     * 
     * @return
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        //
        UserEntity user = authService.getCurrentUser(); // 返回的是缓存，导致修改后的数据无法获取
        Optional<UserEntity> userOptional = userService.findByUid(user.getUid());
        if (userOptional.isPresent()) {

            UserResponse userResponse = ConvertUtils.convertToUserResponse(userOptional.get());

            return ResponseEntity.ok(JsonResult.success(userResponse));
        } else {
            return ResponseEntity.ok().body(JsonResult.error("user not found", -1, false));
        }
    }

    /**
     * update user info
     * 
     * @param userRequest
     * @return
     */
    @ActionAnnotation(title = "user", action = "update", description = "update user info")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserRequest userRequest) {

        UserResponse userResponse = userService.update(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = "user", action = "changePassword", description = "changePassword")
    @PostMapping("/change/password")
    public ResponseEntity<?> changePassword(@RequestBody UserRequest userRequest) {

        UserResponse userResponse = userService.changePassword(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = "user", action = "changeEmail", description = "changeEmail")
    @PostMapping("/change/email")
    public ResponseEntity<?> changeEmail(@RequestBody UserRequest userRequest) {

        // validate email & code
        // 验证邮箱验证码
        if (!pushService.validateCode(userRequest.getEmail(), userRequest.getCode())) {
            return ResponseEntity.ok().body(JsonResult.error("validate code failed", -1, false));
        }

        UserResponse userResponse = userService.changeEmail(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = "user", action = "changeMobile", description = "changeMobile")
    @PostMapping("/change/mobile")
    public ResponseEntity<?> changeMobile(@RequestBody UserRequest userRequest) {

        // validate mobile & code
        // 验证手机验证码
        if (!pushService.validateCode(userRequest.getMobile(), userRequest.getCode())) {
            return ResponseEntity.ok().body(JsonResult.error("validate code failed", -1, false));
        }

        UserResponse userResponse = userService.changeMobile(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    /**  */
    @ActionAnnotation(title = "user", action = "logout", description = "logout")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        userService.logout();
        
        return ResponseEntity.ok().body(JsonResult.success());
    }

    /** for testing，client will return 403, if don't have authority/role */
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER', 'ROLE_ADMIN')")
    // @PreAuthorize("hasAuthority('ROLE_SUPER') or hasAuthority('ROLE_ADMIN')")
    // @PreAuthorize("hasAuthority('ROLE_SUPER')")
    @GetMapping("/test/super")
    public ResponseEntity<?> testSuperAuthority() {
        return ResponseEntity.ok("you have super authority");
    }

    /** no need to add ROLE_ prefix, system will auto add */
    @GetMapping(value = "/test/cs")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER', 'CS')")
    // @PreAuthorize("hasRole('CS')")
    public ResponseEntity<?> testCsRole() {
        return ResponseEntity.ok("you have admin or cs role");
    }


}
