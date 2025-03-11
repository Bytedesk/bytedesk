/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-31 17:52:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-31 19:08:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article_archive;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for "/article_archive".
 */
@Slf4j
@Controller
@RequestMapping("/article_archive")
@RequiredArgsConstructor
public class ArticleArchiveRouteController {

    private final ArticleArchiveRestService article_archiveService;

    private final KbaseRestService knowledgebaseService;

    // 
    @RequestMapping("/{kbUid:.+}/{article_archiveUid:.+}")
    public String index(Model model, @PathVariable("kbUid") String kbUid, @PathVariable("article_archiveUid") String article_archiveUid) {
        log.info("kbUid {}, article_archiveUid: {}", kbUid, article_archiveUid);
        // 
        Optional<ArticleArchiveEntity> article_archiveOptional = article_archiveService.findByUid(article_archiveUid);
        if (article_archiveOptional.isPresent()) {
            model.addAttribute("article_archive", article_archiveOptional.get());
        } else {
            return "redirect:/404";
        }
        // 
        Optional<KbaseEntity> knowledgebaseOptional = knowledgebaseService.findByUid(kbUid);
        if (knowledgebaseOptional.isPresent()) {
            model.addAttribute("knowledgebase", knowledgebaseOptional.get());
        } else {
            return "redirect:/404";
        }
        
        // TODO: 生成静态页面，考虑语言切换
        //
        return "kbase/zh/article_archive";
    }

    
    
}
