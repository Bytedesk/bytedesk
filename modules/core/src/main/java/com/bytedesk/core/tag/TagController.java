/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-24 18:25:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/tag")
@AllArgsConstructor
public class TagController extends BaseRestController<TagRequest> {

    private final TagService tagService;

    @Override
    public ResponseEntity<?> queryByOrg(TagRequest request) {
        
        Page<TagResponse> tags = tagService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @Override
    public ResponseEntity<?> queryByUser(TagRequest request) {
        
        Page<TagResponse> tags = tagService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @Override
    public ResponseEntity<?> create(TagRequest request) {
        
        TagResponse tag = tagService.create(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @Override
    public ResponseEntity<?> update(TagRequest request) {
        
        TagResponse tag = tagService.update(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @Override
    public ResponseEntity<?> delete(TagRequest request) {
        
        tagService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}