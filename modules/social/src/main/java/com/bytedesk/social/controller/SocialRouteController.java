/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-02 12:28:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 18:41:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.social.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/module/social")
public class SocialRouteController {

    @Value("${bytedesk.custom.show-demo:true}")
    private Boolean showDemo;
    
    @Value("${bytedesk.custom.enabled:false}")
    private Boolean customEnabled;
    
    @Value("${bytedesk.custom.name:微语}")
    private String customName;
    
    @Value("${bytedesk.custom.logo:https://www.weiyuai.cn/logo.png}")
    private String customLogo;
    
    @Value("${bytedesk.custom.description:重复工作自动化}")
    private String customDescription;

    // http://127.0.0.1:9003/module/social/
    @GetMapping({"", "/"})
    public String index(Model model) {
        if (!showDemo) {
            // 添加自定义配置到模型
            if (customEnabled) {
                model.addAttribute("customName", customName);
                model.addAttribute("customLogo", customLogo);
                model.addAttribute("customDescription", customDescription);
            }
			return "default";
		}
        return "social/index";
    }
    
}
