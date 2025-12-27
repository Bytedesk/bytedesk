package com.bytedesk.ticket.ticket_settings;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsResponse;
import com.bytedesk.ticket.ticket_settings_category.TicketCategoryItemResponse;
import com.bytedesk.ticket.ticket_settings_category.TicketCategorySettingsResponse;
import com.bytedesk.ticket.ticket_settings_category.TicketCategoryVisitorItemResponse;
import com.bytedesk.ticket.ticket_settings_category.TicketCategoryVisitorResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Visitor-facing ticket settings controller that exposes a minimal set of
 * metadata required by the public ticket submission experience.
 */
@Slf4j
@RestController
@RequestMapping("/visitor/api/v1/ticket/settings")
@AllArgsConstructor
public class TicketSettingsRestControllerVisitor {

    private final TicketSettingsRestService ticketSettingsRestService;

    /**
     * 与管理端对齐：按 orgUid + workgroupUid 获取 TicketSettings（不存在则返回默认模板）。
     * visitor 端用于工单提交页面：拿到分类/表单/basic/process 等最小元数据。
     */
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

    @GetMapping("/orgs/{orgUid}/workgroups/{workgroupUid}/categories")
    public ResponseEntity<?> getCategoriesByWorkgroup(
            @PathVariable("orgUid") String orgUid,
            @PathVariable("workgroupUid") String workgroupUid) {

        TicketSettingsResponse settings = ticketSettingsRestService.getOrDefaultByWorkgroup(orgUid, workgroupUid);

        TicketCategoryVisitorResponse response = toVisitorResponse(settings != null ? settings.getCategorySettings() : null);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @GetMapping("/orgs/{orgUid}/workgroups/{workgroupUid}/basic")
    public ResponseEntity<?> getBasicSettingsByWorkgroup(
            @PathVariable("orgUid") String orgUid,
            @PathVariable("workgroupUid") String workgroupUid) {

        TicketBasicSettingsResponse response = resolveBasicSettings(orgUid, workgroupUid);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    private TicketBasicSettingsResponse resolveBasicSettings(String orgUid, String workgroupUid) {
        TicketSettingsResponse settings = ticketSettingsRestService.getOrDefaultByWorkgroup(orgUid, workgroupUid);
        if (settings == null) {
            return null;
        }
        return settings.getBasicSettings() != null
                ? settings.getBasicSettings()
                : settings.getDraftBasicSettings();
    }

    private TicketCategoryVisitorResponse toVisitorResponse(TicketCategorySettingsResponse categorySettings) {
        if (categorySettings == null) {
            return TicketCategoryVisitorResponse.empty();
        }

        List<TicketCategoryVisitorItemResponse> items = categorySettings.getItems().stream()
                .filter(item -> Boolean.TRUE.equals(item.getEnabled()))
                .sorted(Comparator.comparing(this::orderIndexOrDefault)
                        .thenComparing(TicketCategoryItemResponse::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(this::toVisitorItem)
                .collect(Collectors.toList());

        String configuredDefaultUid = categorySettings.getDefaultCategoryUid();
        boolean defaultAvailable = items.stream()
            .anyMatch(item -> Objects.equals(item.getUid(), configuredDefaultUid));
        String effectiveDefaultUid = defaultAvailable
            ? configuredDefaultUid
            : items.stream().findFirst().map(TicketCategoryVisitorItemResponse::getUid).orElse(null);

        return TicketCategoryVisitorResponse.builder()
            .defaultCategoryUid(effectiveDefaultUid)
                .categories(items)
                .build();
    }

    private TicketCategoryVisitorItemResponse toVisitorItem(TicketCategoryItemResponse item) {
        return TicketCategoryVisitorItemResponse.builder()
                .uid(item.getUid())
                .name(item.getName())
                .description(item.getDescription())
                .build();
    }

    private Integer orderIndexOrDefault(TicketCategoryItemResponse item) {
        return item.getOrderIndex() != null ? item.getOrderIndex() : Integer.MAX_VALUE;
    }
}
