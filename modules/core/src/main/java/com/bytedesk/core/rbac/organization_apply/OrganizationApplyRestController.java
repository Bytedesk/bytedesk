/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:23:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization_apply;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/org/apply")
@AllArgsConstructor
public class OrganizationApplyRestController extends BaseRestController<OrganizationApplyRequest, OrganizationApplyRestService> {

    private final OrganizationApplyRestService organizationRestService;

    @ActionAnnotation(title = "加入组织申请", action = "导出", description = "export organizationApply")
    @Override
    // @PreAuthorize("hasAuthority('organizationApply_EXPORT')")
    @GetMapping("/export")
    public Object export(OrganizationApplyRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            organizationRestService,
            OrganizationApplyExcel.class,
            "加入组织申请",
            "organizationApply"
        );
    }
}