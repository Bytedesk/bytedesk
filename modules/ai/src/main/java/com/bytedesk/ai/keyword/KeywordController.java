/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:05:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-06 11:29:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.keyword;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/keyword")
@AllArgsConstructor
public class KeywordController extends BaseController<KeywordRequest> {

    private final KeywordService keywordService;

    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(KeywordRequest request) {
        
        Page<KeywordResponse> page = keywordService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @GetMapping("/query/user")
    @Override
    public ResponseEntity<?> query(KeywordRequest request) {
        
        Page<KeywordResponse> page = keywordService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody KeywordRequest request) {
        
        KeywordResponse response = keywordService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody KeywordRequest request) {
        
        KeywordResponse response = keywordService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody KeywordRequest request) {
        
        keywordService.deleteByUid(request.getUid());

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }
    
}
