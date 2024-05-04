/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-22 10:37:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.action.ActionLogAnnotation;
import com.bytedesk.core.utils.BaseRequest;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/workgroup")
public class WorkgroupController {

    private final WorkgroupService workgroupService;

    /**
     * query
     * 
     * @param workgroupRequest
     * @return
     */
    @ActionLogAnnotation(title = "workgroup", action = "query")
    @GetMapping("/query")
    public ResponseEntity<?> query(WorkgroupRequest workgroupRequest) {
        
        return ResponseEntity.ok(JsonResult.success(workgroupService.query(workgroupRequest)));
    }

    /**
     * create
     *
     * @param workgroupRequest workgroup
     * @return json
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody WorkgroupRequest workgroupRequest) {

        Workgroup workgroup = workgroupService.create(workgroupRequest);
        if (workgroup == null) {
            return ResponseEntity.ok(JsonResult.error("create failed"));
        }

        return ResponseEntity.ok(JsonResult.success(workgroupService.convertToWorkgroupResponse(workgroup)));
    }

    /**
     * update
     *
     * @param workgroupRequest workgroup
     * @return json
     */
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody WorkgroupRequest workgroupRequest) {

        Workgroup workgroup = workgroupService.update(workgroupRequest);
        if (workgroup == null) {
            return ResponseEntity.ok(JsonResult.error("update failed"));
        }
        //
        return ResponseEntity.ok(JsonResult.success(workgroupService.convertToWorkgroupResponse(workgroup)));
    }

    /**
     * delete
     *
     * @param workgroupRequest workgroup
     * @return json
     */
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody WorkgroupRequest workgroupRequest) {

        //
        return ResponseEntity.ok(JsonResult.success("delete success"));
    }

    /**
     * filter
     *
     * @return json
     */
    @GetMapping("/filter")
    public ResponseEntity<?> filter(BaseRequest filterParam) {
        
        //
        return ResponseEntity.ok(JsonResult.success());
    }

}
