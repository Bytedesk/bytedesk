/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:22:48
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
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/group/notice")
@AllArgsConstructor
@Tag(name = "Group Notice Management", description = "Group notice management APIs for managing announcements within groups")
public class GroupNoticeRestController extends BaseRestController<GroupNoticeRequest, GroupNoticeRestService> {

    private final GroupNoticeRestService groupNoticeService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = I18Consts.I18N_GROUP_NOTICE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query group notice")
    @Operation(summary = "Query Group Notices by Organization", description = "Retrieve group notices for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(GroupNoticeRequest request) {
        
        Page<GroupNoticeResponse> groupNotices = groupNoticeService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(groupNotices));
    }

    @ActionAnnotation(title = I18Consts.I18N_GROUP_NOTICE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query group notice")
    @Operation(summary = "Query Group Notices by User", description = "Retrieve group notices for the current user")
    @Override
    public ResponseEntity<?> queryByUser(GroupNoticeRequest request) {
        
        Page<GroupNoticeResponse> groupNotices = groupNoticeService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(groupNotices));
    }

    @ActionAnnotation(title = I18Consts.I18N_GROUP_NOTICE, action = I18Consts.I18N_ACTION_CREATE, description = "create group notice")
    @Operation(summary = "Create Group Notice", description = "Create a new group notice")
    @Override
    public ResponseEntity<?> create(GroupNoticeRequest request) {
        
        GroupNoticeResponse group_notice = groupNoticeService.create(request);

        return ResponseEntity.ok(JsonResult.success(group_notice));
    }

    @ActionAnnotation(title = I18Consts.I18N_GROUP_NOTICE, action = I18Consts.I18N_ACTION_UPDATE, description = "update group notice")
    @Operation(summary = "Update Group Notice", description = "Update an existing group notice")
    @Override
    public ResponseEntity<?> update(GroupNoticeRequest request) {
        
        GroupNoticeResponse group_notice = groupNoticeService.update(request);

        return ResponseEntity.ok(JsonResult.success(group_notice));
    }

    @ActionAnnotation(title = I18Consts.I18N_GROUP_NOTICE, action = I18Consts.I18N_ACTION_DELETE, description = "delete group notice")
    @Operation(summary = "Delete Group Notice", description = "Delete a group notice")
    @Override
    public ResponseEntity<?> delete(GroupNoticeRequest request) {
        
        groupNoticeService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Group Notices", description = "Export group notices to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_GROUP_NOTICE, action = I18Consts.I18N_ACTION_EXPORT, description = "export group notice")
    @Override
    public Object export(GroupNoticeRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "Query Group Notices by UID", description = "Retrieve a specific group notice entry by its unique identifier")
    @ActionAnnotation(title = I18Consts.I18N_GROUP_NOTICE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query group notice")
    @Override
    public ResponseEntity<?> queryByUid(GroupNoticeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}