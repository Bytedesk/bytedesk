/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 10:55:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 11:05:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class SpringAiRagController {

    private final ChatClient defaultChatClient;

    private final VectorStore ollamaRedisVectorStore;

    @Value("classpath:/rag/bikes.json")
	private Resource bikesResource;

	@Value("classpath:/prompts/system-qa.st")
	private Resource systemBikePrompt;

    // retrieve
    // http://127.0.0.1:9003/rag/retrieve?message=
    @GetMapping("/retrieve")
    public ResponseEntity<JsonResult<?>> retrieve(@RequestParam(value = "message", defaultValue = "What is the most popular bike brand?") String message) {
        // Step 1 - Load JSON document as Documents

		log.info("Loading JSON as Documents");
		JsonReader jsonReader = new JsonReader(bikesResource, "name", "price", "shortDescription", "description");
		List<Document> documents = jsonReader.get();
		log.info("Loading JSON as Documents");

		// Step 2 - Create embeddings and save to vector store
		log.info("Creating Embeddings...");
		ollamaRedisVectorStore.add(documents);
		log.info("Embeddings created.");

		// Step 3 retrieve related documents to query
		log.info("Retrieving relevant documents");
		List<Document> similarDocuments = ollamaRedisVectorStore.similaritySearch(message);
		log.info(String.format("Found %s relevant documents.", similarDocuments.size()));

		// Step 4 Embed documents into SystemMessage with the `system-qa.st` prompt
		// template
		Message systemMessage = getSystemMessage(similarDocuments);
		UserMessage userMessage = new UserMessage(message);

		// Step 4 - Ask the AI model
		log.info("Asking AI model to reply to question.");
		Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
		log.info(prompt.toString());

		ChatResponse chatResponse = defaultChatClient.prompt(prompt)
				.call()
				.chatResponse();
		log.info("AI responded.");

        // FIXME: [500] Internal Server Error - {"error":{}}
        return ResponseEntity.ok(JsonResult.success(chatResponse)); 
    }

    private Message getSystemMessage(List<Document> similarDocuments) {
		String documents = similarDocuments.stream().map(entry -> entry.getText()).collect(Collectors.joining("\n"));
		SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemBikePrompt);
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("documents", documents));
		return systemMessage;
	}
    
    
}
