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

import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
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

    private final CategoryRestService categoryRestService;

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

    /**
     * 查询当前组织级工单分类（level=ORGANIZATION、未删除、按 order 升序）。
     * visitor 端创建工单的分类下拉使用，与管理后台组织分类保持同源。
     * http://127.0.0.1:9003/visitor/api/v1/ticket/settings/orgs/{orgUid}/categories?type=TICKET_EXTERNAL
     */
    @GetMapping("/orgs/{orgUid}/categories")
    public ResponseEntity<?> getCategoriesByOrg(
            @PathVariable("orgUid") String orgUid,
            @RequestParam(value = "type", required = false) String type) {

        String categoryType = resolveCategoryType(type);
        List<CategoryEntity> categories = categoryRestService.findByOrgUidAndTypeAndLevelAndDeletedFalseOrderByOrderAsc(
                orgUid, categoryType, LevelEnum.ORGANIZATION.name());

        List<TicketCategoryVisitorItemResponse> items = categories.stream()
                .filter(category -> StringUtils.hasText(category.getUid()))
                .map(category -> TicketCategoryVisitorItemResponse.builder()
                        .uid(category.getUid())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(JsonResult.success(TicketCategoryVisitorResponse.builder()
                .categories(items)
                .build()));
    }

    /**
     * 兼容 TICKET_EXTERNAL / EXTERNAL 等写法，统一解析为组织级工单分类类型，默认外部工单分类
     */
    private String resolveCategoryType(String rawType) {
        if (!StringUtils.hasText(rawType)) {
            return CategoryTypeEnum.TICKET_EXTERNAL.name();
        }
        String normalized = rawType.trim().toUpperCase();
        if (TicketTypeEnum.INTERNAL.name().equals(normalized)
                || CategoryTypeEnum.TICKET_INTERNAL.name().equals(normalized)) {
            return CategoryTypeEnum.TICKET_INTERNAL.name();
        }
        return CategoryTypeEnum.TICKET_EXTERNAL.name();
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
                        .thenComparing(item -> item.getName(), Comparator.nullsLast((a, b) -> a.compareToIgnoreCase(b))))
                .map(this::toVisitorItem)
                .collect(Collectors.toList());

        String configuredDefaultUid = categorySettings.getDefaultCategoryUid();
        boolean defaultAvailable = items.stream()
            .anyMatch(item -> Objects.equals(item.getUid(), configuredDefaultUid));
        String effectiveDefaultUid = defaultAvailable
            ? configuredDefaultUid
            : items.stream().findFirst().map(item -> item.getUid()).orElse(null);

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
