/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 20:39:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Description;

import com.bytedesk.kbase.elastic.KbaseElasticIndexUpgradeService;
import com.bytedesk.kbase.translation.KbaseTranslationBackfillRequest;
import com.bytedesk.kbase.translation.KbaseTranslationIndexBackfillService;

@Tag(name = "Knowledge Base Management", description = "Knowledge base management APIs")
@RestController
@RequestMapping("/api/v1/kbase")
@AllArgsConstructor
@Description("Knowledge Base Controller - Knowledge base content management and organization APIs")
public class KbaseRestController extends BaseRestController<KbaseRequest, KbaseRestService> {

    private final KbaseRestService kbaseRestService;

    private final KbaseElasticIndexUpgradeService kbaseElasticIndexUpgradeService;

    private final KbaseTranslationIndexBackfillService kbaseTranslationIndexBackfillService;

    @Operation(summary = "Query Knowledge Bases by Organization", description = "Query the list of knowledge bases by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseResponse.class)))
    @PreAuthorize(KbasePermissions.HAS_KBASE_READ)
    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query kbase by org")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(KbaseRequest request) {

        Page<KbaseResponse> page = kbaseRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Knowledge Bases by User", description = "Query the list of knowledge bases by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseResponse.class)))
    @PreAuthorize(KbasePermissions.HAS_KBASE_READ)
    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query kbase by user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(KbaseRequest request) {
        
        Page<KbaseResponse> page = kbaseRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Knowledge Base by UID", description = "Query knowledge base details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseResponse.class)))
    @PreAuthorize(KbasePermissions.HAS_KBASE_READ)
    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query kbase by uid")
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(KbaseRequest request) {

        KbaseResponse kbase = kbaseRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(kbase));
    }

    @Operation(summary = "Create Knowledge Base", description = "Create a new knowledge base")
    @ApiResponse(responseCode = "200", description = "Creation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseResponse.class)))
    @PreAuthorize(KbasePermissions.HAS_KBASE_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_CREATE, description = "create kbase")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody KbaseRequest request) {

        KbaseResponse kbase = kbaseRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(kbase));
    }

    @Operation(summary = "Update Knowledge Base", description = "Update knowledge base information")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseResponse.class)))
    @PreAuthorize(KbasePermissions.HAS_KBASE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_UPDATE, description = "update kbase")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody KbaseRequest request) {

        KbaseResponse kbase = kbaseRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(kbase));
    }

    @Operation(summary = "Delete Knowledge Base", description = "Delete the specified knowledge base")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @PreAuthorize(KbasePermissions.HAS_KBASE_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_DELETE, description = "delete kbase")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody KbaseRequest request) {

        kbaseRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @Operation(summary = "Export Knowledge Bases", description = "Export knowledge base data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @PreAuthorize(KbasePermissions.HAS_KBASE_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_EXPORT, description = "export kbase")
    @GetMapping("/export")
    @Override
    public Object export(KbaseRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "Check and upgrade Elasticsearch IK mappings", description = "Check knowledge base Elasticsearch index mappings and automatically rebuild/reindex when IK analyzer mapping is outdated")
    @ApiResponse(responseCode = "200", description = "Check successful")
    @PreAuthorize(KbasePermissions.HAS_KBASE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_UPDATE, description = "check and upgrade kbase elasticsearch mappings")
    @PostMapping("/elastic/check-upgrade")
    public ResponseEntity<?> checkAndUpgradeElasticMappings() {
        return ResponseEntity.ok(JsonResult.success(kbaseElasticIndexUpgradeService.checkAndUpgradeIkIndexes()));
    }

    @Operation(summary = "Backfill translated indexes", description = "Rebuild translated fulltext/vector indexes from existing successful translation records")
    @ApiResponse(responseCode = "200", description = "Backfill successful")
    @PreAuthorize(KbasePermissions.HAS_KBASE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_UPDATE, description = "backfill translated indexes")
    @PostMapping("/translation/backfill-indexes")
    public ResponseEntity<?> backfillTranslatedIndexes(@RequestBody KbaseTranslationBackfillRequest request) {
        return ResponseEntity.ok(JsonResult.success(kbaseTranslationIndexBackfillService.backfill(request)));
    }

}
