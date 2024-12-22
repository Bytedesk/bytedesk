/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-05 22:19:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-18 23:25:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.clipboard;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/vip/clipboard")
@AllArgsConstructor
public class ClipboardRestController extends BaseRestController<ClipboardRequest> {

    private final ClipboardRestService clipboardService;

    @Override
    public ResponseEntity<?> queryByOrg(ClipboardRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public ResponseEntity<?> queryByUser(ClipboardRequest request) {
        
        Page<ClipboardResponse> page = clipboardService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> create(ClipboardRequest request) {
        
        ClipboardResponse response = clipboardService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(ClipboardRequest request) {
        
        ClipboardResponse response = clipboardService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(ClipboardRequest request) {
        
        clipboardService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

}
