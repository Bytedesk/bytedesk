/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-07 15:59:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.group;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.team.member.MemberProtobuf;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "group", description = "group apis")
public class GroupRestController extends BaseRestController<GroupRequest> {
    
    private final GroupRestService groupRestService;
    
    @Override
    public ResponseEntity<?> queryByOrg(GroupRequest request) {
        
        Page<GroupResponse> page = groupRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(GroupRequest request) {

        Page<GroupResponse> page = groupRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }
    
    @Override
    public ResponseEntity<?> queryByUid(GroupRequest request) {
        
        GroupResponse group = groupRestService.queryByUid(request);
        
        return ResponseEntity.ok(JsonResult.success(group));
    }
    
    /**
     * 分页查询群组成员
     * @param request 包含群组uid和分页参数
     * @return 成员列表分页结果
     */
    @GetMapping("/members")
    public ResponseEntity<?> queryMembers(GroupRequest request) {
        
        Page<MemberProtobuf> page = groupRestService.queryGroupMembers(request);
        
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = "群组", action = "新建", description = "create group")
    @Override
    public ResponseEntity<?> create(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @ActionAnnotation(title = "群组", action = "更新", description = "update group")
    @Override
    public ResponseEntity<?> update(@RequestBody GroupRequest request) {

        GroupResponse group = groupRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @ActionAnnotation(title = "群组", action = "invite", description = "invite group")
    @PostMapping("/invite")
    public ResponseEntity<?> invite(@RequestBody GroupRequest request) {
        // TODO: 待完善
        // groupService.invite(request);
        return ResponseEntity.ok(JsonResult.success());
    }
    
    @ActionAnnotation(title = "群组", action = "join", description = "join group")
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody GroupRequest request) {
        // TODO: 待完善
        // groupService.join(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "群组", action = "kick", description = "kick group")
    @PostMapping("/kick")
    public ResponseEntity<?> kick(@RequestBody GroupRequest request) {
        // TODO: 待完善, 将用户踢出/移出群组
        // groupService.kick(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "群组", action = "leave", description = "leave group")
    @PostMapping("/leave")
    public ResponseEntity<?> leave(@RequestBody GroupRequest request) {
        // TODO: 待完善
        // groupService.leave(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "群组", action = "dismiss", description = "dismiss group")
    @PostMapping("/dismiss")
    public ResponseEntity<?> dismiss(@RequestBody GroupRequest request) {

        groupRestService.dismiss(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "群组", action = "删除", description = "delete group")
    @Override
    public ResponseEntity<?> delete(@RequestBody GroupRequest request) {
        
        groupRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "群组", action = "导出", description = "export group")
    @Override
    public Object export(GroupRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            groupRestService,
            GroupExcel.class,
            "群组",
            "group"
        );
    }

    

}
