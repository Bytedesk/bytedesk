/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-13 13:15:00
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

/**
 * Controller for "/".
 *
 * @author jackning
 */
@Controller
public class IndexController {

	/**
	 * http://localhost:9003
	 */
	@GetMapping("/index")
	public String index() {
		return "index";
	}

	/**
	 * http://localhost:9003/admin
	 */
	@GetMapping("/admin")
	public String admin() {
		return "admin/index.html";
	}

	/**
	 * page for development
	 * http://localhost:9003/dev
	 */
	@GetMapping("/dev")
	public String dev() {
		return "dev";
	}

	/**
	 * http://localhost:9003/download
	 */
	@GetMapping("/download")
	public String download() {
		return "download";
	}

	/**
	 * http://localhost:9003/about
	 */
	@GetMapping("/about")
	public String about() {
		return "about";
	}

	/**
	 * http://localhost:9003/contact
	 */
	@GetMapping("/contact")
	public String contact() {
		return "contact";
	}


}
