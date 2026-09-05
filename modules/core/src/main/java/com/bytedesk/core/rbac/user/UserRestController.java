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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestControllerOverride;
import com.bytedesk.core.base.ExcelExportUtils;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.push.PushService;
import com.bytedesk.core.rbac.auth.AuthService;
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

    private static final DateTimeFormatter EXPORT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 昵称、用户名、手机、邮箱、当前组织、当前角色、描述、启用、超级管理员、性别、注册来源、创建时间、更新时间
    private static final int[] EXPORT_COLUMN_WIDTHS = {20, 20, 20, 25, 20, 25, 25, 10, 12, 10, 15, 20, 20};

    private static final String SEX_I18N_PREFIX = "export.user.sex.";

    private static final String REGISTER_SOURCE_I18N_PREFIX = "export.user.registerSource.";

    private final UserRestService userRestService;

    private final UserService userService;

    private final PushService pushService;

    private final AuthService authService;

    private final MessageSource messageSource;

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
    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_UPDATE, description = "user enabled update by super")
    @PostMapping("/update/enabled/by/super")
    public ResponseEntity<?> updateEnabledBySuper(@RequestBody UserRequest request) {

        UserResponse userResponse = userRestService.updateEnabledBySuper(request);
        
        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_EXPORT, description = "export user")
    @Override
    @GetMapping("/export")
    public Object export(UserRequest request, HttpServletResponse response) {
        try {
            Locale locale = ExcelExportUtils.resolveLocale(request);
            String sheetName = localize("export.user.sheet", "User", locale);
            String filePrefix = localize("export.user.file.prefix", "User", locale);

            Page<UserEntity> userPage = userRestService.queryByOrgEntity(request);
            List<List<Object>> rows = userPage.getContent().stream()
                    .map(entity -> buildExportRow(userRestService.convertToExcel(entity), locale))
                    .collect(Collectors.toList());

            ExcelExportUtils.writeCustomExcel(
                response,
                request,
                sheetName,
                filePrefix,
                buildExportHead(locale),
                rows,
                EXPORT_COLUMN_WIDTHS);
        } catch (Exception e) {
            log.error("export user failed: request={}", request, e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            String message = e.getMessage() != null ? e.getMessage() : e.toString();
            return JsonResult.error(message);
        }
        return "";
    }

    private List<List<String>> buildExportHead(Locale locale) {
        return List.of(
                List.of(localize("export.user.column.nickname", "Nickname", locale)),
                List.of(localize("export.user.column.username", "Username", locale)),
                List.of(localize("export.user.column.mobile", "Mobile", locale)),
                List.of(localize("export.user.column.email", "Email", locale)),
                List.of(localize("export.user.column.currentOrganization", "Current Organization", locale)),
                List.of(localize("export.user.column.currentRoles", "Current Roles", locale)),
                List.of(localize("export.user.column.description", "Description", locale)),
                List.of(localize("export.user.column.enabled", "Enabled", locale)),
                List.of(localize("export.user.column.superUser", "Super Admin", locale)),
                List.of(localize("export.user.column.sex", "Gender", locale)),
                List.of(localize("export.user.column.registerSource", "Register Source", locale)),
                List.of(localize("export.user.column.createdAt", "Created At", locale)),
                List.of(localize("export.user.column.updatedAt", "Updated At", locale)));
    }

    private List<Object> buildExportRow(UserExcel excel, Locale locale) {
        return List.of(
                // 昵称可能为 i18n key（如 i18n.file.assistant.name、i18n.system.notification），按请求语言翻译
                localizeI18nKey(excel.getNickname(), locale),
                nullableToEmpty(excel.getUsername()),
                nullableToEmpty(excel.getMobile()),
                nullableToEmpty(excel.getEmail()),
                // 组织名称可能为 i18n key，与前端 translateString 对齐
                localizeI18nKey(excel.getCurrentOrganizationName(), locale),
                // 角色名称（ROLE_* / i18n.* / 自定义），逐个翻译后拼接
                localizeJoinedValues(excel.getCurrentRoles(), locale),
                // 描述可能为 i18n key（如 i18n.user.description），按请求语言翻译
                localizeI18nKey(excel.getDescription(), locale),
                localizeBoolean(excel.getEnabled(), "export.user.enabled.", locale),
                localizeBoolean(excel.getSuperUser(), "export.user.superUser.", locale),
                localizeEnumValue(excel.getSex(), SEX_I18N_PREFIX, locale),
                localizeEnumValue(excel.getRegisterSource(), REGISTER_SOURCE_I18N_PREFIX, locale),
                formatExportDateTime(excel.getCreatedAt()),
                formatExportDateTime(excel.getUpdatedAt()));
    }

    /**
     * 翻译 i18n.* 前缀的国际化 key，非 key 内容原样返回（与前端 translateString 行为对齐）
     */
    private String localizeI18nKey(String value, Locale locale) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        if (!value.startsWith(I18Consts.I18N_PREFIX)) {
            return value;
        }
        return messageSource.getMessage(value, null, value, locale);
    }

    /**
     * 逗号分隔的多个值逐个翻译（如 "ROLE_ADMIN, ROLE_USER" -> "组织管理员, 用户"），
     * 支持 ROLE_* 与 i18n.* 前缀，自定义角色名原样保留
     */
    private String localizeJoinedValues(String joined, Locale locale) {
        if (!StringUtils.hasText(joined)) {
            return "";
        }
        return Arrays.stream(joined.split(", "))
                .map(value -> {
                    if (value.startsWith(I18Consts.I18N_PREFIX) || value.startsWith("ROLE_")) {
                        return messageSource.getMessage(value, null, value, locale);
                    }
                    return value;
                })
                .collect(Collectors.joining(", "));
    }

    /**
     * 布尔值翻译（是/否、启用/禁用），词条缺失时回退 true/false
     */
    private String localizeBoolean(Boolean value, String keyPrefix, Locale locale) {
        if (value == null) {
            return "";
        }
        return messageSource.getMessage(keyPrefix + value, null, String.valueOf(value), locale);
    }

    /**
     * 枚举值翻译（如 MALE -> 男），枚举名转小写作为词条后缀，未知值原样返回
     */
    private String localizeEnumValue(String value, String keyPrefix, Locale locale) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        String key = keyPrefix + value.toLowerCase(Locale.ROOT);
        return messageSource.getMessage(key, null, value, locale);
    }

    private String localize(String key, String defaultMessage, Locale locale) {
        return messageSource.getMessage(key, null, defaultMessage, locale);
    }

    private String formatExportDateTime(ZonedDateTime dateTime) {
        return dateTime != null ? EXPORT_DATETIME_FORMATTER.format(dateTime) : "";
    }

    private String nullableToEmpty(String value) {
        return value == null ? "" : value;
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

    // 管理员修改子成员用户密码；普通成员仅能修改自己的密码（均无需旧密码）
    @PreAuthorize(RolePermissions.ROLE_ADMIN + " or @authService.isSelfUserUid(#userRequest.uid)")
    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_CHANGE_PASSWORD, description = "changePassword")
    @PostMapping("/admin/change/password")
    public ResponseEntity<?> adminChangePassword(@RequestBody UserRequest userRequest) {

        UserResponse userResponse = userService.adminChangePassword(userRequest);

        return ResponseEntity.ok(JsonResult.success(userResponse));
    }

    // 已登录用户通过手机号/邮箱验证码重置自己的密码（忘记旧密码场景）
    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_CHANGE_PASSWORD, description = "resetPassword by verify code")
    @PostMapping("/reset/password")
    public ResponseEntity<?> resetPassword(@RequestBody UserRequest userRequest, HttpServletRequest request) {
        // 绑定校验：接收方必须等于当前登录用户自己绑定的手机号/邮箱，防越权重置
        boolean isMobileChannel = StringUtils.hasText(userRequest.getMobile());
        boolean isEmailChannel = StringUtils.hasText(userRequest.getEmail());
        if (!isMobileChannel && !isEmailChannel) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_EMAIL_OR_MOBILE_REQUIRED, -1, false));
        }
        UserEntity currentUser = authService.getUser();
        if (isMobileChannel) {
            if (!StringUtils.hasText(currentUser.getMobile())
                    || !currentUser.getMobile().equals(userRequest.getMobile())) {
                return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_MOBILE_NOT_BOUND, -1, false));
            }
        } else {
            if (!StringUtils.hasText(currentUser.getEmail())
                    || !currentUser.getEmail().equals(userRequest.getEmail())) {
                return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_EMAIL_NOT_BOUND, -1, false));
            }
        }

        // 验证码校验（一次性，命中即置 CONFIRMED，不可重放）
        boolean codeValid = isMobileChannel
                ? pushService.validateCode(userRequest.getMobile(), userRequest.getCountry(), userRequest.getCode(), request)
                : pushService.validateCode(userRequest.getEmail(), userRequest.getCode(), request);
        if (!codeValid) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_VALIDATE_FAILED, -2, false));
        }

        UserResponse userResponse = userService.resetPassword(userRequest);

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

    /**
     * 用户主动退出组织（只能退出非当前组织）
     */
    @ActionAnnotation(title = I18Consts.I18N_USER, action = I18Consts.I18N_ACTION_LEAVE_ORGANIZATION, description = "leave organization")
    @PostMapping("/leave/organization")
    @PreAuthorize(UserPermissions.HAS_USER_READ + " or " + RolePermissions.ROLE_SUPER)
    public ResponseEntity<?> leaveOrganization(@RequestBody UserRequest userRequest) {

        userRestService.leaveOrganization(userRequest.getOrgUid());

        return ResponseEntity.ok(JsonResult.success());
    }

    
}
