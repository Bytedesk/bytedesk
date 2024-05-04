/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 11:33:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.organization;

import java.util.Optional;

import org.springframework.data.domain.Page;
// import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
// @Slf4j
@AllArgsConstructor
// @RepositoryRestController("/v1/org")
@RestController
@RequestMapping("/api/v1/org")
@Tag(name = "organization - 组织", description = "organization apis")
public class OrganizationController {

    private final OrganizationService organizationService;

    /**
     * query user organizations
     *
     * @return json
     */
    @GetMapping("/query")
    public ResponseEntity<?> query(OrganizationRequest pageParam) {
        //
        Page<OrganizationResponse> orgPage = organizationService.query(pageParam);
        // //
        return ResponseEntity.ok(JsonResult.success(orgPage));
    }

    /**
     * 
     * @param request
     * @return
     */
    @GetMapping("/uid")
    public ResponseEntity<?> queryByUid(OrganizationRequest request) {
        //
        Optional<Organization> org = organizationService.findByUid(request.getUid());
        if (!org.isPresent()) {
            return ResponseEntity.ok(JsonResult.error("组织不存在"));
        }
        //
        return ResponseEntity.ok(JsonResult.success(organizationService.convertToOrganizationResponse(org.get())));
    }

    /** user join organization by oid */
    @GetMapping("/join")
    public ResponseEntity<?> join(OrganizationRequest request) {
        // TODO: check if user is already in the organization
        //
        return ResponseEntity.ok(JsonResult.success());
    }

}
