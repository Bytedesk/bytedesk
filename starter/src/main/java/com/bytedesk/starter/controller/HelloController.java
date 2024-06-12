/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-29 16:26:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.uid.utils.NetUtils;
import com.bytedesk.core.utils.JsonResult;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
@Slf4j
@Tag(name = "hello - 测试")
@RestController
@RequestMapping("/hello")
public class HelloController {

	Counter visitCounter;

    public HelloController(MeterRegistry registry) {
        visitCounter = Counter.builder("visit_counter")
            .description("Number of visits to the site")
            .register(registry);
    }

	// http://localhost:9003/hello
	@GetMapping("")
	@Operation(summary = "hello world test")
	public Map<String, Object> greeting() {
		visitCounter.increment();
		log.error("log error");
		log.warn("log warn");
		log.info("log info");
		log.debug("log debug");
		log.trace("log trace");
		return Collections.singletonMap("message", "Hello, World");
	}

	// http://localhost:9003/hello/visits
	@GetMapping("/visits")
	public ResponseEntity<?> visitCount() {
		return ResponseEntity.ok(JsonResult.success("visitor counts",visitCounter.count()));
	}

	/**
	 * 设置session
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/set/session")
	public Object setSession(HttpServletRequest request, @RequestParam(value = "uid") final String uid) {
		request.getSession().setAttribute("uid", uid);
		return request.getSession().getAttribute("uid");
	}

	/**
	 * 获取session
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/get/session")
	public Object getSession(HttpServletRequest request) {
		return request.getSession().getAttribute("uid");
	}

	/**
	 * get server host ip
	 * http://localhost:9003/hello/host
	 * 
	 * @return
	 */
	@GetMapping("/host")
	public ResponseEntity<?> getHost() {
		String host = NetUtils.getLocalAddress();
		return ResponseEntity.ok(JsonResult.success("host", host));
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String moderatorAccess() {
		return "Admin Board.";
	}

	@GetMapping("/super")
	@PreAuthorize("hasRole('SUPER')")
	public String superAccess() {
		return "Super Board.";
	}

	// https://spring.io/guides/gs/rest-service-cors
	// private static final String template = "Hello, %s!";
	// private final AtomicLong counter = new AtomicLong();

	// // http://localhost:9003/greeting
	// @CrossOrigin(origins = "http://localhost:9000")
	// @GetMapping("/greeting")
	// public Greeting greeting(@RequestParam(required = false, defaultValue = "World") String name) {
	// 	log.info("==== get greeting ====");
	// 	return new Greeting(counter.incrementAndGet(), String.format(template, name));
	// }

	// // http://localhost:9003/greeting-javaconfig
	// @GetMapping("/greeting-javaconfig")
	// public Greeting greetingWithJavaconfig(@RequestParam(required = false, defaultValue = "World") String name) {
	// 	log.info("==== in greeting ====");
	// 	return new Greeting(counter.incrementAndGet(), String.format(template, name));
	// }

}
