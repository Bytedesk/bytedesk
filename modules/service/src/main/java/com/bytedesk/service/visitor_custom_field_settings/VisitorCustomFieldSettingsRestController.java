package com.bytedesk.service.visitor_custom_field_settings;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.service.visitor.VisitorPermissions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "访客扩展字段配置", description = "按组织配置访客扩展字段定义")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/visitor/customFieldSettings")
public class VisitorCustomFieldSettingsRestController {

    private final VisitorCustomFieldSettingsRestService visitorCustomFieldSettingsRestService;

    @ActionAnnotation(title = "访客扩展字段", action = "查询", description = "query visitor custom field settings by org")
    @Operation(summary = "查询组织下的访客扩展字段定义", description = "根据 orgUid 查询扩展字段定义列表")
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_READ)
    @GetMapping("/query")
    public ResponseEntity<?> queryByOrg(@RequestParam("orgUid") String orgUid) {
        return ResponseEntity.ok(JsonResult.success(visitorCustomFieldSettingsRestService.queryByOrg(orgUid)));
    }

    @ActionAnnotation(title = "访客扩展字段", action = "更新", description = "update visitor custom field settings by org")
    @Operation(summary = "更新组织下的访客扩展字段定义", description = "根据 orgUid 更新扩展字段定义列表")
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> updateByOrg(@RequestBody VisitorCustomFieldSettingsRequest request) {
        return ResponseEntity.ok(JsonResult.success(visitorCustomFieldSettingsRestService.updateByOrg(request.getOrgUid(), request.getCustomFieldList())));
    }
}
