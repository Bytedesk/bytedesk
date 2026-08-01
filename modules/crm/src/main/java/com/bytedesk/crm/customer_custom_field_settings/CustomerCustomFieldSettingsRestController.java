package com.bytedesk.crm.customer_custom_field_settings;

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
import com.bytedesk.crm.customer.CustomerPermissions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Customer Custom Field Settings", description = "Configure customer custom field definitions by organization")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/customer/customFieldSettings")
public class CustomerCustomFieldSettingsRestController {

    private final CustomerCustomFieldSettingsRestService customerCustomFieldSettingsRestService;

    @ActionAnnotation(title = I18Consts.I18N_CUSTOMER_CUSTOM_FIELD_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query customer custom field settings by org")
    @Operation(summary = "Query Customer Custom Field Definitions by Organization", description = "Retrieve custom field definitions by orgUid")
    @PreAuthorize(CustomerPermissions.HAS_CUSTOMER_READ)
    @GetMapping("/query")
    public ResponseEntity<?> queryByOrg(@RequestParam("orgUid") String orgUid) {
        return ResponseEntity.ok(JsonResult.success(customerCustomFieldSettingsRestService.queryByOrg(orgUid)));
    }

    @ActionAnnotation(title = I18Consts.I18N_CUSTOMER_CUSTOM_FIELD_SETTINGS, action = I18Consts.I18N_ACTION_UPDATE, description = "update customer custom field settings by org")
    @Operation(summary = "Update Customer Custom Field Definitions by Organization", description = "Update custom field definitions by orgUid")
    @PreAuthorize(CustomerPermissions.HAS_CUSTOMER_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> updateByOrg(@RequestBody CustomerCustomFieldSettingsRequest request) {
        return ResponseEntity.ok(JsonResult.success(
                customerCustomFieldSettingsRestService.updateByOrg(request.getOrgUid(), request.getCustomFieldList())));
    }
}
