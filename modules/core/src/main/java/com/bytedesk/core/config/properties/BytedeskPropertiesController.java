/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-07 21:24:22
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-02-25 22:54:00
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

import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.JsonResult;

@RestController
@RequestMapping(value = "/config/bytedesk", produces = "application/json;charset=UTF-8")
public class BytedeskPropertiesController {

    // http://127.0.0.1:9003/config/bytedesk/properties
    @GetMapping(value = "/properties", produces = "application/json;charset=UTF-8")
    public ResponseEntity<JsonResult<?>> getBytedeskProperties() {

        BytedeskPropertiesResponse bytedeskPropertiesResponse = ConvertUtils.convertToBytedeskPropertiesResponse(BytedeskProperties.getInstance());
        
        return ResponseEntity.ok(JsonResult.success(bytedeskPropertiesResponse));
    }
    
}