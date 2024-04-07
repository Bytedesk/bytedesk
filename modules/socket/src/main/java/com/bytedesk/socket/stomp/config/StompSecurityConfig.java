// /*
//  * @Author: jackning 270580156@qq.com
//  * @Date: 2024-01-29 16:21:46
//  * @LastEditors: jackning 270580156@qq.com
//  * @LastEditTime: 2024-04-04 15:27:32
//  * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
//  *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
//  *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
//  *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
//  *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
//  *  contact: 270580156@qq.com 
//  *  联系：270580156@qq.com
//  * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
//  */
// package com.bytedesk.socket.stomp.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.messaging.Message;
// import org.springframework.security.authorization.AuthorizationManager;
// import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
// import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

// /**
//  * https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html
//  * 
//  * @author bytedesk.com
//  */
// @Configuration
// @EnableWebSocketSecurity
// public class StompSecurityConfig {

//     @Bean
//     AuthorizationManager<Message<?>> messageAuthorizationManager(
//             MessageMatcherDelegatingAuthorizationManager.Builder messages) {
//         messages
//                 .simpDestMatchers("/**").permitAll();

//         return messages.build();
//     }
// }
