/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-28 17:29:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;

/**
 * Controller for "/".
 *
 * @author jackning
 */
@Slf4j
@Controller
public class PageRouteController {

	/**
	 * 微语首页
	 * http://127.0.0.1:9003
	 * http://127.0.0.1:9003/home
	 */
	@GetMapping({ "/", "/home" })
	public String home(Model model) {
		model.addAttribute("title", "微语");
		model.addAttribute("chatUrl", "/chat/home");
		return "home";
		// return "quanjing";
	}

	/**
	 * http://127.0.0.1:9003
	 */
	@GetMapping({"/dev", "/index"})
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
			en.put("notebase", "Knowledge Base");
			en.put("kanban", "Project Management");
			en.put("javaDoc", "Java Docs");
			en.put("apiDoc", "Swagger API Docs");
			en.put("knife4jDoc", "Knife4j API Docs");
			en.put("docs", "Docs");
			en.put("monitoring", "Monitoring");
			en.put("voiceOfCustomer", "Voice of Customer");
			en.put("kanban", "Project kanban");
			en.put("moduleDevelopment", "Module Development");
			en.put("forum", "Forum");
			en.put("ticket", "Ticket");
			en.put("team", "Team");
			en.put("service", "Service");
			en.put("kbase", "Kbase");
			en.put("ai", "AI");
			en.put("demo", "Demo");
			en.put("social", "Social");
			en.put("airline", "Airline");
			en.put("bytedesk", "BytedeskAI");
			en.put("shopping", "Shopping");

			// 简体中文
			Map<String, String> zh = new HashMap<>();
			zh.put("title", "微语");
			zh.put("systemEntrance", "系统入口");
			zh.put("systemDevelopment", "开发工具");
			zh.put("adminDashboard", "管理后台");
			zh.put("agentClient", "客服工作台");
			zh.put("visitorChat", "访客对话");
			zh.put("workFlow", "工作流");
			zh.put("notebase", "知识库");
			zh.put("kanban", "项目管理");
			zh.put("javaDoc", "Java 文档");
			zh.put("apiDoc", "Swagger API 文档");
			zh.put("knife4jDoc", "Knife4j API 文档");
			zh.put("docs", "文档");
			zh.put("monitoring", "系统监控");
			zh.put("voiceOfCustomer", "客户之声");
			zh.put("kanban", "项目看板");
			zh.put("moduleDevelopment", "模块简介");
			zh.put("forum", "用户社区");
			zh.put("ticket", "工单模块");
			zh.put("team", "企业IM模块");
			zh.put("service", "客服模块");
			zh.put("kbase", "知识库模块");
			zh.put("ai", "AI模块");
			zh.put("social", "社交IM模块");
			zh.put("demo", "演示");
			zh.put("airline", "机票购票/退改签演示");
			zh.put("bytedesk", "微语文档问答演示");
			zh.put("shopping", "购物商品推荐/退换货演示");
			// 繁体中文
			Map<String, String> zhTW = new HashMap<>();
			zhTW.put("title", "微語");
			zhTW.put("systemEntrance", "系統入口");
			zhTW.put("systemDevelopment", "開發工具");
			zhTW.put("adminDashboard", "管理後台");
			zhTW.put("agentClient", "客服工作台");
			zhTW.put("visitorChat", "訪客對話");
			zhTW.put("workFlow", "工作流");
			zhTW.put("notebase", "知識庫");
			zhTW.put("kanban", "項目管理");
			zhTW.put("javaDoc", "Java 文檔");
			zhTW.put("apiDoc", "Swagger API 文檔");
			zhTW.put("knife4jDoc", "Knife4j API 文檔");
			zhTW.put("docs", "文檔");
			zhTW.put("monitoring", "系統監控");
			zhTW.put("voiceOfCustomer", "客戶之聲");
			zhTW.put("kanban", "項目看板");
			zhTW.put("moduleDevelopment", "模組簡介");
			zhTW.put("forum", "用戶社區");
			zhTW.put("ticket", "工單模組");
			zhTW.put("team", "企業IM模組");
			zhTW.put("service", "客服模組");
			zhTW.put("kbase", "知識庫模組");
			zhTW.put("ai", "AI模組");
			zhTW.put("social", "社交IM模組");
			zhTW.put("demo", "演示");
			zhTW.put("airline", "機票購票/退改簽演示");
			zhTW.put("bytedesk", "微語文檔問答演示");
			zhTW.put("shopping", "購物商品推薦/退換貨演示");

			// 添加多语言文本
			i18n.put("en", en);
			i18n.put("zh", zh);
			i18n.put("zh-TW", zhTW);

			model.addAttribute("i18n", i18n);

			// 添加调试信息
			// log.info("Languages: {}", languages);
			// log.info("I18n: {}", i18n);

			return "dev";
		} catch (Exception e) {
			log.error("Error rendering index page", e);
			throw e;
		}
	}

	/**
	 * 
	 * http://127.0.0.1:9003/web
	 */
	@GetMapping("/web")
	public String web() {
		return "index";
	}

	/**
	 * docusaurus 文档
	 * 支持多语言路径：/docs, /docs/zh-CN, /docs/zh-TW
	 * http://127.0.0.1:9003/docs
	 */
	@GetMapping({
			"/docs",
			"/docs/",
			"/docs/{lang:zh-CN|zh-TW}",
			"/docs/{lang:zh-CN|zh-TW}/",
			"/docs/{path:[^\\.]*}",
			"/docs/{path:[^\\.]*}/{path2:[^\\.]*}",
			"/docs/{lang:zh-CN|zh-TW}/{path:[^\\.]*}",
			"/docs/{lang:zh-CN|zh-TW}/{path:[^\\.]*}/{path2:[^\\.]*}"
	})
	public String docs(
			@PathVariable(required = false) String lang,
			@PathVariable(required = false) String path,
			@PathVariable(required = false) String path2) {

		// 如果指定了语言，则使用对应语言的入口页面
		if (lang != null) {
			return "forward:/docs/" + lang + "/index.html";
		}

		// 默认使用英文入口页面
		return "forward:/docs/index.html";
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
			"/admin/{path:[^\\.]*}/{path2:[^\\.]*}",
			"/admin/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}" 
		})
	public String admin(
		// @PathVariable(required = false) String path, 
		// @PathVariable(required = false) String path2, 
		// @PathVariable(required = false) String path3
		) {
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
			"/agent/{path:[^\\.]*}/{path2:[^\\.]*}"
		})
	public String agent(
		@PathVariable(required = false) String path, 
		@PathVariable(required = false) String path2
		) {
		return "forward:/agent/index.html"; // 默认路径
	}

	/**
	 * visitor
	 * 访客对话窗口
	 * http://127.0.0.1:9003/chat/
	 */
	@GetMapping({
			"/chat",
			"/chat/",
			"/chat/home",
			"/chat/demo/airline",
			"/chat/demo/bytedesk",
			"/chat/demo/shopping",
			"/chat/{type:demo|frame|float|ticket|feedback|number|queue|center|helpcenter|server|config}" })
	public String chat() {
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
			"/agenticflow/{path:[^\\.]*}/{path2:[^\\.]*}" })
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
	 * kanban
	 * 看板
	 * http://127.0.0.1:9003/kanban
	 */
	@GetMapping({
			"/kanban",
			"/kanban/",
			"/kanban/{path:[^\\.]*}",
			"/kanban/{path:[^\\.]*}/{path2:[^\\.]*}"
	})
	public String kanban(@PathVariable(required = false) String path,
			@PathVariable(required = false) String path2) {
		return "forward:/kanban/index.html";
	}


	@GetMapping({
			"/reactdemo",
			"/reactdemo/",
			"/reactdemo/{path:[^\\.]*}",
			"/reactdemo/{path:[^\\.]*}/{path2:[^\\.]*}"
	})
	public String reactdemo(@PathVariable(required = false) String path,
			@PathVariable(required = false) String path2) {
		return "forward:/reactdemo/index.html";
	}

	/**
	 * http://127.0.0.1:9003/download
	 * http://127.0.0.1:9003/privacy
	 * http://127.0.0.1:9003/protocol
	 */
	@GetMapping({ "/{page:download|contact|about|privacy|protocol}", "/{page:download|contact|about|privacy|protocol}.html" })
	public String handlePageRoutes(@PathVariable String page) {
		return page;
	}

}
