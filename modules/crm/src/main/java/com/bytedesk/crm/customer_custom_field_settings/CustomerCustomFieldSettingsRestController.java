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
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.crm.customer.CustomerPermissions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "客户扩展字段配置", description = "按组织配置客户扩展字段定义")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/customer/customFieldSettings")
public class CustomerCustomFieldSettingsRestController {

    private final CustomerCustomFieldSettingsRestService customerCustomFieldSettingsRestService;

    @ActionAnnotation(title = "客户扩展字段", action = "查询", description = "query customer custom field settings by org")
    @Operation(summary = "查询组织下的客户扩展字段定义", description = "根据 orgUid 查询扩展字段定义列表")
    @PreAuthorize(CustomerPermissions.HAS_CUSTOMER_READ)
    @GetMapping("/query")
    public ResponseEntity<?> queryByOrg(@RequestParam("orgUid") String orgUid) {
        return ResponseEntity.ok(JsonResult.success(customerCustomFieldSettingsRestService.queryByOrg(orgUid)));
    }

    @ActionAnnotation(title = "客户扩展字段", action = "更新", description = "update customer custom field settings by org")
    @Operation(summary = "更新组织下的客户扩展字段定义", description = "根据 orgUid 更新扩展字段定义列表")
    @PreAuthorize(CustomerPermissions.HAS_CUSTOMER_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> updateByOrg(@RequestBody CustomerCustomFieldSettingsRequest request) {
        return ResponseEntity.ok(JsonResult.success(
                customerCustomFieldSettingsRestService.updateByOrg(request.getOrgUid(), request.getCustomFieldList())));
    }
}
