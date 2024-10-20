/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 09:50:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-19 10:00:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.ollama;

// import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html
@Slf4j
@RestController
@RequestMapping("/visitor/api/v1/ai/ollama")
@AllArgsConstructor
public class OllamaController {

    // private final ChatClient chatClient;

    // private final ChatModel chatModel;

    // private final UploadVectorStore uploadVectorStore;

    // private final String PROMPT_BLUEPRINT = """
    //           Answer the query strictly referring the provided context:
    //           {context}
    //           Query:
    //           {query}
    //           In case you don't have any answer from the context provided, just say:
    //           I'm sorry I don't have the information you are looking for.
    //         """;
    // private final String PROMPT_BLUEPRINT = """
    // 根据提供的文档信息回答问题，文档信息如下:
    // {context}
    // 问题:
    // {query}
    // 当用户提出的问题无法根据文档内容进行回复或者你也不清楚时，回复不知道即可.
    // """;

    // http://127.0.0.1:9003/visitor/api/v1/ai/ollama/simple?message=讲一个笑话
    // @GetMapping("/simple")
    // public ResponseEntity<?> getSimpleCompletion(
    //         @RequestParam(value = "message", defaultValue = "讲一个笑话") String message) {

    //     String content = chatClient.prompt().user(message).call().content();

    //     return ResponseEntity.ok(JsonResult.success(content));
    // }

    // http://127.0.0.1:9003/visitor/api/v1/ai/ollama/rich
    // @GetMapping("/rich")
    // public ResponseEntity<?> getRichCompletion(
    //         @RequestParam(value = "message", defaultValue = "讲一个笑话") String message) {

    //     ChatResponse chatResponse = chatClient.prompt().user(message).call().chatResponse();
    //     // ChatResponse chatResponse = chatModel.call(
    //     // new Prompt(message,
    //     // OllamaOptions.builder()
    //     // .withModel(OllamaModel.MISTRAL)
    //     // // .withTemperature(0.4)
    //     // .build()));
    //     return ResponseEntity.ok(JsonResult.success(chatResponse));
    // }

    // FIXME: java.lang.NoClassDefFoundError:
    // org/eclipse/jetty/reactive/client/ReactiveRequest
    // http://127.0.0.1:9003/visitor/api/v1/ai/ollama/stream
    @GetMapping("/stream")
    public ResponseEntity<?> getStreamCompletion(
            @RequestParam(value = "message", defaultValue = "讲一个笑话") String message) {
        // chatModel.stream(new Prompt(message)).subscribe(chatResponse -> {
        //     log.info("stream response: {}", chatResponse.getResult().getOutput().getContent());
        // });
        
        return ResponseEntity.ok(JsonResult.success("stream success"));
    }

    // 参考
    // https://github.com/habuma/spring-ai-rag-example/blob/main/src/main/java/com/example/springairag/AskController.java
    // http://127.0.0.1:9003/visitor/api/v1/ai/ollama/chat?query=考试日期
    // @GetMapping("/chat")
    // public ResponseEntity<?> chat(@RequestParam(value = "query", defaultValue = "考试日期") String query) {
    //     //
    //     List<String> contentList = uploadVectorStore.searchText(query);

    //     ChatResponse response = chatClient.prompt()
    //             .user(userSpec -> userSpec
    //                     .text(PROMPT_BLUEPRINT)
    //                     .param("query", query)
    //                     .param("context", String.join("\n", contentList)))
    //             .call()
    //             .chatResponse();
    //     log.info("chat response: {}", response);
    //     String answer = response.getResult().getOutput().getContent();
    //     //
    //     return ResponseEntity.ok(JsonResult.success("chat success", answer));
    // }

    





    // http://127.0.0.1:9003/visitor/api/v1/ai/ollama/chat/stream?query=考试日期
    // @GetMapping("/chat/stream")
    // public void chatStream(@RequestParam(value = "query", defaultValue = "考试日期")
    // String query) {
    // //
    // List<String> contentList = uploadVectorStore.searchText(query);
    // //
    // SystemPromptTemplate promoptTemplate = new
    // SystemPromptTemplate(PROMPT_BLUEPRINT);
    // Message message = promoptTemplate.createMessage(Map.of("query", query,
    // "context", contentList));
    // log.info("query: {}, message {}", query, message);
    // //
    // chatModel.stream(new Prompt(message)).subscribe(response -> {
    // log.info(query + " : " + response.getResult().getOutput().getContent());
    // });
    // }

    // public Generation retrieve(String message) {
    // SearchRequest request = SearchRequest.query(message).withTopK(topK);
    // // Query Redis for the top K documents most relevant to the input message
    // List<Document> docs = store.similaritySearch(request);
    // Message systemMessage = getSystemMessage(docs);
    // UserMessage userMessage = new UserMessage(message);
    // // Assemble the complete prompt using a template
    // Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
    // // Call the autowired chat client with the prompt
    // ChatResponse response = client.call(prompt);
    // return response.getResult();
    // }
    // // end::retrieve[]

    // private Message getSystemMessage(List<Document> similarDocuments) {
    // String documents =
    // similarDocuments.stream().map(Document::getContent).collect(Collectors.joining("\n"));
    // SystemPromptTemplate systemPromptTemplate = new
    // SystemPromptTemplate(systemBeerPrompt);
    // return systemPromptTemplate.createMessage(Map.of("documents", documents));
    // }

}
