/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 10:50:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-06 09:46:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GreetingIntegrationTests {

	// @Autowired
	// private TestRestTemplate restTemplate;

	// @Test
	// public void corsWithAnnotation() throws Exception {
	// 	ResponseEntity<Greeting> entity = this.restTemplate.exchange(
	// 			RequestEntity.get(uri("/greeting")).header(HttpHeaders.ORIGIN, "http://localhost:9000").build(),
	// 			Greeting.class);
	// 	assertEquals(HttpStatus.OK, entity.getStatusCode());
	// 	assertEquals("http://localhost:9000", entity.getHeaders().getAccessControlAllowOrigin());
	// 	Greeting greeting = entity.getBody();
	// 	assertEquals("Hello, World!", greeting.getContent());
	// }

	// @Test
	// public void corsWithJavaconfig() {
	// 	ResponseEntity<Greeting> entity = this.restTemplate.exchange(RequestEntity.get(uri("/greeting-javaconfig"))
	// 			.header(HttpHeaders.ORIGIN, "http://localhost:9000").build(), Greeting.class);
	// 	assertEquals(HttpStatus.OK, entity.getStatusCode());
	// 	assertEquals("http://localhost:9000", entity.getHeaders().getAccessControlAllowOrigin());
	// 	Greeting greeting = entity.getBody();
	// 	assertEquals("Hello, World!", greeting.getContent());
	// }

	// private URI uri(String path) {
	// 	return restTemplate.getRestTemplate().getUriTemplateHandler().expand(path);
	// }

}
