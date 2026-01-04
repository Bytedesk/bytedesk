/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.kbase.blog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRepository;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseProperties;
import com.bytedesk.kbase.kbase.KbaseRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/blog")
@AllArgsConstructor
public class BlogController {

    private final KbaseRestService kbaseRestService;

    private final CategoryRepository categoryRepository;

    private final BlogRepository blogRepository;

    private final BlogRestService blogRestService;

    private final KbaseProperties kbaseProperties;

    // http://127.0.0.1:9003/blog/{kbUid}
    @GetMapping({"/{kbUid:[^\\.]*}", "/{kbUid:[^\\.]*}/"})
    public String blogIndex(@PathVariable String kbUid, Model model) {
        log.info("blogIndex path: {}", kbUid);

        Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(kbUid);
        if (kbaseOptional.isEmpty()) {
            return "redirect:/404";
        }

        KbaseEntity kbase = kbaseOptional.get();
        model.addAttribute("kbase", kbase);

        List<CategoryEntity> categories = categoryRepository.findByKbUidAndDeletedFalse(kbUid);
        model.addAttribute("categories", categories);

        List<BlogResponse> blogs = blogRepository.findByKbUidAndDeletedFalse(kbUid)
                .stream()
                .map(blogRestService::convertToResponse)
                .collect(Collectors.toList());
        model.addAttribute("blogs", blogs);

        return "blog/themes/" + kbase.getTheme() + "/index";
    }

    // http://127.0.0.1:9003/blog/{kbUid}/category/{categoryUid}
    // http://127.0.0.1:9003/blog/{kbUid}/category/{categoryUid}.html
    @GetMapping("/{kbUid}/category/{categoryUid:[^\\.]*}")
    public String blogCategory(@PathVariable String kbUid, @PathVariable String categoryUid, Model model) {
        categoryUid = categoryUid.replaceAll("\\.html$", "");
        log.info("blogCategory kbUid={}, categoryUid={}", kbUid, categoryUid);

        Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(kbUid);
        if (kbaseOptional.isEmpty()) {
            return "redirect:/404";
        }
        KbaseEntity kbase = kbaseOptional.get();
        model.addAttribute("kbase", kbase);

        Optional<CategoryEntity> categoryOptional = categoryRepository.findByUid(categoryUid);
        if (categoryOptional.isEmpty()) {
            return "redirect:/404";
        }
        CategoryEntity category = categoryOptional.get();
        model.addAttribute("category", category);

        List<CategoryEntity> categories = categoryRepository.findByKbUidAndDeletedFalse(kbUid);
        model.addAttribute("categories", categories);

        List<BlogResponse> blogs = blogRepository.findByKbUidAndCategoryUidAndDeletedFalse(kbUid, categoryUid)
                .stream()
                .map(blogRestService::convertToResponse)
                .collect(Collectors.toList());
        model.addAttribute("blogs", blogs);

        return "blog/themes/" + kbase.getTheme() + "/category";
    }

    // http://127.0.0.1:9003/blog/{kbUid}/post/{blogUid}
    // http://127.0.0.1:9003/blog/{kbUid}/post/{blogUid}.html
    @GetMapping("/{kbUid}/post/{blogUid:[^\\.]*}")
    public String blogPost(@PathVariable String kbUid, @PathVariable String blogUid, Model model) {
        blogUid = blogUid.replaceAll("\\.html$", "");
        log.info("blogPost kbUid={}, blogUid={}", kbUid, blogUid);

        Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(kbUid);
        if (kbaseOptional.isEmpty()) {
            return "redirect:/404";
        }
        KbaseEntity kbase = kbaseOptional.get();
        model.addAttribute("kbase", kbase);

        Optional<BlogEntity> blogOptional = blogRestService.findByUid(blogUid);
        if (blogOptional.isEmpty()) {
            return "redirect:/404";
        }
        model.addAttribute("blog", blogRestService.convertToResponse(blogOptional.get()));

        List<CategoryEntity> categories = categoryRepository.findByKbUidAndDeletedFalse(kbUid);
        model.addAttribute("categories", categories);
        model.addAttribute("related", new ArrayList<>());

        return "blog/themes/" + kbase.getTheme() + "/post";
    }

    // http://127.0.0.1:9003/blog/{kbUid}/search.html?kbUid=xxx&content=yyy
    @GetMapping("/{kbUid}/search.html")
    public String blogSearch(
            @PathVariable String kbUid,
            @RequestParam(value = "content", required = false) String content,
            Model model) {
        log.info("blogSearch kbUid={}, content={}", kbUid, content);

        model.addAttribute("apiHost", kbaseProperties.resolveBlogApiUrl());
        model.addAttribute("content", content);

        Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(kbUid);
        if (kbaseOptional.isPresent()) {
            model.addAttribute("kbase", kbaseOptional.get());
            return "blog/themes/" + kbaseOptional.get().getTheme() + "/search";
        }

        return "redirect:/404";
    }
}
