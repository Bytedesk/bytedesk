/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-10 16:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 16:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.kbase.translation;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "KB Translation Management", description = "Knowledge base translation management APIs")
@RestController
@RequestMapping("/api/v1/kbase/translation")
@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER')")
public class KbaseTranslationRestController extends BaseRestController<KbaseTranslationRequest, KbaseTranslationRestService> {

    private final KbaseTranslationRestService kbaseTranslationRestService;

    public KbaseTranslationRestController(KbaseTranslationRestService kbaseTranslationRestService) {
        this.kbaseTranslationRestService = kbaseTranslationRestService;
    }

    @Operation(summary = "Query translations by organization")
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(KbaseTranslationRequest request) {
        Page<KbaseTranslationResponse> page = kbaseTranslationRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query translations by user")
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(KbaseTranslationRequest request) {
        Page<KbaseTranslationResponse> page = kbaseTranslationRestService.queryByUser(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query translation by UID")
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(KbaseTranslationRequest request) {
        KbaseTranslationResponse response = kbaseTranslationRestService.queryByUid(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_CREATE, description = "create kbase translation")
    @Operation(summary = "Create translation")
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody KbaseTranslationRequest request) {
        KbaseTranslationResponse response = kbaseTranslationRestService.create(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_UPDATE, description = "update kbase translation")
    @Operation(summary = "Update translation")
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody KbaseTranslationRequest request) {
        KbaseTranslationResponse response = kbaseTranslationRestService.update(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_DELETE, description = "delete kbase translation")
    @Operation(summary = "Delete translation")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody KbaseTranslationRequest request) {
        kbaseTranslationRestService.delete(request);
        return ResponseEntity.ok(JsonResult.success());
    }
}
