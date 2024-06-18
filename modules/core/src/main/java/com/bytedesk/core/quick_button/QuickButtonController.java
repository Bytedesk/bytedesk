/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:02:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-14 13:46:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quick_button;

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
@RequestMapping("/api/v1/quickbutton")
@AllArgsConstructor
public class QuickButtonController extends BaseController<QuickButtonRequest> {

    private final QuickButtonService quickButtonService;

    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(QuickButtonRequest request) {
        
        Page<QuickButtonResponse> page = quickButtonService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> query(QuickButtonRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody QuickButtonRequest request) {
        
        QuickButtonResponse quickButton = quickButtonService.create(request);

        return ResponseEntity.ok(JsonResult.success(quickButton));
    }

    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody QuickButtonRequest request) {
        
        QuickButtonResponse quickButton = quickButtonService.update(request);

        return ResponseEntity.ok(JsonResult.success(quickButton)); 
    }

    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody QuickButtonRequest request) {
        
        quickButtonService.deleteByUid(request.getUid());

        return ResponseEntity.ok(JsonResult.success(request.getUid())); 
    }


    // 
    


}
