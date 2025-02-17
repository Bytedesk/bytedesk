/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-13 10:25:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-13 10:26:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.advisor;

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisorChain;
import org.springframework.ai.chat.model.MessageAggregator;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Advisor for logging the request and response of the chat client.
 * https://docs.spring.io/spring-ai/reference/api/advisors.html#_logging_advisor
 */
@Slf4j
public class SimpleLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

	@Override
	public String getName() { 
		return this.getClass().getSimpleName();
	}

	@Override
	public int getOrder() { 
		return 0;
	}

	@Override
	public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {

		log.debug("BEFORE: {}", advisedRequest);

		AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);

		log.debug("AFTER: {}", advisedResponse);

		return advisedResponse;
	}

	@Override
	public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {

		log.debug("BEFORE: {}", advisedRequest);

		Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);

        return new MessageAggregator().aggregateAdvisedResponse(advisedResponses,
                    advisedResponse -> log.debug("AFTER: {}", advisedResponse)); 
	}
}
