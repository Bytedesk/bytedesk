/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-13 10:30:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-13 10:31:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisorChain;

import reactor.core.publisher.Flux;

/**
 * Re-Reading (Re2) Advisor
 * https://docs.spring.io/spring-ai/reference/api/advisors.html#_re_reading_re2_advisor
 * https://arxiv.org/pdf/2309.06275
 */
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

	private AdvisedRequest before(AdvisedRequest advisedRequest) { 

		Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());
		advisedUserParams.put("re2_input_query", advisedRequest.userText());

		return AdvisedRequest.from(advisedRequest)
			.userText("""
			    {re2_input_query}
			    Read the question again: {re2_input_query}
			    """)
			.userParams(advisedUserParams)
			.build();
	}

	@Override
	public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) { 
		return chain.nextAroundCall(this.before(advisedRequest));
	}

	@Override
	public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) { 
		return chain.nextAroundStream(this.before(advisedRequest));
	}

	@Override
	public int getOrder() { 
		return 0;
	}

    @Override
    public String getName() { 
		return this.getClass().getSimpleName();
	}
}
