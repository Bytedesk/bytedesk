/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:10:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 18:42:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.controller;

import com.bytedesk.voc.feedback.*;
// import com.bytedesk.voc.reply.ReplyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/voc")
public class VocController {

    @Value("${bytedesk.custom.show-demo:true}")
    private Boolean showDemo;

    @Autowired
    private FeedbackService feedbackService;

    // 添加用户信息到所有请求
    @ModelAttribute
    public void addAttributes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            model.addAttribute("currentUser", userDetails);
        }
    }

    // VOC首页
    // http://127.0.0.1:9003/voc/
    @GetMapping({"", "/"})
    public String index() {
        if (!showDemo) {
			return "default";
		}
        return "voc/index";
    }

    // 创建反馈页面
    @GetMapping("/feedback/create")
    public String createFeedbackForm() {
        return "voc/feedback-form";
    }

    // 反馈详情页面
    @GetMapping("/feedback/{feedbackId}")
    public String viewFeedback(@PathVariable Long feedbackId, Model model) {
        model.addAttribute("feedback", feedbackService.getFeedback(feedbackId));
        return "voc/feedback";
    }

    // 我的反馈列表
    @GetMapping("/user/feedbacks")
    public String userFeedbacks(@AuthenticationPrincipal UserDetails userDetails, Model model,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Long userId = Long.valueOf(userDetails.getUsername());
        Page<FeedbackEntity> feedbacks;
        
        if (type != null || status != null) {
            feedbacks = feedbackService.search(null, type, status, pageable);
        } else {
            feedbacks = feedbackService.getFeedbacksByUser(userId, pageable);
        }
        
        model.addAttribute("feedbacks", feedbacks);
        return "voc/user-feedbacks";
    }

    // 管理员页面 - 待处理反馈
    @GetMapping("/admin/pending")
    public String pendingFeedbacks(Model model,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("feedbacks", feedbackService.getFeedbacksByStatus("pending", pageable));
        return "voc/admin/pending";
    }

    // 管理员页面 - 分配给我的反馈
    @GetMapping("/admin/assigned")
    public String assignedFeedbacks(@AuthenticationPrincipal UserDetails userDetails, Model model,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // model.addAttribute("feedbacks", 
        //     feedbackService.getFeedbacksByAssignedTo(Long.valueOf(userDetails.getUsername()), pageable));
        return "voc/admin/assigned";
    }
} 