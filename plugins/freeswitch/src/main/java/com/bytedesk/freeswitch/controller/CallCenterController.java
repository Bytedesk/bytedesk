/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-03 14:30:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 10:07:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.freeswitch.model.CallStatistics;
import com.bytedesk.freeswitch.service.CallStatisticsService;

@Controller
@RequestMapping("/callcenter")
public class CallCenterController {
    
    @Autowired
    private CallStatisticsService callStatisticsService;

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
        // 获取呼叫统计数据
        CallStatistics statistics = callStatisticsService.getCallStatistics();
        model.addAttribute("statistics", statistics);
        return "callcenter/index";
    }

    // 呼叫记录列表
    // @GetMapping("/calls")
    // public String calls(Model model,
    //         @RequestParam(required = false) String status,
    //         @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
    //     model.addAttribute("calls", callService.getCallsByStatus(status, pageable));
    //     model.addAttribute("status", status);
    //     return "callcenter/calls";
    // }

    // 呼叫详情页面
    // @GetMapping("/call/{callId}")
    // public String viewCall(@PathVariable String callId, Model model) {
    //     model.addAttribute("call", callService.getCall(callId));
    //     return "callcenter/call-detail";
    // }

    // 我的呼叫记录
    // @GetMapping("/user/calls")
    // public String userCalls(@AuthenticationPrincipal UserDetails userDetails, Model model,
    //         @RequestParam(required = false) String type,
    //         @RequestParam(required = false) String status,
    //         @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
    //     String userUid = userDetails.getUsername();
    //     model.addAttribute("calls", callService.getUserCalls(userUid, status, pageable));
    //     return "callcenter/user-calls";
    // }

    // 管理员页面 - 呼叫监控
    // @GetMapping("/admin/monitor")
    // public String callMonitor(Model model) {
    //     model.addAttribute("activeCalls", callService.getActiveCalls());
    //     model.addAttribute("agents", callService.getAvailableAgents());
    //     return "callcenter/admin/monitor";
    // }

    // 管理员页面 - 呼叫统计和报表
    @GetMapping("/admin/statistics")
    public String callStatistics(Model model, 
            @RequestParam(required = false) String period) {
        
        model.addAttribute("statistics", callStatisticsService.getCallStatisticsByPeriod(period));
        return "callcenter/admin/statistics";
    }
}
