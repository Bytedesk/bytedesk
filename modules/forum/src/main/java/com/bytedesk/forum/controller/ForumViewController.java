/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 11:51:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 18:41:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.forum.controller;

import com.bytedesk.core.category.CategoryRepository;
import com.bytedesk.forum.post.PostService;
import com.bytedesk.forum.user.ForumUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/forum")
public class ForumViewController {

    @Value("${bytedesk.custom.show-demo:true}")
    private Boolean showDemo;

    @Autowired
    private PostService postService;


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ForumUserService forumUserService;

    // 添加用户信息到所有请求
    @ModelAttribute
    public void addAttributes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            model.addAttribute("currentUser", userDetails);
        }
    }

    // http://127.0.0.1:9003/forum/
    // 论坛首页
    @GetMapping({"", "/"})
    public String index() {
        if (!showDemo) {
			return "default";
		}
        // 
        return "forum/index";
    }


    // @GetMapping({"", "/"})
    // public String index(Model model, 
    //         @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    //     model.addAttribute("categories", categoryRepository.findAll());
    //     model.addAttribute("posts", postService.getPosts(pageable));
    //     return "forum/index";
    // }

    // 分类页面
    @GetMapping("/category/{categoryId}")
    public String category(@PathVariable Long categoryId, Model model,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("category", categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found")));
        model.addAttribute("posts", postService.getPostsByCategory(categoryId, pageable));
        return "forum/category";
    }

    // 帖子详情页
    @GetMapping("/posts/{postId}")
    public String post(@PathVariable Long postId, Model model,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        // 增加浏览量
        postService.incrementViewCount(postId);
        
        model.addAttribute("post", postService.getPost(postId));
        // model.addAttribute("comments", commentService.getCommentsByPost(postId, pageable));
        return "forum/post";
    }

    // 发布帖子页面
    @GetMapping("/posts/create")
    public String createPost(@RequestParam(required = false) Long categoryId, Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        if (categoryId != null) {
            model.addAttribute("selectedCategory", categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found")));
        }
        return "forum/post-form";
    }

    // 编辑帖子页面
    @GetMapping("/posts/{postId}/edit")
    public String editPost(@PathVariable Long postId, Model model) {
        model.addAttribute("post", postService.getPost(postId));
        model.addAttribute("categories", categoryRepository.findAll());
        return "forum/post-form";
    }

    // 搜索页面
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("posts", postService.fullTextSearch(keyword, pageable));
        return "forum/search";
    }

    // 添加用户帖子页面
    @GetMapping("/user/posts")
    public String userPosts(@AuthenticationPrincipal UserDetails userDetails, Model model,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("posts", postService.getPostsByUser(Long.valueOf(userDetails.getUsername()), pageable));
        return "forum/user-posts";
    }

    // 添加用户评论页面
    @GetMapping("/user/comments")
    public String userComments(@AuthenticationPrincipal UserDetails userDetails, Model model,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // model.addAttribute("comments", commentService.getCommentsByUser(Long.valueOf(userDetails.getUsername()), pageable));
        return "forum/user-comments";
    }

    // 用户个人中心
    @GetMapping("/user/profile")
    public String userProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long userId = Long.valueOf(userDetails.getUsername());
        model.addAttribute("userStats", forumUserService.getUserStats(userId));
        return "forum/user/profile";
    }

    // 用户设置页面
    @GetMapping("/user/settings")
    public String userSettings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        return "forum/user/settings";
    }

    // 用户点赞列表
    @GetMapping("/user/likes")
    public String userLikes(@AuthenticationPrincipal UserDetails userDetails, Model model,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = Long.valueOf(userDetails.getUsername());
        model.addAttribute("likes", forumUserService.getUserLikes(userId, pageable));
        return "forum/user/likes";
    }
} 