/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 10:52:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestControllerOverride;
import com.bytedesk.core.base.ExcelExportUtils;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * http://127.0.0.1:9003/swagger-ui/index.html
 * https://www.bezkoder.com/swagger-3-annotations/#Swagger_3_annotations
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Tag(name = "Member Management", description = "Member management APIs")
public class MemberRestController extends BaseRestControllerOverride<MemberRequest> {

    private static final DateTimeFormatter EXPORT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 昵称、用户名、手机、邮箱、工号、职位、座位号、分机号、部门、角色、可登录平台、创建时间、更新时间
    private static final int[] EXPORT_COLUMN_WIDTHS = {20, 20, 20, 25, 20, 20, 20, 20, 20, 25, 30, 20, 20};

    private static final String PLATFORM_I18N_PREFIX = "member.platform.option.";

    private final MemberRestService memberRestService;

    private final MessageSource messageSource;

    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query member by org")
    @Operation(summary = "Query Members by Organization", description = "Retrieve member list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(MemberRequest request) {
        //
        Page<MemberResponse> memberResponse = memberRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query member by user")
    @Operation(summary = "Query Members by User", description = "Retrieve member information by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(MemberRequest request) {
        //
        MemberResponse memberResponse = memberRestService.query(request);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_QUERY_USER_UID, description = "query member by user uid")
    @Operation(summary = "Query Member by User UID", description = "Retrieve member information by user UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_READ)
    @GetMapping("/query/userUid")
    public ResponseEntity<?> queryByUserUid(MemberRequest request) {
        //
        MemberResponse memberResponse = memberRestService.queryByUserUid(request);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query member by uid")
    @Operation(summary = "Query Member by UID", description = "Retrieve member details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(MemberRequest request) {
        
        MemberResponse memberResponse = memberRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @Operation(summary = "Create Member", description = "Create a new member")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_CREATE, description = "create member")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MemberRequest request) {

        MemberResponse member = memberRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(member));
    }

    @Operation(summary = "Update Member", description = "Update member information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_UPDATE, description = "update member")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody MemberRequest request) {

        MemberResponse member = memberRestService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(member));
    }

    @Operation(summary = "Delete Member", description = "Delete the specified member")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PreAuthorize(MemberPermissions.HAS_MEMBER_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_DELETE, description = "delete member")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody MemberRequest request) {

        memberRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Members", description = "Export member data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @PreAuthorize(MemberPermissions.HAS_MEMBER_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_EXPORT, description = "export member")
    @GetMapping("/export")
    public Object export(MemberRequest request, HttpServletResponse response) {
        try {
            Locale locale = ExcelExportUtils.resolveLocale(request);
            String sheetName = localize("export.member.sheet", "Member", locale);
            String filePrefix = localize("export.member.file.prefix", "Member", locale);

            Page<MemberEntity> memberPage = memberRestService.queryByOrgEntity(request);
            List<List<Object>> rows = memberPage.getContent().stream()
                    .map(entity -> buildExportRow(memberRestService.convertToExcel(entity), locale))
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
            return exportError(response, e);
        }
        return "";
    }

    private Object exportError(HttpServletResponse response, Exception e) {
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        String message = e.getMessage() != null ? e.getMessage() : e.toString();
        return JsonResult.error(message);
    }

    private List<List<String>> buildExportHead(Locale locale) {
        return List.of(
                List.of(localize("export.member.column.nickname", "Nickname", locale)),
                List.of(localize("export.member.column.username", "Username", locale)),
                List.of(localize("export.member.column.mobile", "Mobile", locale)),
                List.of(localize("export.member.column.email", "Email", locale)),
                List.of(localize("export.member.column.jobNo", "Job Number", locale)),
                List.of(localize("export.member.column.jobTitle", "Job Title", locale)),
                List.of(localize("export.member.column.seatNo", "Seat Number", locale)),
                List.of(localize("export.member.column.telephone", "Telephone", locale)),
                List.of(localize("export.member.column.department", "Department", locale)),
                List.of(localize("export.member.column.roles", "Roles", locale)),
                List.of(localize("export.member.column.allowedLoginPlatforms", "Allowed Login Platforms", locale)),
                List.of(localize("export.member.column.createdAt", "Created At", locale)),
                List.of(localize("export.member.column.updatedAt", "Updated At", locale)));
    }

    private List<Object> buildExportRow(MemberExcelExport excel, Locale locale) {
        return List.of(
                nullableToEmpty(excel.getNickname()),
                nullableToEmpty(excel.getUsername()),
                nullableToEmpty(excel.getMobile()),
                nullableToEmpty(excel.getEmail()),
                nullableToEmpty(excel.getJobNo()),
                // 职位可能为 i18n key（如 i18n.admin），按请求语言翻译
                localizeI18nKey(excel.getJobTitle(), locale),
                nullableToEmpty(excel.getSeatNo()),
                nullableToEmpty(excel.getTelephone()),
                // 部门名称可能为 i18n key，与前端 translateString 对齐
                localizeI18nKey(excel.getDepartmentName(), locale),
                // 角色名称（ROLE_* / i18n.* / 自定义），逐个翻译后拼接
                localizeJoinedValues(excel.getRoles(), locale),
                // 可登录平台编码 -> 与前端一致的平台名称（如 admin -> 管理后台 admin）
                localizePlatformCodes(excel.getAllowedLoginPlatforms(), locale),
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
     * 平台编码翻译为与前端一致的平台名称，词条缺失时回退显示原始编码
     */
    private String localizePlatformCodes(String joined, Locale locale) {
        if (!StringUtils.hasText(joined)) {
            return "";
        }
        return Arrays.stream(joined.split(", "))
                .map(code -> messageSource.getMessage(PLATFORM_I18N_PREFIX + code, null, code, locale))
                .collect(Collectors.joining(", "));
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

    @Operation(summary = "Activate Member", description = "Activate the specified member")
    @ApiResponse(responseCode = "200", description = "Activated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_UPDATE)
    @PostMapping("/activate")
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_ACTIVATE, description = "activate member")
    public ResponseEntity<?> activate(@RequestBody MemberRequest request) {
        //
        MemberResponse member = memberRestService.activate(request);
        //
        return ResponseEntity.ok(JsonResult.success(member));
    }

    @Operation(summary = "Force Logout Member", description = "Force the specified member to logout from desktop and block re-login")
    @ApiResponse(responseCode = "200", description = "Force logout applied successfully",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_UPDATE)
    @PostMapping("/force/logout")
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_UPDATE, description = "force logout member")
    public ResponseEntity<?> forceLogout(@RequestBody MemberRequest request) {
        MemberResponse member = memberRestService.forceLogout(request);
        return ResponseEntity.ok(JsonResult.success(member));
    }

    @Operation(summary = "Restore Member Login", description = "Restore the specified member login after a forced logout")
    @ApiResponse(responseCode = "200", description = "Member login restored successfully",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_UPDATE)
    @PostMapping("/restore/login")
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_UPDATE, description = "restore member login")
    public ResponseEntity<?> restoreLogin(@RequestBody MemberRequest request) {
        MemberResponse member = memberRestService.restoreLogin(request);
        return ResponseEntity.ok(JsonResult.success(member));
    }
}
