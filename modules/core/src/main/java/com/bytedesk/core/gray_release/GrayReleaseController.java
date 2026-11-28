/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-07 11:40:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:26:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.gray_release;

import java.time.ZonedDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.role.RolePermissions;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 灰度发布控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gray-release")
public class GrayReleaseController {

    private final GrayReleaseService grayReleaseService;
    private final GrayReleaseMetricsService metricsService;
    
    /**
     * 初始化功能的灰度发布
     */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Initialize Feature", description = "Initialize a feature for gray release")
    @ActionAnnotation(title = I18Consts.I18N_GRAY_RELEASE, action = I18Consts.I18N_ACTION_INIT, description = "init gray release")
    @PostMapping("/features/{feature}/init")
    public ResponseEntity<?> initializeFeature(@PathVariable String feature) {
        GrayReleaseFeature grayFeature = GrayReleaseFeature.getByCode(feature);
        if (grayFeature == null) {
            return ResponseEntity.badRequest().body("Invalid feature code");
        }
        
        grayReleaseService.initializeFeatureRollout(grayFeature);
        return ResponseEntity.ok().build();
    }

    /**
     * 调整灰度比例
     */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Adjust Rollout Percentage", description = "Adjust the rollout percentage of a feature")
    @ActionAnnotation(title = I18Consts.I18N_GRAY_RELEASE, action = I18Consts.I18N_ACTION_UPDATE, description = "update gray release")
    @PutMapping("/features/{feature}/rollout")
    public ResponseEntity<?> adjustRollout(
            @PathVariable String feature,
            @RequestParam int percentage) {
        
        if (percentage < 0 || percentage > 100) {
            return ResponseEntity.badRequest().body("Percentage must be between 0 and 100");
        }

        grayReleaseService.increaseRolloutPercentage(feature, percentage);
        return ResponseEntity.ok().build();
    }

    /**
     * 暂停灰度发布
     */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Pause Rollout", description = "Pause the rollout of a feature")
    @ActionAnnotation(title = I18Consts.I18N_GRAY_RELEASE, action = I18Consts.I18N_ACTION_UPDATE_STATUS, description = "update status of gray release")
    @PostMapping("/features/{feature}/pause")
    public ResponseEntity<?> pauseRollout(@PathVariable String feature) {
        grayReleaseService.pauseRollout(feature);
        return ResponseEntity.ok().build();
    }

    /**
     * 恢复灰度发布
     */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Resume Rollout", description = "Resume the rollout of a feature")
    @ActionAnnotation(title = I18Consts.I18N_GRAY_RELEASE, action = I18Consts.I18N_ACTION_UPDATE_STATUS, description = "update status of gray release")
    @PostMapping("/features/{feature}/resume")
    public ResponseEntity<?> resumeRollout(@PathVariable String feature) {
        grayReleaseService.resumeRollout(feature);
        return ResponseEntity.ok().build();
    }

    /**
     * 完成灰度发布
     */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Complete Rollout", description = "Complete the rollout and release the feature to all users")
    @ActionAnnotation(title = I18Consts.I18N_GRAY_RELEASE, action = I18Consts.I18N_ACTION_UPDATE, description = "update gray release")
    @PostMapping("/features/{feature}/complete")
    public ResponseEntity<?> completeRollout(@PathVariable String feature) {
        grayReleaseService.completeRollout(feature);
        return ResponseEntity.ok().build();
    }

    /**
     * 添加白名单用户
     */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Add User to Whitelist", description = "Add a user to the gray release whitelist")
    @ActionAnnotation(title = I18Consts.I18N_GRAY_RELEASE, action = I18Consts.I18N_ACTION_CREATE, description = "create gray release")
    @PostMapping("/features/{feature}/whitelist/{userUid}")
    public ResponseEntity<?> addToWhitelist(
            @PathVariable String feature,
            @PathVariable String userUid) {
        grayReleaseService.addToWhitelist(feature, userUid);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取功能状态
     */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get Feature Status", description = "Retrieve the rollout status of a feature")
    @ActionAnnotation(title = I18Consts.I18N_GRAY_RELEASE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query gray release")
    @GetMapping("/features/{feature}")
    public ResponseEntity<GrayReleaseStatus> getFeatureStatus(
            @PathVariable String feature) {
        return ResponseEntity.ok(grayReleaseService.getFeatureStatus(feature));
    }
    
    /**
     * 获取功能统计数据
     */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get Gray Release Statistics", description = "Retrieve gray release statistics")
    @ActionAnnotation(title = I18Consts.I18N_GRAY_RELEASE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query gray release")
    @GetMapping("/features/{feature}/stats")
    public ResponseEntity<GrayReleaseFeatureStatistics> getStats(
            @PathVariable String feature,
            @RequestParam(required = false) Integer hours) {
        
        ZonedDateTime end = BdDateUtils.now();
        ZonedDateTime start = end.minusHours(hours != null ? hours : 24);
        
        GrayReleaseFeature grayFeature = GrayReleaseFeature.getByCode(feature);
        if (grayFeature == null) {
            return ResponseEntity.badRequest().body(null);
        }

        GrayReleaseFeatureStatistics stats = metricsService.getFeatureStatistics(
            grayFeature, start, end);
        
        return ResponseEntity.ok(stats);
    }
}
