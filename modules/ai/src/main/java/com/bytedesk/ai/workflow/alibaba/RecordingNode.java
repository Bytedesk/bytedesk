/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-14 11:17:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-29 16:21:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow.alibaba;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecordingNode implements NodeAction {

	@Override
	public Map<String, Object> apply(OverAllState state) throws Exception {

		String feedback = (String) state.value("classifier_output").get();

		Map<String, Object> updatedState = new HashMap<>();
		if (feedback.contains("positive")) {
			log.info("Received positive feedback: {}", feedback);
			updatedState.put("solution", "Praise, no action taken.");
		}
		else {
			log.info("Received negative feedback: {}", feedback);
			updatedState.put("solution", feedback);
		}

		return updatedState;
	}

}
