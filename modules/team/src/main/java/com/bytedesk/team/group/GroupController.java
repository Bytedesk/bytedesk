/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:21:59
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

import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

/**
 * http://127.0.0.1:9003/swagger-ui/index.html
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "group", description = "group apis")
public class GroupController extends BaseRestController<GroupRequest> {
    
    private final GroupService groupService;
    
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(GroupRequest request) {
        
        Page<GroupResponse> page = groupService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @GetMapping("/query")
    @Override
    public ResponseEntity<?> queryByUser(GroupRequest request) {

        Page<GroupResponse> page = groupService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }
    
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(GroupRequest request) {
        
        GroupResponse group = groupService.queryByUid(request);
        
        return ResponseEntity.ok(JsonResult.success(group));
    }

    @ActionAnnotation(title = "group", action = "create", description = "create group")
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupService.create(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @ActionAnnotation(title = "group", action = "update", description = "update group")
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody GroupRequest request) {

        GroupResponse group = groupService.update(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @ActionAnnotation(title = "group", action = "invite", description = "invite group")
    @PostMapping("/invite")
    public ResponseEntity<?> invite(@RequestBody GroupRequest request) {
        // TODO: 待完善
        // groupService.invite(request);
        return ResponseEntity.ok(JsonResult.success());
    }
    
    @ActionAnnotation(title = "group", action = "join", description = "join group")
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody GroupRequest request) {
        // TODO: 待完善
        // groupService.join(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "group", action = "kick", description = "kick group")
    @PostMapping("/kick")
    public ResponseEntity<?> kick(@RequestBody GroupRequest request) {
        // TODO: 待完善, 将用户踢出/移出群组
        // groupService.kick(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "group", action = "leave", description = "leave group")
    @PostMapping("/leave")
    public ResponseEntity<?> leave(@RequestBody GroupRequest request) {
        // TODO: 待完善
        // groupService.leave(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "group", action = "dismiss", description = "dismiss group")
    @PostMapping("/dismiss")
    public ResponseEntity<?> dismiss(@RequestBody GroupRequest request) {

        groupService.dismiss(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "group", action = "delete", description = "delete group")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody GroupRequest request) {
        
        groupService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }


    

}
