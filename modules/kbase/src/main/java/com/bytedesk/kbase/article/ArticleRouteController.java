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
package com.bytedesk.kbase.article;

// import java.util.Optional;

// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;

// import com.bytedesk.kbase.kbase.KbaseEntity;
// import com.bytedesk.kbase.kbase.KbaseRestService;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * Controller for "/article".
//  */
// @Slf4j
// @Controller
// @RequestMapping("/article")
// @RequiredArgsConstructor
// public class ArticleRouteController {

//     private final ArticleRestService articleService;

//     private final KbaseRestService knowledgebaseService;

//     // 
//     @RequestMapping("/{kbUid:.+}/{articleUid:.+}")
//     public String index(Model model, @PathVariable("kbUid") String kbUid, @PathVariable("articleUid") String articleUid) {
//         log.info("kbUid {}, articleUid: {}", kbUid, articleUid);
//         // 
//         Optional<ArticleEntity> articleOptional = articleService.findByUid(articleUid);
//         if (articleOptional.isPresent()) {
//             model.addAttribute("article", articleOptional.get());
//         } else {
//             return "redirect:/404";
//         }
//         // 
//         Optional<KbaseEntity> knowledgebaseOptional = knowledgebaseService.findByUid(kbUid);
//         if (knowledgebaseOptional.isPresent()) {
//             model.addAttribute("knowledgebase", knowledgebaseOptional.get());
//         } else {
//             return "redirect:/404";
//         }
        
//         // TODO: 生成静态页面，考虑语言切换
//         //
//         return "kbase/zh/article";
//     }

    
    
// }
