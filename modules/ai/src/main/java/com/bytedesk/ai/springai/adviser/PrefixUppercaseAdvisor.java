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
 * 演示用自定义 {@link BaseAdvisor}：在请求前加前缀，在响应后按需记录信息。
 *
 * <p>{@code before} 阶段为用户输入拼接 {@code prefix}，{@code after} 阶段当
 * {@code uppercase=true} 时记录响应长度（仅观察，不实际改写响应内容）。</p>
 *
 * <p>演示 Advisor 可同时改写请求与观察响应的能力。</p>
 */
@Slf4j
public class PrefixUppercaseAdvisor implements BaseAdvisor {

	private final String prefix;

	private final boolean uppercase;

	private final int order;

	public PrefixUppercaseAdvisor(String prefix, boolean uppercase, int order) {
		this.prefix = prefix == null ? "" : prefix;
		this.uppercase = uppercase;
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public String getName() {
		return "PrefixUppercaseAdvisor";
	}

	@Override
	public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
		String origin = chatClientRequest.prompt().getUserMessage().getText();
		String augmented = this.prefix + (origin == null ? "" : origin);
		log.info("[advisor] {} before: prefix='{}'", getName(), this.prefix);
		return chatClientRequest.mutate().prompt(chatClientRequest.prompt().augmentUserMessage(augmented)).build();
	}

	@Override
	public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
		if (this.uppercase && chatClientResponse.chatResponse() != null
				&& chatClientResponse.chatResponse().getResult() != null
				&& chatClientResponse.chatResponse().getResult().getOutput() != null) {
			String text = chatClientResponse.chatResponse().getResult().getOutput().getText();
			if (text != null) {
				log.info("[advisor] {} after: uppercase flag=true, response length={}", getName(), text.length());
			}
		}
		return chatClientResponse;
	}

}
