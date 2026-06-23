/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 18:02:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_settings;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.util.StringUtils;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.ticket_settings_binding.TicketSettingsBatchBindRequest;

import java.util.Map;
import com.bytedesk.ticket.ticket_settings_category.TicketCategoryItemResponse;
import com.bytedesk.ticket.ticket_settings_category.TicketCategorySettingsResponse;
import com.bytedesk.ticket.ticket_settings_category.TicketCategoryVisitorItemResponse;
import com.bytedesk.ticket.ticket_settings_category.TicketCategoryVisitorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ticket/settings")
@AllArgsConstructor
@Tag(name = "TicketSettings Management", description = "TicketSettings management APIs for organizing and categorizing content with ticketSettings")
@Description("TicketSettings Management Controller - Content ticket settings and categorization APIs")
public class TicketSettingsRestController extends BaseRestController<TicketSettingsRequest, TicketSettingsRestService> {

    private final TicketSettingsRestService ticketSettingsRestService;

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query ticketSettings by org")
    @Operation(summary = "Query TicketSettings by Organization", description = "Retrieve ticket settings for the current organization")
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(TicketSettingsRequest request) {
        
        Page<TicketSettingsResponse> ticketSettings = ticketSettingsRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(ticketSettings));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query ticketSettings by user")
    @Operation(summary = "Query ticketSettings by User", description = "Retrieve ticketSettings for the current user")
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(TicketSettingsRequest request) {
        
        Page<TicketSettingsResponse> ticketSettings = ticketSettingsRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(ticketSettings));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query ticketSettings by uid")
    @Operation(summary = "Query TicketSettings by UID", description = "Retrieve a specific ticketSettings by its unique identifier")
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(TicketSettingsRequest request) {
        
        TicketSettingsResponse ticketSettings = ticketSettingsRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(ticketSettings));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_CREATE, description = "create ticketSettings")
    @Operation(summary = "Create TicketSettings", description = "Create a new ticketSettings")
    @PostMapping("/create")
    @Override
    // @PreAuthorize("hasAuthority('TICKET_SETTINGS_CREATE')")
    public ResponseEntity<?> create(@RequestBody TicketSettingsRequest request) {
        
        TicketSettingsResponse ticketSettings = ticketSettingsRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(ticketSettings));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_UPDATE, description = "update ticketSettings")
    @Operation(summary = "Update TicketSettings", description = "Update an existing ticketSettings")
    @PostMapping("/update")
    @Override
    // @PreAuthorize("hasAuthority('TICKET_SETTINGS_UPDATE')")
    public ResponseEntity<?> update(@RequestBody TicketSettingsRequest request) {
        
        TicketSettingsResponse ticketSettings = ticketSettingsRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(ticketSettings));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_DELETE, description = "delete ticketSettings")
    @Operation(summary = "Delete TicketSettings", description = "Delete a ticketSettings")
    @PostMapping("/delete")
    @Override
    // @PreAuthorize("hasAuthority('TICKET_SETTINGS_DELETE')")
    public ResponseEntity<?> delete(@RequestBody TicketSettingsRequest request) {
        if (request == null || request.getUid() == null) {
            return ResponseEntity.badRequest().body(JsonResult.error("uid is required"));
        }
        try {
            ticketSettingsRestService.delete(request);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(JsonResult.error(ex.getMessage()));
        }
        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_EXPORT, description = "export ticketSettings")
    @Operation(summary = "Export ticketSettings", description = "Export ticketSettings to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TICKET_SETTINGS_EXPORT')")
    @GetMapping("/export")
    public Object export(TicketSettingsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            ticketSettingsRestService,
            TicketSettingsExcel.class,
            "Ticket Settings",
            "ticketSettings"
        );
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_BY_WORKGROUP, description = "通过 orgUid+workgroupUid 获取工单设置，若不存在返回默认模板")
    @Operation(summary = "Get TicketSettings by workgroup", description = "Get settings by orgUid and workgroupUid; returns defaults if missing")
    @GetMapping("/orgs/{orgUid}/workgroups/{workgroupUid}")
    public ResponseEntity<?> getByWorkgroup(
            @PathVariable("orgUid") String orgUid,
            @PathVariable("workgroupUid") String workgroupUid,
            @RequestParam(value = "type", required = false) String type) {
        TicketSettingsResponse resp = StringUtils.hasText(type)
            ? ticketSettingsRestService.getOrDefaultByWorkgroup(orgUid, workgroupUid, type)
            : ticketSettingsRestService.getOrDefaultByWorkgroup(orgUid, workgroupUid);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_SAVE_BY_WORKGROUP, description = "保存或更新指定工作组的工单设置(草稿)")
    @Operation(summary = "Save TicketSettings by workgroup", description = "Upsert ticket settings draft by orgUid+workgroupUid")
    @PostMapping("/orgs/{orgUid}/workgroups/{workgroupUid}")
        public ResponseEntity<?> saveByWorkgroup(
            @PathVariable("orgUid") String orgUid,
            @PathVariable("workgroupUid") String workgroupUid,
            @RequestBody TicketSettingsRequest request) {

        TicketSettingsResponse resp = ticketSettingsRestService.saveByWorkgroup(orgUid, workgroupUid, request);

        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_PUBLISH, description = "发布当前工作组的工单草稿配置")
    @Operation(summary = "Publish TicketSettings", description = "Publish draft settings to active for given TicketSettings uid")
    @PostMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody TicketSettingsRequest request) {
        if (request == null || request.getUid() == null) {
            return ResponseEntity.badRequest().body(JsonResult.error("uid is required"));
        }
        TicketSettingsResponse resp = ticketSettingsRestService.publish(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_PUBLISH_BY_WORKGROUP, description = "通过 orgUid+workgroupUid 发布草稿配置")
    @Operation(summary = "Publish TicketSettings by workgroup", description = "Publish draft settings for given orgUid+workgroupUid")
    @PostMapping("/orgs/{orgUid}/workgroups/{workgroupUid}/publish")
    public ResponseEntity<?> publishByWorkgroup(
            @PathVariable("orgUid") String orgUid,
            @PathVariable("workgroupUid") String workgroupUid) {

        TicketSettingsResponse resp = ticketSettingsRestService.publishByWorkgroup(orgUid, workgroupUid);

        return ResponseEntity.ok(JsonResult.success(resp));
    }

    // ===== 新增：批量绑定工作组到指定 TicketSettings =====
    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_BATCH_BIND_WORKGROUP, description = "将多个工作组绑定到同一套工单设置")
    @Operation(summary = "Bind workgroups to TicketSettings", description = "Bind multiple workgroups to one ticket settings instance")
    @PostMapping("/{uid}/orgs/{orgUid}/bindings")
    public ResponseEntity<?> bindWorkgroups(
            @PathVariable("uid") String uid,
            @PathVariable("orgUid") String orgUid,
            @RequestBody TicketSettingsBatchBindRequest request) {
        if (request == null || request.getWorkgroupUids() == null) {
            return ResponseEntity.badRequest().body(JsonResult.error("workgroupUids are required"));
        }
        ticketSettingsRestService.bindWorkgroups(uid, orgUid, request.getWorkgroupUids());
        return ResponseEntity.ok(JsonResult.success());
    }

    // ===== 新增：查询某 TicketSettings 已绑定的工作组列表 =====
    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_BIND_WORKGROUP, description = "列出使用该工单设置的所有工作组")
    @Operation(summary = "List bound workgroups", description = "List workgroups bound to the given ticket settings")
    @GetMapping("/bindings")
    public ResponseEntity<?> listBindings(TicketSettingsRequest request) {
        if (request == null || request.getUid() == null) {
            return ResponseEntity.badRequest().body(JsonResult.error("uid is required"));
        }
        return ResponseEntity.ok(JsonResult.success(ticketSettingsRestService.listBindings(request.getUid())));
    }

    // ===== 新增：按工作组查询工单分类列表（供 admin 前端调用）=====
    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_CATEGORIES_BY_WORKGROUP, description = "通过 orgUid+workgroupUid 获取工单分类列表")
    @Operation(summary = "Get ticket categories by workgroup", description = "Get enabled ticket categories for given orgUid+workgroupUid")
    @GetMapping("/orgs/{orgUid}/workgroups/{workgroupUid}/categories")
    public ResponseEntity<?> getCategoriesByWorkgroup(
            @PathVariable("orgUid") String orgUid,
            @PathVariable("workgroupUid") String workgroupUid,
            @RequestParam(value = "type", required = false) String type) {
        TicketSettingsResponse settings = StringUtils.hasText(type)
            ? ticketSettingsRestService.getOrDefaultByWorkgroup(orgUid, workgroupUid, type)
            : ticketSettingsRestService.getOrDefaultByWorkgroup(orgUid, workgroupUid);
        TicketCategoryVisitorResponse response = 
            toCategoryVisitorResponse(settings != null ? settings.getCategorySettings() : null);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 将分类设置转换为访客端响应格式
     */
    private TicketCategoryVisitorResponse toCategoryVisitorResponse(
            TicketCategorySettingsResponse categorySettings) {
        if (categorySettings == null) {
            return TicketCategoryVisitorResponse.empty();
        }

        java.util.List<TicketCategoryVisitorItemResponse> items = 
            categorySettings.getItems().stream()
                .filter(item -> Boolean.TRUE.equals(item.getEnabled()))
                .sorted(java.util.Comparator.comparing(
                    (TicketCategoryItemResponse item) -> 
                        item.getOrderIndex() != null ? item.getOrderIndex() : Integer.MAX_VALUE)
                    .thenComparing(
                        TicketCategoryItemResponse::getName, 
                        java.util.Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(item -> TicketCategoryVisitorItemResponse.builder()
                        .uid(item.getUid())
                        .name(item.getName())
                        .description(item.getDescription())
                        .build())
                .collect(java.util.stream.Collectors.toList());

        String configuredDefaultUid = categorySettings.getDefaultCategoryUid();
        boolean defaultAvailable = items.stream()
            .anyMatch(item -> java.util.Objects.equals(item.getUid(), configuredDefaultUid));
        String effectiveDefaultUid = defaultAvailable
            ? configuredDefaultUid
            : items.stream().findFirst()
                .map(TicketCategoryVisitorItemResponse::getUid)
                .orElse(null);

        return TicketCategoryVisitorResponse.builder()
            .defaultCategoryUid(effectiveDefaultUid)
            .categories(items)
            .build();
    }

    // ===== 测试邮件 / 短信 =====

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = "testEmail", description = "send test email notification")
    @Operation(summary = "Send test email", description = "Send a test email using the configured email provider")
    @PostMapping("/test-email")
    public ResponseEntity<?> testEmail(@RequestBody Map<String, String> body) {
        String emailProviderUid = body.get("emailProviderUid");
        String to = body.get("to");
        JsonResult<Boolean> result = ticketSettingsRestService.sendTestEmail(emailProviderUid, to);
        return ResponseEntity.ok(result);
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_SETTINGS, action = "testSms", description = "send test sms notification")
    @Operation(summary = "Send test SMS", description = "Send a test SMS using the configured SMS provider and template")
    @PostMapping("/test-sms")
    public ResponseEntity<?> testSms(@RequestBody Map<String, String> body) {
        String to = body.get("to");
        String country = body.get("country");
        String signName = body.get("signName");
        String templateCode = body.get("templateCode");
        JsonResult<Boolean> result = ticketSettingsRestService.sendTestSms(to, country, signName, templateCode);
        return ResponseEntity.ok(result);
    }

}