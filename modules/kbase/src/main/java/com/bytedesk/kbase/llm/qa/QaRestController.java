/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 22:37:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

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
import com.bytedesk.core.utils.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/llm/qa")
@AllArgsConstructor
public class QaRestController extends BaseRestController<QaRequest> {

    private final QaRestService qaRestService;

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(QaRequest request) {

        Page<QaResponse> page = qaRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUser(QaRequest request) {
        
        Page<QaResponse> page = qaRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUid(QaRequest request) {
        
        QaResponse qa = qaRestService.queryByUid(request);
        if (qa == null) {
            return ResponseEntity.ok(JsonResult.error("qa not found"));
        }
        return ResponseEntity.ok(JsonResult.success(qa));
    }

    @ActionAnnotation(title = "问答对", action = "新建", description = "create qa")
    @Override
    @PreAuthorize("hasAuthority('KBASE_CREATE')")
    public ResponseEntity<?> create(@RequestBody QaRequest request) {

        QaResponse Qa = qaRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(Qa));
    }

    @ActionAnnotation(title = "问答对", action = "更新", description = "update qa")
    @Override
    @PreAuthorize("hasAuthority('KBASE_UPDATE')")
    public ResponseEntity<?> update(@RequestBody QaRequest request) {

        QaResponse Qa = qaRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(Qa));
    }

    @ActionAnnotation(title = "问答对", action = "删除", description = "delete qa")
    @Override
    @PreAuthorize("hasAuthority('KBASE_DELETE')")
    public ResponseEntity<?> delete(@RequestBody QaRequest request) {

        qaRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "问答对", action = "删除所有", description = "delete qa all")
    @PreAuthorize("hasAuthority('KBASE_DELETE')")
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody QaRequest request) {

        qaRestService.delateAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable qa
    @ActionAnnotation(title = "问答对", action = "启用", description = "enable qa")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody QaRequest request) {

        QaResponse qaResponse = qaRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(qaResponse));
    }

    @ActionAnnotation(title = "问答对", action = "导出", description = "export qa")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    public Object export(QaRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            qaRestService,
            QaExcel.class,
            "问答对",
            "qa"
        );
    }

}
