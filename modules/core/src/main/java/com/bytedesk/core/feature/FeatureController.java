/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-19 10:35:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-22 10:23:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.feature;

import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;

// anonymous api
@RestController
@RequestMapping("/features")
@AllArgsConstructor
public class FeatureController {
    
    // private final BytedeskProperties bytedeskProperties;

    // http://127.0.0.1:9003/features/get
    // @GetMapping("/get")
    // public ResponseEntity<?> get() {

    //     FeatureResponseSimple featureResponseSimple = FeatureResponseSimple.builder()
    //         .edition(bytedeskProperties.getEdition())
    //     .build();

    //     return ResponseEntity.ok(JsonResult.success(featureResponseSimple));
    // }


}

