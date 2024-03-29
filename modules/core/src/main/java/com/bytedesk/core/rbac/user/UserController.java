/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-29 13:48:23
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

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.auth.AuthService;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

/**
 * 
 */
@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    // private final UserService userService;

    private final AuthService authService;

    private final ModelMapper modelMapper;

    /**
     * 
     * @return
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            // UserResponse userResponse = userService.getUser();
            User user = authService.getCurrentUser();

            UserResponse userResponse = modelMapper.map(user, UserResponse.class);

            return ResponseEntity.ok().body(new JsonResult<>("get user profile success", 200, userResponse));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserRequest userRequest) {
        try {
            // User user = authService.getCurrentUser();

            // UserResponse userResponse = modelMapper.map(user, UserResponse.class);

            return ResponseEntity.ok().body(new JsonResult<>("update user success", 200, null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        try {
            return ResponseEntity.ok().body("logout");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    // @GetMapping("/test")
    // public String test() {
    //     try {
    //         return "Welcome";
    //     } catch (Exception e) {
    //         throw new RuntimeException(e);
    //     }
    // }

    
    // @GetMapping("/me")
    // @PreAuthorize("isAuthenticated()")
    // public ResponseEntity<User> authenticatedUser() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    //     User currentUser = (User) authentication.getPrincipal();

    //     return ResponseEntity.ok(currentUser);
    // }

    // @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    // public ResponseEntity<List<User>> allUsers() {
    //     // List <User> users = userService.allUsers();

    //     return ResponseEntity.ok(null);
    // }

    
}
