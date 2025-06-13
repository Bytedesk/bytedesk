/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-08 17:12:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group_notice;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/group/notice")
@AllArgsConstructor
public class GroupNoticeRestController extends BaseRestController<GroupNoticeRequest> {

    private final GroupNoticeRestService groupNoticeService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(GroupNoticeRequest request) {
        
        Page<GroupNoticeResponse> groupNotices = groupNoticeService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(groupNotices));
    }

    @Override
    public ResponseEntity<?> queryByUser(GroupNoticeRequest request) {
        
        Page<GroupNoticeResponse> groupNotices = groupNoticeService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(groupNotices));
    }

    @Override
    public ResponseEntity<?> create(GroupNoticeRequest request) {
        
        GroupNoticeResponse group_notice = groupNoticeService.create(request);

        return ResponseEntity.ok(JsonResult.success(group_notice));
    }

    @Override
    public ResponseEntity<?> update(GroupNoticeRequest request) {
        
        GroupNoticeResponse group_notice = groupNoticeService.update(request);

        return ResponseEntity.ok(JsonResult.success(group_notice));
    }

    @Override
    public ResponseEntity<?> delete(GroupNoticeRequest request) {
        
        groupNoticeService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(GroupNoticeRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(GroupNoticeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}