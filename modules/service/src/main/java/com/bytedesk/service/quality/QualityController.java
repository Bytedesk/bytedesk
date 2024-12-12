/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-09 11:08:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-09 11:19:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.quality;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.service.quality.dto.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/quality")
public class QualityController {

    @Autowired
    private QualityService qualityService;

    // 质检管理
    @PostMapping("/inspections")
    @PreAuthorize("hasRole('QUALITY_INSPECTOR')")
    public ResponseEntity<QualityInspectionEntity> createInspection(
            @RequestParam String threadUid,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(qualityService.createInspection(threadUid, userDetails.getUsername()));
    }

    @PutMapping("/inspections/{inspectionId}")
    @PreAuthorize("hasRole('QUALITY_INSPECTOR')")
    public ResponseEntity<QualityInspectionEntity> updateInspection(
            @PathVariable Long inspectionId,
            @Valid @RequestBody QualityInspectionDTO dto) {
        return ResponseEntity.ok(qualityService.updateInspection(inspectionId, dto));
    }

    @DeleteMapping("/inspections/{inspectionId}")
    @PreAuthorize("hasRole('QUALITY_ADMIN')")
    public ResponseEntity<Void> deleteInspection(@PathVariable Long inspectionId) {
        qualityService.deleteInspection(inspectionId);
        return ResponseEntity.ok().build();
    }

    // 质检查询
    @GetMapping("/inspections")
    public ResponseEntity<Page<QualityInspectionEntity>> getInspections(Pageable pageable) {
        return ResponseEntity.ok(qualityService.getInspections(pageable));
    }

    @GetMapping("/inspections/agent/{agentUid}")
    public ResponseEntity<Page<QualityInspectionEntity>> getInspectionsByAgent(
            @PathVariable String agentUid,
            Pageable pageable) {
        return ResponseEntity.ok(qualityService.getInspectionsByAgent(agentUid, pageable));
    }

    // 质检统计
    @GetMapping("/stats/agent/{agentUid}/score")
    public ResponseEntity<Double> getAverageScore(
            @PathVariable String agentUid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(qualityService.getAverageScore(agentUid, startTime, endTime));
    }

    @GetMapping("/stats/agent/{agentUid}/distribution")
    public ResponseEntity<Map<String, Double>> getScoreDistribution(@PathVariable String agentUid) {
        return ResponseEntity.ok(qualityService.getScoreDistribution(agentUid));
    }

    // 规则管理
    @PostMapping("/rules")
    @PreAuthorize("hasRole('QUALITY_ADMIN')")
    public ResponseEntity<QualityRuleEntity> createRule(@Valid @RequestBody QualityRuleDTO dto) {
        return ResponseEntity.ok(qualityService.createRule(dto));
    }

    @PutMapping("/rules/{ruleId}")
    @PreAuthorize("hasRole('QUALITY_ADMIN')")
    public ResponseEntity<QualityRuleEntity> updateRule(
            @PathVariable Long ruleId,
            @Valid @RequestBody QualityRuleDTO dto) {
        return ResponseEntity.ok(qualityService.updateRule(ruleId, dto));
    }

    @DeleteMapping("/rules/{ruleId}")
    @PreAuthorize("hasRole('QUALITY_ADMIN')")
    public ResponseEntity<Void> deleteRule(@PathVariable Long ruleId) {
        qualityService.deleteRule(ruleId);
        return ResponseEntity.ok().build();
    }
} 