/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-17 10:22:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-14 11:26:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * for testing api
 */
@Slf4j
@RestController
@RequestMapping("/jms")
@Tag(name = "JMS Management", description = "JMS Artemis queue and topic management APIs for testing")
public class JmsArtemisController {
    
    @Autowired
	private JmsArtemisService jmsArtemisService;

	@Autowired
	private BytedeskProperties bytedeskProperties;

    // http://127.0.0.1:9003/jms/artemis/queue
	@Operation(summary = "Test JMS Queue", description = "Test sending a message to JMS Artemis queue (debug mode only)")
	@GetMapping("/artemis/queue")
	public ResponseEntity<?> getJmsQueueArtemis() {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.status(503).body(JsonResult.error("debug is false"));
		}

		jmsArtemisService.testQueue();
		return ResponseEntity.ok(JsonResult.success("send artemis queue message success"));
 	}

	// http://127.0.0.1:9003/jms/artemis/topic
	@Operation(summary = "Test JMS Topic", description = "Test sending a message to JMS Artemis topic (debug mode only)")
	@GetMapping("/artemis/topic")
	public ResponseEntity<?> getJmsTopicArtemis() {
		if (!bytedeskProperties.getDebug()) {
			return ResponseEntity.status(503).body(JsonResult.error("debug is false"));
		}
		
		jmsArtemisService.testTopic();
		return ResponseEntity.ok(JsonResult.success("send artemis topic message success"));
 	}

}

