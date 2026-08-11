/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-10 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-10 22:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.adviser;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;

import lombok.extern.slf4j.Slf4j;

/**
 * 仅用于演示 Advisor 执行顺序：在 before/after 中向控制台打印标签。
 *
 * <p>配合两个不同 order 的实例（如 {@code new TagAdvisor("A", 0)}、
 * {@code new TagAdvisor("B", 1)}）可以直观观察：</p>
 * <ul>
 *   <li>请求阶段：order 小的先执行 {@code before}</li>
 *   <li>响应阶段：order 小的后执行 {@code after}（栈式回溯）</li>
 * </ul>
 */
@Slf4j
public class TagAdvisor implements BaseAdvisor {

	private final String tag;

	private final int order;

	public TagAdvisor(String tag, int order) {
		this.tag = tag;
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public String getName() {
		return "TagAdvisor-" + this.tag;
	}

	@Override
	public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
		log.info("➡️  [TagAdvisor-{}] before  (order={})", this.tag, this.order);
		return chatClientRequest;
	}

	@Override
	public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
		log.info("⬅️  [TagAdvisor-{}] after   (order={})", this.tag, this.order);
		return chatClientResponse;
	}

}
