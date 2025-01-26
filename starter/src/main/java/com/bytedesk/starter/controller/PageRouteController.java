/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-01-24 23:12:50
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
	public String index() {
		return "dev";
	}
	
	@GetMapping("/en")
	public String indexEn() {
		return "en/index";
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

	// http://127.0.0.1:9003/agentflow
	@GetMapping({
		"/agentflow", 
		"/agentflow/", 
		"/agentflow/{path:[^\\.]*}", 
		"/agentflow/{path:[^\\.]*}/{path2:[^\\.]*}"})
	public String agentflow(@PathVariable(required = false) String path, 
							@PathVariable(required = false) String path2) {
		return "forward:/agentflow/index.html"; // 默认路径
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
