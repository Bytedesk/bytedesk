/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-13 12:09:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-13 16:49:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
import com.bytedesk.starter.service.PageService;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

/**
 * http://localhost:8011/swagger-ui/index.html
 * 
 * @author
 */
// @Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/temp")
public class TemplateController {

    //
    private PageService pageService;

    /**
     * generate html static file
     * 模板静态化 
     * http://localhost:9003/temp/static
     * 
     * @return
     */
    @GetMapping("/static")
    public JsonResult<?> staticlize() {

        pageService.index();
        pageService.download();
        pageService.about();
        pageService.contact();
        pageService.privacy();
        pageService.protocal();

        return new JsonResult<>("generate html success", 200, true);
    }


}
