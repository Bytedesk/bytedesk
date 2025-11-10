/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-13 12:09:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-30 08:56:20
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

    private PageTemplateService pageTemplateService;

    /**
     * generate html static file
     * 模板静态化
     * http://127.0.0.1:9003/temp/static
     * 
     * @return
     */
    @GetMapping("/static")
    public JsonResult<?> staticize() {
        // single default zh-CN legacy generation
        pageTemplateService.toHtml("index");
        // pages
        pageTemplateService.toHtml("pages/download");
        pageTemplateService.toHtml("pages/about");
        pageTemplateService.toHtml("pages/contact");
        pageTemplateService.toHtml("pages/privacy");
        pageTemplateService.toHtml("pages/terms");
        // features
        pageTemplateService.toHtml("features/office");
        pageTemplateService.toHtml("features/scrm");
        pageTemplateService.toHtml("features/team");
        pageTemplateService.toHtml("features/ai");
        pageTemplateService.toHtml("features/kbase");
        pageTemplateService.toHtml("features/voc");
        pageTemplateService.toHtml("features/ticket");
        pageTemplateService.toHtml("features/workflow");
        pageTemplateService.toHtml("features/kanban");
        pageTemplateService.toHtml("features/callcenter");
        pageTemplateService.toHtml("features/video");
        
        // multilingual generation
        pageTemplateService.toHtmlMulti("index");
        // pages
        pageTemplateService.toHtmlMulti("pages/download");
        pageTemplateService.toHtmlMulti("pages/about");
        pageTemplateService.toHtmlMulti("pages/contact");
        pageTemplateService.toHtmlMulti("pages/privacy");
        pageTemplateService.toHtmlMulti("pages/terms");
        // features
        pageTemplateService.toHtmlMulti("features/office");
        pageTemplateService.toHtmlMulti("features/scrm");
        pageTemplateService.toHtmlMulti("features/team");
        pageTemplateService.toHtmlMulti("features/ai");
        pageTemplateService.toHtmlMulti("features/kbase");
        pageTemplateService.toHtmlMulti("features/voc");
        pageTemplateService.toHtmlMulti("features/ticket");
        pageTemplateService.toHtmlMulti("features/workflow");
        pageTemplateService.toHtmlMulti("features/kanban");
        pageTemplateService.toHtmlMulti("features/callcenter");
        pageTemplateService.toHtmlMulti("features/video");
        
        return JsonResult.success("generate html success (multi-language)", 200, true);
    }

}
