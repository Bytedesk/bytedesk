package com.bytedesk.ai.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ChatMemory
 * https://docs.spring.io/spring-ai/reference/api/chat-memory.html
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/chat-memory")
@RequiredArgsConstructor
@ConditionalOnBean(ChatClient.class)
public class ChatMemoryTestController {
    
}
