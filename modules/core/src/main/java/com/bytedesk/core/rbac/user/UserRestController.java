/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-24 13:00:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 16:06:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.push.PushRestService;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserRestController extends BaseRestController<UserRequest> {

    private final UserRestService userRestService;

    private final UserService userService;

    private final PushRestService pushService;

    @PreAuthorize("hasRole('SUPER')")
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @PreAuthorize("hasRole('SUPER')")
    @ActionAnnotation(title = "user", action = "新建", description = "create user info")
    @Override
    public ResponseEntity<?> create(UserRequest request) {
        
        UserResponse userResponse = userRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @PreAuthorize("hasRole('SUPER')")
    @ActionAnnotation(title = "user", action = "更新", description = "update user info")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserRequest userRequest) {

        UserResponse userResponse = userRestService.update(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @PreAuthorize("hasRole('SUPER')")
    @ActionAnnotation(title = "user", action = "删除", description = "delete user info")
    @Override
    public ResponseEntity<?> delete(UserRequest request) {
        
        userRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @PreAuthorize("hasRole('SUPER')")
    @Override
    public Object export(UserRequest request, HttpServletResponse response) {
        // query data to export
        Page<UserResponse> userPage = userRestService.queryByOrg(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "user-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<UserExcel> excelList = userPage.getContent().stream().map(userResponse -> userRestService.convertToExcel(userResponse)).toList();
            // write to excel
            EasyExcel.write(response.getOutputStream(), UserExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("member")
                    .doWrite(excelList);

        } catch (Exception e) {
            // reset response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //
            return JsonResult.error(e.getMessage());
        }

        return "";
    }


    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        
        UserResponse userResponse = userRestService.getProfile();

        if (userResponse != null) {
            return ResponseEntity.ok(JsonResult.success(userResponse));
        } else {
            return ResponseEntity.ok().body(JsonResult.error("user not found", -1, false));
        }

        // UserEntity user = authService.getUser(); // 返回的是缓存，导致修改后的数据无法获取
        // Optional<UserEntity> userOptional = userService.findByUid(user.getUid());
        // if (userOptional.isPresent()) {
        //     UserResponse userResponse = ConvertUtils.convertToUserResponse(userOptional.get());
        //     return ResponseEntity.ok(JsonResult.success(userResponse));
        // } else {
        //     return ResponseEntity.ok().body(JsonResult.error("user not found", -1, false));
        // }
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


    // 静态权限配置，测试
    /** for testing，client will return 403, if don't have authority/role */
    // @PreAuthorize(AuthorityPermissions.AUTHORITY_ANY)
    // @GetMapping("/test/super")
    // public ResponseEntity<?> testSuperAuthority() {
    //     return ResponseEntity.ok("you have super authority");
    // }

    /** no need to add ROLE_ prefix, system will auto add */
    // @GetMapping(value = "/test/cs")
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    // // @PreAuthorize("hasRole('CS')")
    // public ResponseEntity<?> testCsRole() {
    //     return ResponseEntity.ok("you have admin or cs role");
    // }

    
}
