/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-30 08:56:05
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

/**
 * Controller for "/".
 *
 * @author jackning
 */
@Slf4j
@Controller
public class PageRouteController {

    @Value("${bytedesk.custom.show-demo:true}")
    private Boolean showDemo;
    
    @Value("${bytedesk.custom.enabled:false}")
    private Boolean customEnabled;
    
    @Value("${bytedesk.custom.name:微语}")
    private String customName;
    
    @Value("${bytedesk.custom.logo:https://www.weiyuai.cn/logo.png}")
    private String customLogo;
    
    @Value("${bytedesk.custom.description:重复工作自动化}")
    private String customDescription;

	/**
	 * 微语首页
	 * http://127.0.0.1:9003
	 * http://127.0.0.1:9003/home
	 * 多语言支持:
	 * http://127.0.0.1:9003/zh-TW/index.html
	 * http://127.0.0.1:9003/en/index.html
	 */
	@GetMapping({ "/", "/home" })
	public String home(Model model) {
		if (!showDemo) {
			// 添加自定义配置到模型
			if (customEnabled) {
				model.addAttribute("customName", customName);
				model.addAttribute("customLogo", customLogo);
				model.addAttribute("customDescription", customDescription);
			}
			return "default";
		}
		model.addAttribute("title", "微语");
		model.addAttribute("chatUrl", "/chat/home");
		return "home";
	}

	/**
	 * Multi-language static HTML support
	 * http://127.0.0.1:9003/zh-TW/index.html
	 * http://127.0.0.1:9003/en/index.html
	 * http://127.0.0.1:9003/zh-TW/features/team.html
	 * http://127.0.0.1:9003/en/pages/about.html
	 */
	@GetMapping({
		"/{lang:zh-CN|zh-TW|en}/index.html",
		"/{lang:zh-CN|zh-TW|en}/features/{feature:office|scrm|team|ai|kbase|voc|ticket|workflow|kanban|callcenter|video|service|open}.html",
		"/{lang:zh-CN|zh-TW|en}/pages/{page:download|contact|about|privacy|terms}.html"
	})
	public String multiLanguageStaticPages(
			@PathVariable String lang,
			@PathVariable(required = false) String feature,
			@PathVariable(required = false) String page,
			Model model) {
		
		// Add lang to model for template processing
		model.addAttribute("lang", lang);
		
		// Forward to the actual static HTML file in templates/{lang}/ directory
		if (feature != null) {
			return "forward:/templates/" + lang + "/features/" + feature + ".html";
		} else if (page != null) {
			return "forward:/templates/" + lang + "/pages/" + page + ".html";
		} else {
			return "forward:/templates/" + lang + "/index.html";
		}
	}

	/**
	 * Root static HTML pages for zh-CN (default language)
	 * http://127.0.0.1:9003/index.html
	 * http://127.0.0.1:9003/features/team.html
	 * http://127.0.0.1:9003/pages/about.html
	 */
	@GetMapping({
		"/index.html",
		"/features/{feature:office|scrm|team|ai|kbase|voc|ticket|workflow|kanban|callcenter|video|service|open}.html",
		"/pages/{page:download|contact|about|privacy|terms}.html"
	})
	public String rootStaticPages(
			@PathVariable(required = false) String feature,
			@PathVariable(required = false) String page,
			Model model) {
		
		// Default language is zh-CN
		model.addAttribute("lang", "zh-CN");
		
		// Forward to the actual static HTML file in templates/ directory
		if (feature != null) {
			return "forward:/templates/features/" + feature + ".html";
		} else if (page != null) {
			return "forward:/templates/pages/" + page + ".html";
		} else {
			return "forward:/templates/index.html";
		}
	}

	/**
	 * 
	 * http://127.0.0.1:9003/web
	 */
	@GetMapping("/web")
	public String web(Model model) {
		if (!showDemo) {
			// 添加自定义配置到模型
			if (customEnabled) {
				model.addAttribute("customName", customName);
				model.addAttribute("customLogo", customLogo);
				model.addAttribute("customDescription", customDescription);
			}
			return "default";
		}
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
		"/docs/{lang:zh-CN|zh-TW}/{path:[^\\.]*}/{path2:[^\\.]*}",
		"/docs/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\\\.]*}",
		"/docs/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\\\.]*}/{path4:[^\\\\\\\\.]*}",
		"/docs/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\\\.]*}/{path4:[^\\\\\\\\.]*}/{path5:[^\\\\\\\\\\\\\\\\.]*}",
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
	 * apidocs 文档
	 * 支持多语言路径：/apidocs, /apidocs/zh-CN, /apidocs/zh-TW
	 * http://127.0.0.1:9003/apidocs
	 */
	@GetMapping({
		"/apidocs",
		"/apidocs/",
		"/apidocs/{lang:zh-CN|zh-TW}",
		"/apidocs/{lang:zh-CN|zh-TW}/",
		"/apidocs/{path:[^\\.]*}",
		"/apidocs/{path:[^\\.]*}/{path2:[^\\.]*}",
		"/apidocs/{lang:zh-CN|zh-TW}/{path:[^\\.]*}",
		"/apidocs/{lang:zh-CN|zh-TW}/{path:[^\\.]*}/{path2:[^\\.]*}",
		"/apidocs/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\\\.]*}",
		"/apidocs/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\\\.]*}/{path4:[^\\\\\\\\.]*}",
		"/apidocs/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\\\.]*}/{path4:[^\\\\\\\\.]*}/{path5:[^\\\\\\\\\\\\\\\\.]*}",
	})
	public String apidocs(
			@PathVariable(required = false) String lang,
			@PathVariable(required = false) String path,
			@PathVariable(required = false) String path2) {

		// 如果指定了语言，则使用对应语言的入口页面
		if (lang != null) {
			return "forward:/apidocs/" + lang + "/index.html";
		}

		// 默认使用英文入口页面
		return "forward:/apidocs/index.html";
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
			"/admin/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}",
			"/admin/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}/{path4:[^\\.]*}"
		})
	public String admin() {
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
			"/agent/{path:[^\\.]*}/{path2:[^\\.]*}",
			"/agent/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}",
			"/agent/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}/{path4:[^\\.]*}"
		})
	public String agent(@PathVariable(required = false) String path) {
		return "forward:/agent/index.html"; // 默认路径
	}

	/**
	 * visitor
	 * 访客对话窗口
	 * http://127.0.0.1:9003/chat/demo
	 */
	// 特定的chat/demo路径，放在通用chat路径之前
	@GetMapping("/chat/demo")
	public String chatDemo(Model model) {
		if (!showDemo) {
			// 添加自定义配置到模型
			if (customEnabled) {
				model.addAttribute("customName", customName);
				model.addAttribute("customLogo", customLogo);
				model.addAttribute("customDescription", customDescription);
			}
			return "default";
		}
		return "forward:/chat/index.html"; // 默认路径
	}

	@GetMapping({
			"/chat",
			"/chat/",
			"/chat/{path:[^\\.]*}",
			"/chat/{path:[^\\.]*}/{path2:[^\\.]*}",
			"/chat/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}",
			"/chat/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}/{path4:[^\\.]*}"
		})
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
			"/agenticflow/{path:[^\\.]*}/{path2:[^\\.]*}",
			"/agenticflow/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}",
			"/agenticflow/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}/{path4:[^\\.]*}"
		})
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
			"/notebase/{path:[^\\.]*}/{path2:[^\\.]*}",
			"/notebase/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}",
			"/notebase/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}/{path4:[^\\.]*}"
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
			"/kanban/{path:[^\\.]*}/{path2:[^\\.]*}",
			"/kanban/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}",
			"/kanban/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}/{path4:[^\\.]*}"
	})
	public String kanban(@PathVariable(required = false) String path,
			@PathVariable(required = false) String path2) {
		return "forward:/kanban/index.html";
	}

	@GetMapping({
			"/reactdemo",
			"/reactdemo/",
			"/reactdemo/{path:[^\\.]*}",
			"/reactdemo/{path:[^\\.]*}/{path2:[^\\.]*}",
			"/reactdemo/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}",
			"/reactdemo/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}/{path4:[^\\.]*}"
	})
	public String reactdemo(@PathVariable(required = false) String path,
			@PathVariable(required = false) String path2) {
		return "forward:/reactdemo/index.html";
	}

	@GetMapping({
			"/forum",
			"/forum/",
			"/forum/{path:[^\\.]*}",
			"/forum/{path:[^\\.]*}/{path2:[^\\.]*}",
			"/forum/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}",
			"/forum/{path:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}/{path4:[^\\.]*}"
	})
	public String forum(@PathVariable(required = false) String path,
			@PathVariable(required = false) String path2) {
		return "forward:/forum/index.html";
	}

	/**
	 * Features pages - support both with and without /features/ prefix
	 * For dynamic FTL template rendering (non-.html requests)
	 * http://127.0.0.1:9003/office
	 * http://127.0.0.1:9003/features/office
	 */
	@GetMapping({ 
		"/{feature:office|scrm|team|ai|kbase|voc|ticket|workflow|kanban|callcenter|video|service|open}", 
		"/features/{feature:office|scrm|team|ai|kbase|voc|ticket|workflow|kanban|callcenter|video|service|open}"
	})
	public String handleFeatureRoutes(
			@PathVariable(required = false) String feature, 
			Model model) {
		if (!showDemo) {
			// 添加自定义配置到模型
			if (customEnabled) {
				model.addAttribute("customName", customName);
				model.addAttribute("customLogo", customLogo);
				model.addAttribute("customDescription", customDescription);
			}
			return "default";
		}
		
		return "features/" + feature;
	}

	/**
	 * Pages - support both with and without /pages/ prefix
	 * For dynamic FTL template rendering (non-.html requests)
	 * http://127.0.0.1:9003/download
	 * http://127.0.0.1:9003/pages/download
	 */
	@GetMapping({ 
		"/{page:download|contact|about|privacy|terms}", 
		"/pages/{page:download|contact|about|privacy|terms}"
	})
	public String handlePageRoutes(@PathVariable String page, Model model) {
		if (!showDemo) {
			// 添加自定义配置到模型
			if (customEnabled) {
				model.addAttribute("customName", customName);
				model.addAttribute("customLogo", customLogo);
				model.addAttribute("customDescription", customDescription);
			}
			return "default";
		}
		return "pages/" + page;
	}

}
