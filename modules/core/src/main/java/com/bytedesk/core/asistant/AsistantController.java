/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:04:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-26 22:54:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.asistant;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/asistant")
@Tag(name = "asistant - 助手", description = "asistant apis")
public class AsistantController {

    private final AsistantService asistantService;

    /**
     * query asistant
     *
     * @return json
     */
    @GetMapping("/query")
    public ResponseEntity<?> query(AsistantRequest asistantRequest) {
        //
        Page<AsistantResponse> asistantPage = asistantService.query(asistantRequest);
        //
        return ResponseEntity.ok(JsonResult.success(asistantPage));
    }

    



    
}
