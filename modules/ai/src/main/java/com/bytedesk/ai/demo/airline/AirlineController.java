/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 10:54:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 18:29:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.demo.airline;

// import java.time.LocalDate;
// import java.util.List;

// import org.springframework.ai.chat.client.ChatClient;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.bytedesk.ai.demo.airline.services.FlightBookingService;
// import com.bytedesk.ai.demo.airline.services.BookingTools.BookingDetails;
// import com.bytedesk.core.utils.JsonResult;

// import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
// import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

// import lombok.RequiredArgsConstructor;
// import reactor.core.publisher.Flux;
// import org.springframework.beans.factory.annotation.Qualifier;

// @RestController
// @RequestMapping("/demo/airline")
// @RequiredArgsConstructor
// @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true")
// public class AirlineController {

// 	@Qualifier("dashScopeCustomerSupportAssistant")
// 	private final ChatClient dashScopeCustomerSupportAssistant;
	
// 	private final FlightBookingService flightBookingService;

// 	// http://127.0.0.1:9003//demo/airline/chat?chatId=1&userMessage=退票？
// 	@GetMapping(path = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
// 	public Flux<String> chat(String chatId, String userMessage) {
// 		return this.dashScopeCustomerSupportAssistant.prompt()
// 				.system(s -> s.param("current_date", LocalDate.now().toString()))
// 				.user(userMessage)
// 				.advisors(
// 						a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
// 				.stream()
// 				.content();
// 	}

// 	// http://127.0.0.1:9003//demo/airline/bookings
// 	@GetMapping("/bookings")
// 	public ResponseEntity<JsonResult<?>> getBookings() {

// 		List<BookingDetails> bookings = flightBookingService.getBookings();

// 		return ResponseEntity.ok(JsonResult.success(bookings));
// 	}

// }
