/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-18 14:46:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:21:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.worktime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/worktime")
public class WorktimeController extends BaseController<WorktimeRequest> {

    private final WorktimeService worktimeService;

    @Override
    public ResponseEntity<?> queryByOrg(WorktimeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public ResponseEntity<?> queryByUser(WorktimeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @RequestMapping("create")
    @Override
    public ResponseEntity<?> create(@RequestBody WorktimeRequest request) {
        
        WorktimeResponse response = worktimeService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @RequestMapping("update")
    @Override
    public ResponseEntity<?> update(@RequestBody WorktimeRequest request) {
        
        WorktimeResponse response = worktimeService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @RequestMapping("delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody WorktimeRequest request) {
        
        worktimeService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }
    
}
