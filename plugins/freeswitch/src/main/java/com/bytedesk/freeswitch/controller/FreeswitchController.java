/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-03 14:10:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 14:46:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.controller;

import com.bytedesk.freeswitch.service.CallService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/callcenter")
public class FreeswitchController {

    @Autowired
    private CallService callService;

    // 添加用户信息到所有请求
    @ModelAttribute
    public void addAttributes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            model.addAttribute("currentUser", userDetails);
        }
    }

    // 呼叫中心首页
    // http://127.0.0.1:9003/callcenter/
    @GetMapping({"", "/"})
    public String index(Model model) {
        // 获取呼叫中心统计数据
        model.addAttribute("totalCalls", callService.countTotalCalls());
        model.addAttribute("activeCalls", callService.countActiveCalls());
        model.addAttribute("missedCalls", callService.countMissedCalls());
        model.addAttribute("avgDuration", callService.getAverageCallDuration());
        return "callcenter/index";
    }

    // 历史通话记录页面
    @GetMapping("/history")
    public String callHistory(Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        model.addAttribute("calls", callService.getCallHistory(keyword, status, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "callcenter/history";
    }

    // 我的通话记录
    @GetMapping("/user/calls")
    public String userCalls(@AuthenticationPrincipal UserDetails userDetails, Model model,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        String userId = userDetails.getUsername();
        model.addAttribute("calls", callService.getUserCalls(userId, pageable));
        return "callcenter/user-calls";
    }

    // 管理员页面 - 实时监控
    @GetMapping("/admin/monitor")
    public String monitorCalls(Model model) {
        model.addAttribute("activeCalls", callService.getActiveCalls());
        return "callcenter/admin/monitor";
    }

    // 管理员页面 - 座席状态
    @GetMapping("/admin/agents")
    public String agentStatus(Model model) {
        model.addAttribute("agents", callService.getAgentStatus());
        return "callcenter/admin/agents";
    }
}
