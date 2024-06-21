/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-18 16:02:53
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
// import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller for "/".
 *
 * @author jackning
 */
@Controller
public class PageRouteController {

	/**
	 * http://127.0.0.1:9003
	 */
	@GetMapping("/index")
	public String index() {
		return "index";
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

	// FIXME: 在管理后台刷新页面，无法正确路由到 admin/index.html
	// http://127.0.0.1:9003/admin/welcome
	// @GetMapping("/admin/**")
	// public String adminAll(HttpServletRequest request) {
	// // return "admin/index.html";
	// String requestURI = request.getRequestURI();
	// String staticResourcePath = "classpath:/templates/admin/" +
	// requestURI.substring(requestURI.indexOf("/admin"));
	// try {
	// // 尝试访问静态资源文件，如果文件不存在，将抛出异常
	// UrlResource resource = new UrlResource(staticResourcePath);
	// if (!resource.exists()) {
	// throw new RuntimeException("Static resource not found");
	// }
	// // 如果静态资源存在，可以将其内容作为响应返回，或者重定向到静态资源的URL
	// // 这里为了简化，我们假设直接返回静态资源视图名称
	// return "forward:" + staticResourcePath;
	// } catch (Exception e) {
	// // 静态资源不存在，回退到控制器方法逻辑
	// return "admin/index.html"; // 或者其他备用逻辑
	// }
	// }

	/**
	 * agent
	 * web聊天/客服端
	 * http://127.0.0.1:9003/agent
	 */
	@GetMapping("/agent")
	public String agent() {
		return "agent/index.html";
	}

	/**
	 * visitor
	 * 访客对话窗口
	 * http://127.0.0.1:9003/chat
	 */
	@GetMapping("/chat")
	public String chat() {
		return "chat/index.html";
	}

	// http://127.0.0.1:9003/frame
	@GetMapping("/frame")
	public String frameChat() {
		return "chat/index.html";
	}

	// http://127.0.0.1:9003/float
	@GetMapping("/float")
	public String floatChat() {
		return "chat/index.html";
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

	/**
	 * http://127.0.0.1:9003/about
	 */
	@GetMapping("/about")
	public String about() {
		return "about";
	}

	/**
	 * http://127.0.0.1:9003/contact
	 */
	@GetMapping("/contact")
	public String contact() {
		return "contact";
	}

}
