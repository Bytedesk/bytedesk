/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-07 21:24:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-15 00:03:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config.properties;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ApiRateLimiter;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/config/bytedesk", produces = "application/json;charset=UTF-8")
@Tag(name = "Configuration Properties", description = "Configuration properties management APIs for system settings")
public class BytedeskPropertiesController {

    // http://127.0.0.1:9003/config/bytedesk/properties
    @ApiRateLimiter(value = 1, timeout = 1)
    @Operation(summary = "Get Bytedesk Properties", description = "Retrieve Bytedesk system configuration properties")
    @GetMapping(value = "/properties", produces = "application/json;charset=UTF-8")
    public ResponseEntity<JsonResult<?>> getBytedeskProperties() {

        BytedeskPropertiesResponse bytedeskPropertiesResponse = ConvertUtils.convertToBytedeskPropertiesResponse(BytedeskProperties.getInstance());
        
        return ResponseEntity.ok(JsonResult.success(bytedeskPropertiesResponse));
    }
    
}