/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-01 16:09:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/email")
@AllArgsConstructor
public class EmailRestController extends BaseRestController<EmailRequest, EmailRestService> {

    private final EmailRestService emailRestService;
    
    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = I18Consts.I18N_EMAIL, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query email by org")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(EmailRequest request) {
        
        Page<EmailResponse> emails = emailRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(emails));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMAIL, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query email by user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(EmailRequest request) {
        
        Page<EmailResponse> emails = emailRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(emails));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMAIL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query email by uid")
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(EmailRequest request) {
        
        EmailResponse email = emailRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(email));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMAIL, action = I18Consts.I18N_ACTION_CREATE, description = "create email")
    @Override
    // @PreAuthorize("hasAuthority('EMAIL_CREATE')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody EmailRequest request) {
        
        EmailResponse email = emailRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(email));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMAIL, action = I18Consts.I18N_ACTION_UPDATE, description = "update email")
    @Override
    // @PreAuthorize("hasAuthority('EMAIL_UPDATE')")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody EmailRequest request) {
        
        EmailResponse email = emailRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(email));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMAIL, action = I18Consts.I18N_ACTION_DELETE, description = "delete email")
    @Override
    // @PreAuthorize("hasAuthority('EMAIL_DELETE')")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody EmailRequest request) {
        
        emailRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_EMAIL, action = I18Consts.I18N_ACTION_EXPORT, description = "export email")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(EmailRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            emailRestService,
            EmailExcel.class,
            "Email",
            "email"
        );
    }

    

    

}