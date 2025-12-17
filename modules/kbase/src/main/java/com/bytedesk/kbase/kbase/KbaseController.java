/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-29 13:52:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-01 16:36:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.kbase.article.ArticleEntity;
import com.bytedesk.kbase.article.ArticleResponse;
import com.bytedesk.kbase.article.ArticleRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/helpcenter")
@AllArgsConstructor
public class KbaseController {
    
    private final KbaseRestService kbaseRestService;

    private final CategoryRestService categoryRestService;

    private final ArticleRestService articleRestService;

    private final KbaseProperties kbaseProperties;

	// kb/${currentKbase?.uid}
	// http://127.0.0.1:9003/helpcenter/${currentArticle?.uid}
    @KbaseAnnotation(title = "KbaseRouter", action = "kbIndex", description = "show kbase")
	@GetMapping({"/{kbUid:[^\\.]*}", "/{kbUid:[^\\.]*}/"})
	public String kbIndex(@PathVariable String kbUid, Model model) {
		log.info("kbIndex path: {}", kbUid);

        Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(kbUid);
        if (kbaseOptional.isPresent()) {
            log.info("kbase found: {}, {}", kbaseOptional.get().getName(), kbaseOptional.get().getHeadline());
            model.addAttribute("kbase", kbaseOptional.get());
            // 
            Page<CategoryResponse> categoriesPage = kbaseRestService.getCategories(kbaseOptional.get());
            model.addAttribute("categories", categoriesPage.getContent());
            // 
            Page<ArticleResponse> articlesPage = kbaseRestService.getArticles(kbaseOptional.get());
            // 
            model.addAttribute("articlesTop", articlesPage);
            model.addAttribute("articlesHot", articlesPage);
            model.addAttribute("articlesRecent", articlesPage);
            // 
            return "kbase/themes/" + kbaseOptional.get().getTheme() + "/index";
        }
        
        return "redirect:/404";
	}

    // http://127.0.0.1:9003/helpcenter/{kbUid}/category/${currentCategory?.uid}
    // http://127.0.0.1:9003/helpcenter/{kbUid}/category/${currentCategory?.uid}.html
    // kb/category/${currentCategory?.uid}
    @GetMapping("/{kbUid}/category/{categoryUid}")
	public String kbCategory(@PathVariable(value = "categoryUid") String categoryUid, Model model) {
        categoryUid = categoryUid.replaceAll(".html", "");
        log.info("kbCategory path: {}", categoryUid);
        return routeCategory(categoryUid, model);
	}

    // http://127.0.0.1:9003/helpcenter/{kbUid}/article/${currentArticle?.uid}
    // http://127.0.0.1:9003/helpcenter/{kbUid}/article/${currentArticle?.uid}.html
	// kb/article/${currentArticle?.uid}
	@GetMapping("/{kbUid}/article/{articleUid}")
	public String kbArticle(@PathVariable(value = "articleUid") String articleUid, Model model) {
        articleUid = articleUid.replaceAll(".html", "");
		log.info("kbArticle path: {}", articleUid);
        return routeArticle(articleUid, model);
	}

    @GetMapping("/{kbUid}/search.html")
    public String kbSearch(@RequestParam("kbUid") String kbUid, @RequestParam("content") String content, Model model) {
        log.info("kbSearch path: {}", kbUid, content);
        //
        // model.addAttribute("kbUid", kbUid);
        // model.addAttribute("content", content);
        model.addAttribute("apiHost", kbaseProperties.getApiUrl());
        // 
        Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(kbUid);
        if (kbaseOptional.isPresent()) {
            model.addAttribute("kbase", kbaseOptional.get());
        }
        return "kbase/themes/" + kbaseOptional.get().getTheme() + "/search";
    }

    private String routeCategory(String categoryUid, Model model) {
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(categoryUid);
        if (categoryOptional.isPresent()) {
            model.addAttribute("category", categoryOptional.get());
            // 
            Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(categoryOptional.get().getKbUid());
            if (kbaseOptional.isPresent()) {
                model.addAttribute("kbase", kbaseOptional.get());
                // 
                Page<CategoryResponse> categoriesPage = kbaseRestService.getCategories(kbaseOptional.get());
                model.addAttribute("categories", categoriesPage.getContent());
                //
                Page<ArticleResponse> articlesPage = kbaseRestService.getArticlesByCategory(kbaseOptional.get(), categoryOptional.get().getUid());
                model.addAttribute("articles", articlesPage.getContent());

                return "kbase/themes/" + kbaseOptional.get().getTheme() + "/category";
            }
        }
        // error
		return "redirect:/404";
    }

    private String routeArticle(String articleUid, Model model) {
        Optional<ArticleEntity> articleOptional = articleRestService.findByUid(articleUid);
        if (articleOptional.isPresent()) {
            model.addAttribute("article", articleRestService.convertToResponse(articleOptional.get()));
            // 
            Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(articleOptional.get().getKbase().getUid());
            if (kbaseOptional.isPresent()) {
                model.addAttribute("kbase", kbaseOptional.get());
                // 
                Page<CategoryResponse> categoriesPage = kbaseRestService.getCategories(kbaseOptional.get());
                model.addAttribute("categories", categoriesPage.getContent());
                model.addAttribute("related", new ArrayList<>());
                // 
                return "kbase/themes/" + kbaseOptional.get().getTheme() + "/article";
            }
        }
        // error
		return "redirect:/404";
    }

    //////////////////////////////////////////////////////////////////

	// http://127.0.0.1:9003/helpcenter/edu
	@GetMapping("/edu")
	public String kbEduIndex() {
		return "kbase/themes/eduport/index";
	}

	// http://127.0.0.1:9003/helpcenter/edu/detail
	public String kbEduDetail() {
		return "kbase/themes/eduport/detail";
	}

	// http://127.0.0.1:9003/helpcenter/social
	@GetMapping("/social")
	public String kbSocialIndexS() {
		return "kbase/themes/social/index";
	}

	// http://127.0.0.1:9003/helpcenter/social/detail
	public String kbSocialDetail() {
		return "kbase/themes/social/detail";
	}

	// 
	// http://127.0.0.1:9003/helpcenter/default
	@GetMapping("/default")
	public String kbZdIndex() {
		return "kbase/themes/default/index";
	}

	// http://127.0.0.1:9003/helpcenter/default/article
	@GetMapping("/default/{path:[^\\.]*}")
	public String kbZdRedirect(@PathVariable String path) {
		log.info("kbZdRedirect path: {}", path);
		return "forward:/kbase/themes/default/" + path; // 默认路径
	}
}
