/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 13:36:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-14 19:29:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
@Description("XSS Filter Configuration - Cross-site scripting protection filter configuration")
public class XSSFilterConfig {

	// @Bean
	// public FilterRegistrationBean<XSSFilter> xssFilter() {

	// 	FilterRegistrationBean<XSSFilter> registrationBean = new FilterRegistrationBean<>();

	// 	registrationBean.setFilter(new XSSFilter());
	// 	registrationBean.addUrlPatterns("/*");

	// 	return registrationBean;
	// }

}
