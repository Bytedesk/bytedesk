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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.service.visitor.VisitorPermissions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Visitor Custom Field Settings", description = "Configure visitor custom field definitions by organization")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/visitor/customFieldSettings")
public class VisitorCustomFieldSettingsRestController {

    private final VisitorCustomFieldSettingsRestService visitorCustomFieldSettingsRestService;

    @ActionAnnotation(title = I18Consts.I18N_VISITOR_CUSTOM_FIELD_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query visitor custom field settings by org")
    @Operation(summary = "Query Visitor Custom Field Definitions by Organization", description = "Retrieve custom field definitions by orgUid")
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_READ)
    @GetMapping("/query")
    public ResponseEntity<?> queryByOrg(@RequestParam("orgUid") String orgUid) {
        return ResponseEntity.ok(JsonResult.success(visitorCustomFieldSettingsRestService.queryByOrg(orgUid)));
    }

    @ActionAnnotation(title = I18Consts.I18N_VISITOR_CUSTOM_FIELD_SETTINGS, action = I18Consts.I18N_ACTION_UPDATE, description = "update visitor custom field settings by org")
    @Operation(summary = "Update Visitor Custom Field Definitions by Organization", description = "Update custom field definitions by orgUid")
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> updateByOrg(@RequestBody VisitorCustomFieldSettingsRequest request) {
        return ResponseEntity.ok(JsonResult.success(visitorCustomFieldSettingsRestService.updateByOrg(request.getOrgUid(), request.getCustomFieldList())));
    }
}
