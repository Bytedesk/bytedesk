/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-12 17:44:32
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

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.action.ActionAnnotation;
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

    /**
     * get user info
     * 
     * @return
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        //
        User user = authService.getCurrentUser();

        UserResponse userResponse = ConvertUtils.convertToUserResponse(user);

        return ResponseEntity.ok(JsonResult.success(userResponse));
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

    /**  */
    @ActionAnnotation(title = "user", action = "logout", description = "logout")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // TODO: 清理token，使其过期
        return ResponseEntity.ok().body(JsonResult.success());
    }

    /** for testing，client will return 403, if dont have authority/role */
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
