/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-18 15:58:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 16:06:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/visitor/api/v1/form")
@AllArgsConstructor
public class FormRestControllerVisitor {

    private final FormRestService formRestService;

    @Operation(summary = "查询可用的表单", description = "根据组织UID和状态查询可用的表单列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @GetMapping("/available")
    public ResponseEntity<?> queryAvailableForms(FormRequest request) {
        
        Page<FormResponse> forms = formRestService.queryAvailableForms(request);

        return ResponseEntity.ok(JsonResult.success(forms));
    }

    @Operation(summary = "点击表单", description = "点击表单并返回表单详情（与 FAQ click 类似）")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = FormResponse.class)))
    @GetMapping("/click/uid")
    public ResponseEntity<?> clickForm(FormRequest request) {
        FormResponse form = formRestService.clickForm(request);
        return ResponseEntity.ok(JsonResult.success(form));
    }

}
