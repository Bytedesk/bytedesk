/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 09:36:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/visitor")
public class VisitorRestController extends BaseRestController<VisitorRequest> {

    private final VisitorRestService visitorService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(VisitorRequest request) {

        Page<VisitorResponse> page = visitorService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(VisitorRequest visitorRequest) {
        //
        Page<VisitorResponse> visitorResponse = visitorService.queryByUser(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @Override
    public ResponseEntity<?> queryByUid(VisitorRequest request) {
        
        VisitorResponse visitorResponse = visitorService.queryByUid(request);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @Override
    public ResponseEntity<?> create(VisitorRequest request) {
        
        VisitorResponse visitorResponse = visitorService.create(request);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @Override
    public ResponseEntity<?> update(@RequestBody VisitorRequest visitorRequest) {

        VisitorResponse visitorResponse = visitorService.update(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    // update tagList
    @PostMapping("/update/tagList")
    public ResponseEntity<?> updateTagList(@RequestBody VisitorRequest visitorRequest) {

        VisitorResponse visitorResponse = visitorService.updateTagList(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @Override
    public ResponseEntity<?> delete(@RequestBody VisitorRequest visitorRequest) {

        visitorService.delete(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success("delete success"));
    }

    @Override
    public Object export(VisitorRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            visitorService,
            VisitorExcel.class,
            "访客",
            "visitor"
        );
    }

    

}
