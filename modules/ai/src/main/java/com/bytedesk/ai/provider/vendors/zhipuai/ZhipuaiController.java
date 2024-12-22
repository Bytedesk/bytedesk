/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 11:00:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-22 18:08:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.vendors.zhipuai;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://open.bigmodel.cn/dev/api#sdk_install
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 * 
 * https://docs.spring.io/spring-ai/reference/api/chat/zhipuai-chat.html
 */
@Slf4j
@RestController
@RequestMapping("/zhipuai")
@AllArgsConstructor
public class ZhipuaiController {

    private final ZhiPuAiChatModel chatModel;

    private final ZhiPuAiImageModel zhiPuAiImageModel;

    private final ZhipuaiService zhipuaiService;

    // http://127.0.0.1:9003/zhipuai/chat
    @GetMapping("/chat")
    public ResponseEntity<?> generation() {
        ChatResponse response = chatModel.call(
                new Prompt(
                        "Generate the names of 5 famous pirates.",
                        ZhiPuAiChatOptions.builder()
                                .model(ZhiPuAiApi.ChatModel.GLM_3_Turbo.getValue())
                                .temperature(0.5)
                                .build()));
        return ResponseEntity.ok(JsonResult.success(response));
    }

    // http://127.0.0.1:9003/zhipuai/image
    @GetMapping("/image")
    public ResponseEntity<?> image() {
        ImageResponse response = zhiPuAiImageModel.call(new ImagePrompt("A light cream colored mini golden doodle"));
        return ResponseEntity.ok(JsonResult.success(response));
    }

    //
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // http://127.0.0.1:9003/zhipuai/sse?uid=&sid=&content=hi
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
                zhipuaiService.getSseAnswer(uid, sid, question, emitter);
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

    // http://127.0.0.1:9003/zhipuai/sse/test
    @GetMapping(value = "/sse/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> sseTest(@RequestParam(value = "q", defaultValue = "讲个笑话") String question) {
        log.info("sseTest question {}", question);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        executorService.submit(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    // 将数据作为SSE事件发送
                    String sseData = "emitter: " + System.currentTimeMillis() + "\n\n";
                    // emitter.send(sseData);
                    emitter.send(
                            SseEmitter.event().name("testname").id(String.valueOf(i)).comment("comment").data(sseData));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 完成后完成SSE流
                emitter.complete();
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

    // http://127.0.0.1:9003/zhipuai/stream-sse
    @GetMapping("/stream-sse")
    public void streamSse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();
        for (int i = 0; i < 10; i++) {
            writer.write("data: " + System.currentTimeMillis() + "\n\n");
            writer.flush();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writer.close();
    }

}
