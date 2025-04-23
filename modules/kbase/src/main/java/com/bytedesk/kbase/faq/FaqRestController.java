/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 09:18:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

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
@RequestMapping("/api/v1/faq")
@AllArgsConstructor
public class FaqRestController extends BaseRestController<FaqRequest> {

    private final FaqRestService faqRestService;

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(FaqRequest request) {

        Page<FaqResponse> page = faqRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUser(FaqRequest request) {
        
        Page<FaqResponse> page = faqRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUid(FaqRequest request) {
        
        FaqResponse faq = faqRestService.queryByUid(request);
        if (faq == null) {
            return ResponseEntity.ok(JsonResult.error("faq not found"));
        }
        return ResponseEntity.ok(JsonResult.success(faq));
    }

    @ActionAnnotation(title = "常见问题", action = "新建", description = "create faq")
    @Override
    @PreAuthorize("hasAuthority('KBASE_CREATE')")
    public ResponseEntity<?> create(@RequestBody FaqRequest request) {

        FaqResponse Faq = faqRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @ActionAnnotation(title = "常见问题", action = "更新", description = "update faq")
    @Override
    @PreAuthorize("hasAuthority('KBASE_UPDATE')")
    public ResponseEntity<?> update(@RequestBody FaqRequest request) {

        FaqResponse Faq = faqRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @ActionAnnotation(title = "常见问题", action = "删除", description = "delete faq")
    @Override
    @PreAuthorize("hasAuthority('KBASE_DELETE')")
    public ResponseEntity<?> delete(@RequestBody FaqRequest request) {

        faqRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    // deleteAll
    @ActionAnnotation(title = "常见问题", action = "删除所有", description = "delete faq all")
    @PreAuthorize("hasAuthority('KBASE_DELETE')")
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody FaqRequest request) {

        faqRestService.delateAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable faq
    @ActionAnnotation(title = "常见问题", action = "启用", description = "enable faq")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody FaqRequest request) {

        FaqResponse faqResponse = faqRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(faqResponse));
    }

    @ActionAnnotation(title = "常见问题", action = "导出", description = "export faq")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    public Object export(FaqRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            faqRestService,
            FaqExcel.class,
            "常见问题",
            "faq"
        );
    }

}
