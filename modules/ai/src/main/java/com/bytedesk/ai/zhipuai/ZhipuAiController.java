/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 11:00:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-06 10:48:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.zhipuai;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.core.utils.JsonResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * https://open.bigmodel.cn/dev/api#sdk_install
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 * 
 * https://docs.spring.io/spring-ai/reference/api/chat/zhipuai-chat.html
 */
@Slf4j
@RestController
@RequestMapping("/visitor/api/v1/zhipuai")
@AllArgsConstructor
public class ZhipuAiController {

    private final ZhiPuAiChatModel chatModel;

    private final ZhiPuAiImageModel zhiPuAiImageModel;

    private final ZhipuAiService zhipuAiService;

    // http://localhost:9003/visitor/api/v1/zhipuai/chat
    @GetMapping("/chat")
    public ResponseEntity<?> generation() {
        ChatResponse response = chatModel.call(
                new Prompt(
                        "Generate the names of 5 famous pirates.",
                        ZhiPuAiChatOptions.builder()
                                .withModel(ZhiPuAiApi.ChatModel.GLM_3_Turbo.getValue())
                                .withTemperature(0.5f)
                                .build()));
        return ResponseEntity.ok(JsonResult.success(response));
    }

    // http://localhost:9003/visitor/api/v1/zhipuai/generate?content=hello
    @GetMapping("/generate")
    public Map<?, ?> generate(@RequestParam(value = "content", defaultValue = "讲个笑话") String content) {
        return Map.of("generation", chatModel.call(content));
    }

    // http://localhost:9003/visitor/api/v1/zhipuai/generateStream?content=讲个笑话
    @GetMapping("/generateStream")
    public Flux<ChatResponse> generateStream(
            @RequestParam(value = "content", defaultValue = "讲个笑话") String content) {
        var prompt = new Prompt(new UserMessage(content));
        return chatModel.stream(prompt);
    }

    // http://localhost:9003/visitor/api/v1/zhipuai/image
    @GetMapping("/image")
    public ResponseEntity<?> image() {
        ImageResponse response = zhiPuAiImageModel.call(new ImagePrompt("A light cream colored mini golden doodle"));
        return ResponseEntity.ok(JsonResult.success(response));
    }

    //

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // http://localhost:9003/visitor/api/v1/zhipuai/sse?uid=&sid=&content=hi
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> sseEndpoint(
            @RequestParam(value = "uid", required = true) String uid,
            @RequestParam(value = "sid", required = true) String sid,
            @RequestParam(value = "q", defaultValue = "讲个笑话") String question) {
        // TODO: 根据uid和ip判断此visitor是否骚扰用户，如果骚扰则拒绝响应

        log.info("sseEndpoint sid: {}, uid: {}, question {}", sid, uid, question);

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        executorService.submit(() -> {
            try {
                // 调用你的服务方法来获取SSE数据
                zhipuAiService.getSseAnswer(uid, sid, question, emitter);
                //
                // 将数据作为SSE事件发送
                // emitter.send(SseEmitter.event().data(sseData));
                // 完成后完成SSE流
                // emitter.complete();
            } catch (Exception e) {
                // 如果发生错误，则发送错误事件
                // emitter.send(SseEmitter.event().error(e));
                emitter.completeWithError(e);
            } finally {
                // 确保清理资源
                emitter.complete();
            }
        });

        return ResponseEntity.ok().body(emitter);
    }

    // 销毁时关闭ExecutorService
    public void destroy() {
        executorService.shutdown();
    }

}
