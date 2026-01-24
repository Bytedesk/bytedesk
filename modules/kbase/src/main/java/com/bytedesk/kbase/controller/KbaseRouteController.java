/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-02 12:34:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 18:42:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.Description;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/module/kbase")
@Description("Knowledge Base Route Controller - Knowledge base module route controller for handling knowledge base page requests")
public class KbaseRouteController {

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

    // http://127.0.0.1:9003/module/kbase/
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
        return "kbase/index";
    }
    
}
