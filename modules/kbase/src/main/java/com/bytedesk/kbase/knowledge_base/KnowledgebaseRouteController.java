/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-31 17:51:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-31 18:52:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for "/kbase".
 */
@Slf4j
@Controller
@RequestMapping("/kbase")
@RequiredArgsConstructor
public class KnowledgebaseRouteController {

    private final KnowledgebaseService knowledgebaseService;

    @RequestMapping("/{kbUid:.+}")
    public String index(Model model, @PathVariable("kbUid") String kbUid) {
        log.info("kbUid: {}", kbUid);
        //
        Optional<Knowledgebase> knowledgebaseOptional = knowledgebaseService.findByUid(kbUid);
        if (knowledgebaseOptional.isPresent()) {
            model.addAttribute("knowledgebase", knowledgebaseOptional.get());
        } else {
            return "redirect:/404";
        }
        // TODO: 生成静态页面，考虑语言切换
        //
        return "kbase/zh/index";
    }

}
