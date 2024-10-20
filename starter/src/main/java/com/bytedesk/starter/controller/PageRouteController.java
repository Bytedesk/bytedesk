/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-16 10:26:14
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
	@GetMapping("/admin")
	public String admin() {
		return "admin/index.html";
	}
	@GetMapping(value = "/admin/{path:[^\\.]*}")
    public String adminRedirect(@PathVariable String path) {
		log.info("admin path: {}", path);
		return "forward:/admin/index.html"; // 默认路径
    }
	@GetMapping(value = "/admin/{path:[^\\.]*}/{path2:[^\\\\.]*}")
    public String adminRedirect2(@PathVariable String path, @PathVariable String path2) {
		log.info("admin path: {}, {}", path, path2);
		return "forward:/admin/index.html"; // 默认路径
    }

	/**
	 * agent
	 * web聊天/客服端
	 * http://127.0.0.1:9003/agent
	 */
	@GetMapping("/agent")
	public String agent() {
		return "agent/index.html";
	}
	@GetMapping(value = "/agent/{path:[^\\.]*}")
    public String agentRedirect(@PathVariable String path) {
		log.info("agent path: {}", path);
		return "forward:/agent/index.html"; // 默认路径
    }
	@GetMapping(value = "/agent/{path:[^\\.]*}/{path2:[^\\\\.]*}")
    public String agentRedirect2(@PathVariable String path, @PathVariable String path2) {
		log.info("agent path: {}, {}", path, path2);
        // if ("index.html".equals(path)) {
		// 	return "forward:/agent/index.html";
		// }
		return "forward:/agent/index.html"; // 默认路径
    }

	/**
	 * visitor
	 * 访客对话窗口
	 * http://127.0.0.1:9003/chat
	 * http://127.0.0.1:9003/chat/frame
	 * http://127.0.0.1:9003/chat/float
	 */
	@GetMapping("/chat")
	public String chat() {
		return "/chat/index.html";
	}
	@GetMapping("/chat/")
	public String chatSlash() {
		return "/chat/index.html";
	}

	// http://127.0.0.1:9003/chat/frame
	@GetMapping("/chat/frame")
	public String frameChat() {
		return "/chat/index.html";
	}

	// http://127.0.0.1:9003/chat/float
	@GetMapping("/chat/float")
	public String floatChat() {
		return "/chat/index.html";
	}

	// http://127.0.0.1:9003/iframe
	@GetMapping("/iframe")
	public String iframe() {
		return "chat/iframe.html";
	}

	/**
	 * page for development
	 * http://127.0.0.1:9003/dev
	 */
	@GetMapping("/dev")
	public String dev() {
		return "dev";
	}

	/**
	 * http://127.0.0.1:9003/download
	 */
	@GetMapping("/download")
	public String download() {
		return "download";
	}

	@GetMapping("/download.html")
	public String downloadHtml() {
		return "download";
	}

	/**
	 * http://127.0.0.1:9003/about
	 */
	@GetMapping("/about")
	public String about() {
		return "about";
	}

	@GetMapping("/about.html")
	public String aboutHtml() {
		return "about";
	}

	/**
	 * http://127.0.0.1:9003/contact
	 */
	@GetMapping("/contact")
	public String contact() {
		return "contact";
	}
	
	@GetMapping("/contact.html")
	public String contactHtml() {
		return "contact";
	}

}
