/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:40:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 21:10:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.base.ExcelExportUtils;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/v1/action")
@Tag(name = "Action Log Management", description = "Action log management APIs for tracking user activities and system events")
public class ActionRestController extends BaseRestController<ActionRequest, ActionRestService> {

    /**
     * 与前端 ActionTable 列宽保持一致：昵称、标题、操作、IP、IP位置、创建时间
     */
    private static final int[] EXPORT_COLUMN_WIDTHS = {20, 25, 20, 20, 20, 25};

    private final ActionRestService actionRestService;

    private final MessageSource messageSource;

    @Operation(summary = "Query Action Logs by Organization", description = "Retrieve action logs for the current organization")
    @PreAuthorize(ActionPermissions.HAS_ACTION_READ)
    @Override
    public ResponseEntity<?> queryByOrg(ActionRequest request) {

        Page<ActionResponse> page = actionRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Action Logs by User", description = "Retrieve action logs for the current user")
    @PreAuthorize(ActionPermissions.HAS_ACTION_READ)
    @Override
    public ResponseEntity<?> queryByUser(ActionRequest request) {

        Page<ActionResponse> page = actionRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Action Log by UID", description = "Retrieve a specific action log by its unique identifier")
    @PreAuthorize(ActionPermissions.HAS_ACTION_READ)
    @Override
    public ResponseEntity<?> queryByUid(ActionRequest request) {

        ActionResponse action = actionRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(action));
    }

    @Operation(summary = "Create Action Log", description = "Create a new action log entry")
    @PreAuthorize(ActionPermissions.HAS_ACTION_CREATE)
    @Override
    public ResponseEntity<?> create(@RequestBody ActionRequest request) {

        ActionResponse action = actionRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(action));
    }

    @Operation(summary = "Update Action Log", description = "Update an existing action log entry")
    @PreAuthorize(ActionPermissions.HAS_ACTION_UPDATE)
    @Override
    public ResponseEntity<?> update(@RequestBody ActionRequest request) {

        ActionResponse action = actionRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(action));
    }

    @Operation(summary = "Delete Action Log", description = "Delete an action log entry")
    @PreAuthorize(ActionPermissions.HAS_ACTION_DELETE)
    @Override
    public ResponseEntity<?> delete(@RequestBody ActionRequest request) {

        actionRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    /**
     * 导出操作日志
     * 参考 MessageRestController#export：表头、单元格内容、文件名均按请求语言国际化
     *
     * @param request 导出请求
     * @param response HTTP响应
     * @return 导出结果
     */
    @Operation(summary = "Export Action Logs", description = "Export action logs to Excel format")
    @PreAuthorize(ActionPermissions.HAS_ACTION_EXPORT)
    @GetMapping("/export")
    public Object export(ActionRequest request, HttpServletResponse response) {
        try {
            Locale locale = ExcelExportUtils.resolveLocale(request);
            String sheetName = localize("export.action.sheet", "Log", locale);
            String filePrefix = localize("export.action.file.prefix", "Log", locale);

            Page<ActionEntity> actionPage = actionRestService.queryByOrgEntity(request);
            List<List<Object>> rows = actionPage.getContent().stream()
                    .map(entity -> buildExportRow(entity, locale))
                    .collect(Collectors.toList());

            ExcelExportUtils.writeCustomExcel(
                response,
                sheetName,
                filePrefix,
                buildExportHead(locale),
                rows,
                EXPORT_COLUMN_WIDTHS);
        } catch (Exception e) {
            log.error("export action failed: request={}", request, e);
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
                List.of(localize("export.action.column.nickname", "Nickname", locale)),
                List.of(localize("export.action.column.title", "Title", locale)),
                List.of(localize("export.action.column.action", "Action", locale)),
                List.of(localize("export.action.column.ip", "IP", locale)),
                List.of(localize("export.action.column.ipLocation", "IP Location", locale)),
                List.of(localize("export.action.column.createdAt", "Created At", locale)));
    }

    private List<Object> buildExportRow(ActionEntity entity, Locale locale) {
        String nickname = entity.getUser() != null ? nullableToEmpty(entity.getUser().getNickname()) : "";
        String title = nullableToEmpty(localizeActionValue(entity.getTitle(), locale));
        String action = nullableToEmpty(localizeActionValue(entity.getAction(), locale));
        String ip = nullableToEmpty(entity.getIp());
        String ipLocation = nullableToEmpty(entity.getIpLocation());
        String createdAt = nullableToEmpty(entity.getCreatedAtString());
        return List.of(nickname, title, action, ip, ipLocation, createdAt);
    }

    /**
     * 与前端 formatI18nValue 行为保持一致：
     * i18n. 前缀的键翻译为当前语言，支持通过 | 分隔传参，其余内容原样返回
     */
    private String localizeActionValue(String value, Locale locale) {
        if (value == null || value.isBlank()) {
            return value;
        }
        if (!value.startsWith(I18Consts.I18N_PREFIX) && !value.startsWith("ROLE_")) {
            return value;
        }
        String[] parts = value.split("\\|");
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : null;
        return messageSource.getMessage(parts[0], args, value, locale);
    }

    private String localize(String key, String defaultMessage, Locale locale) {
        return messageSource.getMessage(key, null, defaultMessage, locale);
    }

    private String nullableToEmpty(String value) {
        return value == null ? "" : value;
    }

}
