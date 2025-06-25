/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-13 12:09:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-12 21:12:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.starter.service.PageTemplateService;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

/**
 * http://127.0.0.1:9003/swagger-ui/index.html
 * 
 * @author
 */
// @Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/temp")
public class PageTemplateController {

    private PageTemplateService pageService;

    /**
     * generate html static file
     * 模板静态化
     * http://127.0.0.1:9003/temp/static
     * 
     * @return
     */
    @GetMapping("/static")
    public JsonResult<?> staticize() {

        pageService.index();
        // pageService.plan();
        pageService.download();
        pageService.about();
        pageService.contact();
        pageService.privacy();
        pageService.terms();

        return JsonResult.success("generate html success", 200, true);
    }

}
