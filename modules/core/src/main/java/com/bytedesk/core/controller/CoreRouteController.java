/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-02 12:28:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-21 13:45:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/core")
public class CoreRouteController {

    // http://127.0.0.1:9003/core/
    @GetMapping({"", "/"})
    public String index() {
        return "core/index";
    }

    // http://127.0.0.1:9003/core/flow
    @GetMapping({"flow", "flow/"})
    public String flow() {
        return "core/flow";
    }
    
}
