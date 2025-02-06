/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-06 16:52:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for "/".
 *
 * @author jackning
 */
@Slf4j
@Controller
public class PageRouteController {

	/**
	 * http://127.0.0.1:9003
	 */
	@GetMapping("/")
	public String index(Model model) {
		try {
			// 添加语言选项
			Map<String, String> languages = new HashMap<>();
			languages.put("en", "English");
			languages.put("zh", "简体中文");
			languages.put("zh-TW", "繁體中文");
			model.addAttribute("languages", languages);
			
			// 添加多语言文本
			Map<String, Map<String, String>> i18n = new HashMap<>();
			
			// 英文
			Map<String, String> en = new HashMap<>();
			en.put("title", "ByteDesk");
			en.put("systemEntrance", "System Entrance");
			en.put("systemDevelopment", "System Development");
			en.put("adminDashboard", "Admin Dashboard");
			en.put("agentClient", "Agent Client");
			en.put("visitorChat", "Visitor Chat");
			en.put("workFlow", "Work Flow");
			en.put("knowledgeBase", "Knowledge Base(Internal)");
			en.put("helpCenter", "Help Center(External)");
			en.put("apiDoc", "API Documentation");
			en.put("monitoring", "Monitoring");
			en.put("voiceOfCustomer", "Voice of Customer");
			en.put("moduleDevelopment", "Module Development");
			en.put("forum", "Forum");
			en.put("ticket", "Ticket");
			en.put("team", "Team");
			en.put("service", "Service");
			en.put("kbase", "Kbase");
			en.put("ai", "AI");
			
			// 简体中文
			Map<String, String> zh = new HashMap<>();
			zh.put("title", "微语");
			zh.put("systemEntrance", "系统入口");
			zh.put("systemDevelopment", "开发工具");
			zh.put("adminDashboard", "管理后台");
			zh.put("agentClient", "客服工作台");
			zh.put("visitorChat", "访客对话");
			zh.put("workFlow", "工作流");
			zh.put("knowledgeBase", "对内知识库");
			zh.put("helpCenter", "帮助中心(对外知识库)");
			zh.put("apiDoc", "API 文档");
			zh.put("monitoring", "系统监控");
			zh.put("voiceOfCustomer", "客户之声");
			zh.put("moduleDevelopment", "模块开发");
			zh.put("forum", "用户社区");
			zh.put("ticket", "工单管理");
			zh.put("team", "团队管理");
			zh.put("service", "服务管理");
			zh.put("kbase", "知识库管理");
			zh.put("ai", "AI管理");
			
			// 繁体中文
			Map<String, String> zhTW = new HashMap<>();
			zhTW.put("title", "微語");
			zhTW.put("systemEntrance", "系統入口");
			zhTW.put("systemDevelopment", "開發工具");
			zhTW.put("adminDashboard", "管理後台");
			zhTW.put("agentClient", "客服工作台");
			zhTW.put("visitorChat", "訪客對話");
			zhTW.put("workFlow", "工作流");
			zhTW.put("knowledgeBase", "對內知識庫");
			zhTW.put("helpCenter", "幫助中心(對外知識庫)");
			zhTW.put("apiDoc", "API 文檔");
			zhTW.put("monitoring", "系統監控");
			zhTW.put("voiceOfCustomer", "客戶之聲");
			zhTW.put("moduleDevelopment", "模組開發");
			zhTW.put("forum", "用戶社區");
			zhTW.put("ticket", "工單管理");
			zhTW.put("team", "團隊管理");
			zhTW.put("service", "服務管理");
			zhTW.put("kbase", "知識庫管理");
			zhTW.put("ai", "AI管理");
			
			i18n.put("en", en);
			i18n.put("zh", zh);
			i18n.put("zh-TW", zhTW);
			
			model.addAttribute("i18n", i18n);

			// 添加调试信息
			log.info("Languages: {}", languages);
			log.info("I18n: {}", i18n);

			return "dev";
		} catch (Exception e) {
			log.error("Error rendering index page", e);
			throw e;
		}
	}

	/**
	 * admin
	 * 管理后台
	 * http://127.0.0.1:9003/admin
	 */
	@GetMapping({
		"/admin", 
		"/admin/", 
		"/admin/{path:[^\\.]*}", 
		"/admin/{path:[^\\.]*}/{path2:[^\\.]*}"}) 
	public String admin(@PathVariable(required = false) String path, 
						@PathVariable(required = false) String path2) {
		return "forward:/admin/index.html"; // 默认路径
	}

	/**
	 * agent
	 * web聊天/客服端
	 * http://127.0.0.1:9003/agent
	 */
	@GetMapping({
		"/agent", 
		"/agent/", 
		"/agent/{path:[^\\.]*}", 
		"/agent/{path:[^\\.]*}/{path2:[^\\.]*}"})
	public String agent(@PathVariable(required = false) String path, 
						@PathVariable(required = false) String path2) {
		return "forward:/agent/index.html"; // 默认路径
	}

	/**
	 * visitor
	 * 访客对话窗口
	 * http://127.0.0.1:9003/chat
	 */
	@GetMapping({
		"/chat", 
		"/chat/", 
		"/chat/{type:demo|frame|float|ticket|feedback|number|queue|center|helpcenter|server|config}"})
	public String chat(@PathVariable(required = false) String type) {
		return "forward:/chat/index.html";
	}

	// http://127.0.0.1:9003/iframe
	@GetMapping("/iframe")
	public String iframe() {
		return "forward:/chat/iframe.html";
	}

	// http://127.0.0.1:9003/agenticflow
	@GetMapping({
		"/agenticflow", 
		"/agenticflow/", 
		"/agenticflow/{path:[^\\.]*}", 
		"/agenticflow/{path:[^\\.]*}/{path2:[^\\.]*}"})
	public String agenticflow(@PathVariable(required = false) String path, 
							@PathVariable(required = false) String path2) {
		return "forward:/agenticflow/index.html"; // 默认路径
	}

	/**
	 * notebase - React SPA
	 * http://127.0.0.1:9003/notebase/spaces
	 * 
	 * 注意：
	 * 1. 静态资源请求（包含.的路径）会被 Spring 的资源处理器处理
	 * 2. 其他所有路径都转发到 index.html，由 React Router 处理
	 */
	@GetMapping({
		"/notebase",
		"/notebase/",
		"/notebase/{path:[^\\.]*}",
		"/notebase/{path:[^\\.]*}/{path2:[^\\.]*}"
	})
	public String notebase(@PathVariable(required = false) String path,
						 @PathVariable(required = false) String path2) {
		return "forward:/notebase/index.html";
	}

	/**
	 * http://127.0.0.1:9003/download
	 */
	@GetMapping({"/{page:dev|download|contact|about}", "/{page:download|contact|about}.html"})
	public String handlePageRoutes(@PathVariable String page) {
		return page;
	}
	
	

}
