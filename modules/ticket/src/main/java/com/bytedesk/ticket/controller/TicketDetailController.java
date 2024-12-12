/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 14:22:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 16:04:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bytedesk.ticket.ticket.TicketService;
import com.bytedesk.ticket.comment.TicketCommentService;

import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketDetailController {

    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private TicketCommentService commentService;
    
    // @Autowired
    // private TicketUserService userService;
    
    // @Autowired
    // private CategoryRestService categoryService;

    @GetMapping("/{ticketId}")
    public String showTicket(@PathVariable Long ticketId, Model model) {
        model.addAttribute("ticket", ticketService.getTicket(ticketId));
        model.addAttribute("comments", commentService.getTicketComments(ticketId));
        // model.addAttribute("agents", userService.getAvailableAgents());
        // model.addAttribute("categories", categoryService.getAllCategories());
        
        // 添加页面特定的CSS和JavaScript
        model.addAttribute("styles", List.of("/css/ticket-detail.css"));
        model.addAttribute("scripts", List.of("/js/ticket-detail.js"));
        
        return "ticket/detail";
    }

    @PostMapping("/{ticketId}/comments")
    public String addComment(
            @PathVariable Long ticketId,
            @RequestParam String content,
            @RequestParam(required = false) Boolean internal,
            @RequestParam(required = false) List<MultipartFile> attachments,
            @AuthenticationPrincipal Long userId,
            RedirectAttributes redirectAttributes) {
        try {
            commentService.addComment(ticketId, content, internal, attachments, userId);
            redirectAttributes.addFlashAttribute("success", "评论已添加");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "添加评论失败：" + e.getMessage());
        }
        return "redirect:/tickets/" + ticketId;
    }

    @PostMapping("/{ticketId}/status")
    public String updateStatus(
            @PathVariable Long ticketId,
            @RequestParam String status,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes) {
        try {
            // ticketService.updateStatus(ticketId, status, reason);
            redirectAttributes.addFlashAttribute("success", "工单状态已更新");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "更新状态失败：" + e.getMessage());
        }
        return "redirect:/tickets/" + ticketId;
    }

    @PostMapping("/{ticketId}/assign")
    public String assignTicket(
            @PathVariable Long ticketId,
            @RequestParam Long assigneeId,
            RedirectAttributes redirectAttributes) {
        try {
            ticketService.assignTicket(ticketId, assigneeId);
            redirectAttributes.addFlashAttribute("success", "工单已分配");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "分配工单失败：" + e.getMessage());
        }
        return "redirect:/tickets/" + ticketId;
    }
} 