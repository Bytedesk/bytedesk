/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 11:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-24 12:18:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * API文档控制器
 */
@Controller
public class SwaggerApiController {

    @Autowired
    private MessageSource messageSource;
    
    /**
     * http://127.0.0.1:9003/apidoc/custom-docs
     * 自定义的Swagger UI入口点
     * @param model 视图模型
     * @return 模板名称
     */
    @GetMapping("/apidoc/custom-docs")
    public String customSwaggerUi(Model model) {
        // 获取国际化标题
        String title = messageSource.getMessage("swagger.title", null, LocaleContextHolder.getLocale());
        model.addAttribute("title", title);
        
        // 返回自定义Swagger UI模板
        return "swagger-ui";
    }
    
}
