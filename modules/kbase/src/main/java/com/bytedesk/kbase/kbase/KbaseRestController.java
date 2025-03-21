/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-21 17:42:12
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

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/kbase")
@AllArgsConstructor
public class KbaseRestController extends BaseRestController<KbaseRequest> {

    private final KbaseRestService knowledgeService;

    @PreAuthorize("hasAnyRole('SUPER', 'ADMIN')")
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(KbaseRequest request) {

        Page<KbaseResponse> page = knowledgeService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @Override
    public ResponseEntity<?> queryByUser(KbaseRequest request) {
        
        Page<KbaseResponse> page = knowledgeService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @Override
    public ResponseEntity<?> queryByUid(KbaseRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    // query detail
    @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @GetMapping("/query/detail")
    public ResponseEntity<?> queryDetail(KbaseRequest request) {

        KbaseResponse response = knowledgeService.queryDetail(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("not found"));
        }

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize("hasAuthority('KBASE_CREATE')")
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody KbaseRequest request) {

        KbaseResponse Faq = knowledgeService.create(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @PreAuthorize("hasAuthority('KBASE_UPDATE')")
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody KbaseRequest request) {

        KbaseResponse Faq = knowledgeService.update(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @PreAuthorize("hasAuthority('KBASE_DELETE')")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody KbaseRequest request) {

        knowledgeService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    @Override
    public Object export(KbaseRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    

}
