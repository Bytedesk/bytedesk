/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-24 22:38:06
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.uid.utils.NetUtils;
import com.bytedesk.core.utils.BdPinyinUtils;
import com.bytedesk.core.utils.JsonResult;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * for testing api
 * http://127.0.0.1:9003/swagger-ui/index.html
 */
@Slf4j
@Tag(name = "hello - 测试")
@RestController
@RequestMapping("/hello")
public class HelloMvcController {

	Counter visitCounter;

	public HelloMvcController(MeterRegistry registry) {
		visitCounter = Counter.builder("visit_counter")
				.description("Number of visits to the site")
				.register(registry);
	}

	// http://127.0.0.1:9003/hello
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

	// http://127.0.0.1:9003/hello/visits
	@GetMapping("/visits")
	public ResponseEntity<?> visitCount() {
		visitCounter.increment();
		return ResponseEntity.ok(JsonResult.success("visitor counts", visitCounter.count()));
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
	 * http://127.0.0.1:9003/hello/host
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

	// http://127.0.0.1:9003/hello/callback
	@PostMapping("/callback")
	public ResponseEntity<?> getCallback(@RequestBody String requestBody) {
		log.info("hello callback {}", requestBody);
		return ResponseEntity.ok(JsonResult.success("callback", requestBody));
	}

	// http://127.0.0.1:9003/hello/local/time
	@GetMapping("/local/time")
	public MessageProtobuf getExampleMessage() {
		MessageProtobuf message = new MessageProtobuf();
		message.setContent("Hello, World!");
		return message;
	}

	//
	/**
	 * 将中文转为普通格式拼音（不带声调）
	 * http://127.0.0.0:9003/pinyin/normal?text=你好世界
	 * 
	 * @param text 需要转换的中文文本
	 * @return 转换结果
	 */
	@GetMapping("/pinyin/normal")
	public Map<String, String> normalPinyin(@RequestParam String text) {
		Map<String, String> result = new HashMap<>();
		result.put("original", text);
		result.put("pinyin", BdPinyinUtils.toPinYin(text));
		return result;
	}

	/**
	 * 将中文转为带声调的拼音
	 * http://127.0.0.0:9003/pinyin/tone?text=你好世界
	 * 
	 * @param text 需要转换的中文文本
	 * @return 转换结果
	 */
	@GetMapping("/pinyin/tone")
	public Map<String, String> toneStylePinyin(@RequestParam String text) {
		Map<String, String> result = new HashMap<>();
		result.put("original", text);
		result.put("pinyin", BdPinyinUtils.toPinyinWithShenDiao(text));
		return result;
	}

	/**
	 * 获取单个汉字的多音字列表
	 * http://127.0.0.0:9003/pinyin/multiple?character=重
	 * 
	 * @param character 单个汉字
	 * @return 多音字列表
	 */
	@GetMapping("/pinyin/multiple")
	public Map<String, Object> multiplePinyin(@RequestParam String character) {
		Map<String, Object> result = new HashMap<>();
		if (character != null && character.length() > 0) {
			char c = character.charAt(0);
			List<String> pinyinList = BdPinyinUtils.toPinyinList(c);
			result.put("character", character.substring(0, 1));
			result.put("pinyinList", pinyinList);
		} else {
			result.put("error", "请提供一个汉字");
		}
		return result;
	}

	/**
	 * 将中文转为首字母格式
	 * http://127.0.0.0:9003/pinyin/firstletter?text=你好世界
	 * 
	 * @param text 需要转换的中文文本
	 * @return 转换结果
	 */
	@GetMapping("/firstletter")
	public Map<String, String> firstLetterPinyin(@RequestParam String text) {
		Map<String, String> result = new HashMap<>();
		result.put("original", text);
		result.put("pinyin", BdPinyinUtils.firstLetterStyle(text));
		return result;
	}

	// https://spring.io/guides/gs/rest-service-cors
	// private static final String template = "Hello, %s!";
	// private final AtomicLong counter = new AtomicLong();

	// // http://127.0.0.1:9003/greeting
	// @CrossOrigin(origins = "http://127.0.0.1:9000")
	// @GetMapping("/greeting")
	// public Greeting greeting(@RequestParam(required = false, defaultValue =
	// "World") String name) {
	// log.info("==== get greeting ====");
	// return new Greeting(counter.incrementAndGet(), String.format(template,
	// name));
	// }

	// // http://127.0.0.1:9003/greeting-javaconfig
	// @GetMapping("/greeting-javaconfig")
	// public Greeting greetingWithJavaconfig(@RequestParam(required = false,
	// defaultValue = "World") String name) {
	// log.info("==== in greeting ====");
	// return new Greeting(counter.incrementAndGet(), String.format(template,
	// name));
	// }

}
