/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 15:02:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-01-31 17:25:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;

// import com.bytedesk.core.socket.mqtt.protocol.ProtocolProcess;
// import com.bytedesk.core.socket.mqtt.server.MqttServer;
// import com.bytedesk.core.socket.mqtt.server.MqttWebSocketServer;

// https://springdoc.cn/spring-boot-testing-tutorial/
@SpringBootTest
class StarterApplicationTests {

	// 排除下面三个 bean，运行测试时，不运行
	// @MockBean
	// private MqttServer mqttServer;

	// @MockBean
	// private MqttWebSocketServer mqttWebSocketServer;

	// @MockBean
	// private ProtocolProcess process;

	@Test
	void contextLoads() {
	}

}
