/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-19 10:17:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 10:06:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.feature;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.role.RolePermissions;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/features")
public class FeatureRestController extends BaseRestController<FeatureRequest, FeatureRestService> {

    private final FeatureService featureService;

    @Operation(summary = "Query Features by Organization", description = "Retrieve features for the current organization")
    @ActionAnnotation(title = I18Consts.I18N_FEATURE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query feature")
    @Override
    public ResponseEntity<?> queryByOrg(FeatureRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Operation(summary = "Query Features by User", description = "Retrieve features for the current user")
    @ActionAnnotation(title = I18Consts.I18N_FEATURE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query feature")
    @Override
    public ResponseEntity<?> queryByUser(FeatureRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Operation(summary = "Create Features", description = "Create a new feature entry")
    @ActionAnnotation(title = I18Consts.I18N_FEATURE, action = I18Consts.I18N_ACTION_CREATE, description = "create feature")
    @Override
    public ResponseEntity<?> create(FeatureRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Operation(summary = "Update Features", description = "Update an existing feature entry")
    @ActionAnnotation(title = I18Consts.I18N_FEATURE, action = I18Consts.I18N_ACTION_UPDATE, description = "update feature")
    @Override
    public ResponseEntity<?> update(FeatureRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Operation(summary = "Delete Features", description = "Delete a feature entry")
    @ActionAnnotation(title = I18Consts.I18N_FEATURE, action = I18Consts.I18N_ACTION_DELETE, description = "delete feature")
    @Override
    public ResponseEntity<?> delete(FeatureRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get Enabled Features", description = "Retrieve features currently enabled")
    @ActionAnnotation(title = I18Consts.I18N_FEATURE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query feature")
    @GetMapping
    public ResponseEntity<List<FeatureEntity>> getEnabledFeatures() {
        return ResponseEntity.ok(featureService.getEnabledFeatures());
    }

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get Features by Module", description = "Retrieve features grouped by module name")
    @ActionAnnotation(title = I18Consts.I18N_FEATURE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query feature")
    @GetMapping("/module/{moduleName}")
    public ResponseEntity<List<FeatureEntity>> getFeaturesByModule(
            @PathVariable String moduleName) {
        return ResponseEntity.ok(featureService.getFeaturesByModule(moduleName));
    }

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get Module Feature Statistics", description = "Retrieve feature statistics per module")
    @ActionAnnotation(title = I18Consts.I18N_FEATURE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query feature")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getModuleFeatureStats() {
        return ResponseEntity.ok(featureService.getModuleFeatureStats());
    }

    @Operation(summary = "Update Feature Status", description = "Enable or disable a feature")
    @ActionAnnotation(title = I18Consts.I18N_FEATURE, action = I18Consts.I18N_ACTION_UPDATE_STATUS, description = "update status of feature")
    @PutMapping("/{code}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateFeatureStatus(
            @PathVariable String code,
            @RequestParam boolean enabled) {
        featureService.updateFeatureStatus(code, enabled);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update Feature Config", description = "Update the configuration of a feature")
    @ActionAnnotation(title = I18Consts.I18N_FEATURE, action = I18Consts.I18N_ACTION_UPDATE, description = "update feature")
    @PutMapping("/{code}/config") 
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateFeatureConfig(
            @PathVariable String code,
            @RequestBody Map<String, Object> config) {
        featureService.updateFeatureConfig(code, config);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Export Features", description = "Export features to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_FEATURE, action = I18Consts.I18N_ACTION_EXPORT, description = "export feature")
    @Override
    public Object export(FeatureRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "Query Features by UID", description = "Retrieve a specific feature entry by its unique identifier")
    @ActionAnnotation(title = I18Consts.I18N_FEATURE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query feature")
    @Override
    public ResponseEntity<?> queryByUid(FeatureRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

} 