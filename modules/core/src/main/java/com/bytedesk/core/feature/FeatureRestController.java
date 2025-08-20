/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-19 10:17:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 23:13:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.base.BaseRestController;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/features")
public class FeatureRestController extends BaseRestController<FeatureRequest, FeatureRestService> {

    @Autowired
    private FeatureService featureService;

    @Override
    public ResponseEntity<?> queryByOrg(FeatureRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public ResponseEntity<?> queryByUser(FeatureRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public ResponseEntity<?> create(FeatureRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ResponseEntity<?> update(FeatureRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseEntity<?> delete(FeatureRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }


    @GetMapping
    public ResponseEntity<List<FeatureEntity>> getEnabledFeatures() {
        return ResponseEntity.ok(featureService.getEnabledFeatures());
    }

    @GetMapping("/module/{moduleName}")
    public ResponseEntity<List<FeatureEntity>> getFeaturesByModule(
            @PathVariable String moduleName) {
        return ResponseEntity.ok(featureService.getFeaturesByModule(moduleName));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getModuleFeatureStats() {
        return ResponseEntity.ok(featureService.getModuleFeatureStats());
    }

    @PutMapping("/{code}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateFeatureStatus(
            @PathVariable String code,
            @RequestParam boolean enabled) {
        featureService.updateFeatureStatus(code, enabled);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{code}/config") 
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateFeatureConfig(
            @PathVariable String code,
            @RequestBody Map<String, Object> config) {
        featureService.updateFeatureConfig(code, config);
        return ResponseEntity.ok().build();
    }

    @Override
    public Object export(FeatureRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(FeatureRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

} 