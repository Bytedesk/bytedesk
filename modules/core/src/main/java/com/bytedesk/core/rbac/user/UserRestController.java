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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.push.PushService;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.rbac.organization.OrganizationResponseSimple;

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
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(UserRequest request) {
        
        Page<UserResponse> userResponse = userRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(UserRequest request) {
        
        Page<UserResponse> userResponse = userRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(UserRequest request) {
        
        UserResponse userResponse = userRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_CREATE, description = "create user info")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserRequest request) {
        
        UserResponse userResponse = userRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_UPDATE, description = "update user info")
    @PreAuthorize(UserPermissions.HAS_USER_UPDATE + " or " + RolePermissions.ROLE_SUPER)
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserRequest request) {

        UserResponse userResponse = userRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_DELETE, description = "delete user info")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody UserRequest request) {
        
        userRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_UPDATE, description = "restore user info")
    @Override
    @PostMapping("/restore")
    public ResponseEntity<?> restore(@RequestBody UserRequest request) {

        UserResponse userResponse = userRestService.restore(request);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Override
    @GetMapping("/export")
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

    /**
     * 获取当前用户所属组织列表（用于前端组织切换 UI）。
     */
    @GetMapping("/organizations")
    @PreAuthorize(UserPermissions.HAS_USER_READ + " or " + RolePermissions.ROLE_SUPER)
    public ResponseEntity<?> getOrganizations() {

        java.util.List<OrganizationResponseSimple> organizations = userRestService.getOrganizations();

        return ResponseEntity.ok(JsonResult.success(organizations));
    }

    /**
     * 切换当前组织（写入 User.currentOrganization，并同步 currentRoles）。
     * 请求体使用 BaseRequest 的 orgUid 字段。
     */
    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_SWITCH_ORGANIZATION, description = "switch current organization")
    @PostMapping("/switch/organization")
    @PreAuthorize(UserPermissions.HAS_USER_READ + " or " + RolePermissions.ROLE_SUPER)
    public ResponseEntity<?> switchOrganization(@RequestBody UserRequest userRequest) {

        UserResponse userResponse = userRestService.switchCurrentOrganization(userRequest.getOrgUid());

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_SWITCH_ORGANIZATION, description = "switch target user current organization by super")
    @PostMapping("/switch/organization/by/super")
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    public ResponseEntity<?> switchOrganizationBySuper(@RequestBody UserRequest userRequest) {

        UserResponse userResponse = userRestService.switchUserOrganization(userRequest.getUid(), userRequest.getOrgUid());

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }
    
    // 用户自己修改密码
    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_CHANGE_PASSWORD, description = "changePassword")
    @PostMapping("/change/password")
    public ResponseEntity<?> changePassword(@RequestBody UserRequest userRequest) {

        UserResponse userResponse = userService.changePassword(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    // 管理员修改子成员用户密码
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_CHANGE_PASSWORD, description = "changePassword")
    @PostMapping("/admin/change/password")
    public ResponseEntity<?> adminChangePassword(@RequestBody UserRequest userRequest) {

        UserResponse userResponse = userService.adminChangePassword(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_CHANGE_EMAIL, description = "changeEmail")
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

    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_CHANGE_MOBILE, description = "changeMobile")
    @PostMapping("/change/mobile")
    public ResponseEntity<?> changeMobile(@RequestBody UserRequest userRequest, HttpServletRequest request) {

        // 验证手机验证码
        if (!pushService.validateCode(userRequest.getMobile(), userRequest.getCountry(), userRequest.getCode(), request)) {
            return ResponseEntity.ok().body(JsonResult.error("validate code failed", -1, false));
        }

        UserResponse userResponse = userService.changeMobile(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_LOGOUT, description = "logout")
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
