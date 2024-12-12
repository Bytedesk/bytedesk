/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-03 15:54:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
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
	@GetMapping("/index")
	public String index() {
		return "index";
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
	@GetMapping({"/admin", "/admin/", "/admin/{path:[^\\.]*}", "/admin/{path:[^\\.]*}/{path2:[^\\.]*}"})
	public String admin(@PathVariable(required = false) String path, @PathVariable(required = false) String path2) {
		if (path != null && path2 != null) {
			log.info("admin path: {}, {}", path, path2);
		} else if (path != null) {
			log.info("admin path: {}", path);
		}
		return "forward:/admin/index.html"; // 默认路径
	}

	/**
	 * agent
	 * web聊天/客服端
	 * http://127.0.0.1:9003/agent
	 */
	@GetMapping({"/agent", "/agent/", "/agent/{path:[^\\.]*}", "/agent/{path:[^\\.]*}/{path2:[^\\.]*}"})
	public String agent(@PathVariable(required = false) String path, @PathVariable(required = false) String path2) {
		if (path != null && path2 != null) {
			log.info("agent path: {}, {}", path, path2);
		} else if (path != null) {
			log.info("agent path: {}", path);
		}
		return "forward:/agent/index.html"; // 默认路径
	}

	/**
	 * visitor
	 * 访客对话窗口
	 * http://127.0.0.1:9003/chat
	 * http://127.0.0.1:9003/chat/demo
	 * http://127.0.0.1:9003/chat/frame
	 * http://127.0.0.1:9003/chat/float
	 */
	@GetMapping({"/chat", "/chat/", "/chat/{type:demo|frame|float|ticket|feedback|number|queue|center|helpcenter}"})
	public String handleChatRoutes(@PathVariable(required = false) String type) {
		return "/chat/index.html";
	}

	// http://127.0.0.1:9003/iframe
	@GetMapping("/iframe")
	public String iframe() {
		return "chat/iframe.html";
	}

	/**
	 * http://127.0.0.1:9003/download
	 */
	@GetMapping({"/{page:dev|download|contact|about}", "/{page:download|contact|about}.html"})
	public String handlePageRoutes(@PathVariable String page) {
		return page;
	}


	
	

}
