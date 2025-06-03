/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-03 14:30:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 10:24:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.webrtc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/webrtc")
public class WebrtcController {
    

    // 视频客服首页
    // http://127.0.0.1:9003/webrtc/
    @GetMapping({"", "/"})
    public String index(Model model) {
        return "webrtc/index";
    }

} 