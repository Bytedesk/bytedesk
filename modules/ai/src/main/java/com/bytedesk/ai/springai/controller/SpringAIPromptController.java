/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 10:42:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-07 11:36:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.utils.output.ActorsFilms;
import com.bytedesk.core.utils.JsonResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Prompt Engineering 提示词工程
 * AI应用的核心：提示词的好坏直接影响AI的回答质量
 * 
 * Prompt四个核心要点：
 * 1. 指令：明确告诉AI要做什么
 * 2. 上下文：提供给AI一些相关的信息
 * 3. 用户输入：用户输入的信息
 * 4. 格式：告诉AI返回格式，比如json
 * 另外可以提供一些示例：提供给AI一些示例
 * 
 * https://docs.spring.io/spring-ai/reference/api/prompt.html
 * https://github.com/Azure-Samples/spring-ai-azure-workshop
 */
@Slf4j
@RestController
@RequestMapping("/springai/prompt")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.ollama.chat.enabled", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIPromptController {

	private final ChatClient bytedeskOllamaChatClient;

	@Qualifier("elasticsearchVectorStore")
	private final ElasticsearchVectorStore elasticsearchVectorStore;

	@Value("classpath:/aidemo/prompts/joke-prompt.st")
	private Resource jokeResource;

	@Value("classpath:/aidemo/prompts/system-message.st")
	private Resource systemResource;

	@Value("classpath:/aidemo/rag/wikipedia-curling.md")
	private Resource docsToStuffResource;

	@Value("classpath:/aidemo/prompts/qa-prompt.st")
	private Resource qaPromptResource;

	@Value("classpath:/aidemo/rag/bikes.json")
	private Resource bikesResource;

	@Value("classpath:/aidemo/prompts/system-qa.st")
	private Resource systemBikePrompt;

	// http://127.0.0.1:9003/springai/prompt/templating?adjective=funny&topic=cows
	@GetMapping("/templating")
	public ResponseEntity<JsonResult<?>> templating(
			@RequestParam(value = "adjective", defaultValue = "funny") String adjective,
			@RequestParam(value = "topic", defaultValue = "cows") String topic) {
		// 使用prompt模板
		PromptTemplate promptTemplate = new PromptTemplate(jokeResource);
		Prompt prompt = promptTemplate.create(Map.of("adjective", adjective, "topic", topic));

		ChatResponse response = bytedeskOllamaChatClient.prompt(prompt)
				.call()
				.chatResponse();

		return ResponseEntity.ok(JsonResult.success(response));
	}

	// http://127.0.0.1:9003/springai/prompt/roles?message=&name=&voice=pirate
	@GetMapping("/roles")
	public ResponseEntity<JsonResult<?>> roles(
			@RequestParam(value = "message", defaultValue = "Tell me about three famous pirates from the Golden Age of Piracy and why they did.  Write at least a sentence for each pirate.") String message,
			@RequestParam(value = "name", defaultValue = "Bob") String name,
			@RequestParam(value = "voice", defaultValue = "pirate") String voice) {

		// 使用prompt模板
		// name, The name of the AI assistant. The default value is Bob
		// voice, The style of voice that the AI assistant will use to reply. The
		// default value is pirate
		UserMessage userMessage = new UserMessage(message);
		SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));
		Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

		ChatResponse response = bytedeskOllamaChatClient.prompt(prompt)
				.call()
				.chatResponse();

		return ResponseEntity.ok(JsonResult.success(response));
	}

	// 通过stuff=true，将文档内容添加到上下文
	// http://127.0.0.1:9003/springai/prompt/stuff?message=&stuff=true
	@GetMapping("/stuff")
	public ResponseEntity<JsonResult<?>> stuff(
			@RequestParam(value = "message", defaultValue = "Which athletes won the mixed doubles gold medal in curling at the 2022 Winter Olympics?'") String message,
			@RequestParam(value = "stuff", defaultValue = "false") boolean stuff) {

		// 使用prompt模板
		PromptTemplate promptTemplate = new PromptTemplate(qaPromptResource);
		Map<String, Object> map = new HashMap<>();
		map.put("question", message);

		if (stuff) {
			// 将文档内容添加到上下文
			map.put("context", docsToStuffResource);
		} else {
			// 不将文档内容添加到上下文
			map.put("context", "");
		}
		Prompt prompt = promptTemplate.create(map);
		log.info("prompt: {}", prompt);

		ChatResponse response = bytedeskOllamaChatClient.prompt(prompt)
				.call()
				.chatResponse();

		return ResponseEntity.ok(JsonResult.success(response));
	}

	// rag
	// http://127.0.0.1:9003/springai/prompt/rag?message=
	@GetMapping("/rag")
	public ResponseEntity<JsonResult<?>> rag(
			@RequestParam(value = "message", defaultValue = "What is the most popular bike brand?") String message) {
		// Step 1 - Load JSON document as Documents

		log.info("Loading JSON as Documents");
		JsonReader jsonReader = new JsonReader(bikesResource, "name", "price", "shortDescription", "description");
		List<Document> documents = jsonReader.get();
		log.info("Loading JSON as Documents");

		// Step 2 - Create embeddings and save to vector store
		log.info("Creating Embeddings...");
		elasticsearchVectorStore.add(documents);
		log.info("Embeddings created.");

		// Step 3 retrieve related documents to query
		log.info("Retrieving relevant documents");
		List<Document> similarDocuments = elasticsearchVectorStore.similaritySearch(message);
		log.info(String.format("Found %s relevant documents.", similarDocuments.size()));

		// Step 4 Embed documents into SystemMessage with the `system-qa.st` prompt
		// template
		Message systemMessage = getSystemMessage(similarDocuments);
		UserMessage userMessage = new UserMessage(message);

		// Step 4 - Ask the AI model
		log.info("Asking AI model to reply to question.");
		Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
		log.info(prompt.toString());

		ChatResponse chatResponse = bytedeskOllamaChatClient.prompt(prompt)
				.call()
				.chatResponse();
		log.info("AI responded.");

		return ResponseEntity.ok(JsonResult.success(chatResponse));
	}

	// http://127.0.0.1:9003/springai/prompt/format?actor=
	// https://docs.spring.io/spring-ai/reference/api/structured-output-converter.html
	@GetMapping("/format")
	public ResponseEntity<JsonResult<?>> generate(
			@RequestParam(value = "actor", defaultValue = "Jeff Bridges") String actor) {

		// using the low-level ChatModel API directly:
		var outputParser = new BeanOutputConverter<>(ActorsFilms.class);

		String format = outputParser.getFormat();
		log.info("format: " + format);
		String userMessage = """
				Generate the filmography for the actor {actor}.
				{format}
				/no_think
				""";
		// 使用 builder 模式替换废弃的构造函数
		PromptTemplate promptTemplate = PromptTemplate.builder()
				.template(userMessage)
				.variables(Map.of("actor", actor, "format", format))
				.build();
		Prompt prompt = promptTemplate.create();

		String response = bytedeskOllamaChatClient.prompt(prompt)
				.call()
				.content();
		log.info("response: " + response);

		// 或者 使用structured output
		// https://docs.spring.io/spring-ai/reference/api/structured-output-converter.html
		ActorsFilms actorsFilms = bytedeskOllamaChatClient.prompt()
				.user(u -> u.text("Generate the filmography of 5 movies for {actor}.")
						.param("actor", "Tom Hanks"))
				.call()
				.entity(ActorsFilms.class);
		log.info("actorsFilms: {}", actorsFilms);

		return ResponseEntity.ok(JsonResult.success(outputParser.convert(response)));
	}

	// structured output
	// http://127.0.0.1:9003/springai/prompt/structured?message=
	// https://docs.spring.io/spring-ai/reference/api/structured-output-converter.html
	@GetMapping("/structured")
	public ResponseEntity<JsonResult<?>> structured(
			@RequestParam(value = "message", defaultValue = "Tell me about the actor Jeff Bridges") String message) {
		 
		StructuredOutputConverter<ActorsFilms> outputConverter = new BeanOutputConverter<>(ActorsFilms.class);
		String userInputTemplate = """
				Your response should be in JSON format.
				The data structure for the JSON should match this Java class: java.util.HashMap
				Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
				{message}
				{format}
				/no_think
				"""; // user input with a "format" placeholder.
		log.info("userInputTemplate: {}", userInputTemplate);
		 // 使用 builder 模式替换废弃的构造函数
		Prompt prompt = new Prompt(
				PromptTemplate.builder()
						.template(userInputTemplate)
						.variables(Map.of("message", message, "format", outputConverter.getFormat()))
						.build()
						.createMessage());

		ChatResponse response = bytedeskOllamaChatClient.prompt(prompt)
				.call()
				.chatResponse();

		return ResponseEntity.ok(JsonResult.success(response));
	}

	private Message getSystemMessage(List<Document> similarDocuments) {
		String documents = similarDocuments.stream().map(entry -> entry.getText()).collect(Collectors.joining("\n"));
		SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemBikePrompt);
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("documents", documents));
		return systemMessage;
	}

}
