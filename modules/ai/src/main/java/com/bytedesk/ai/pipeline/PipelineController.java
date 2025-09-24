package com.bytedesk.ai.pipeline;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.core.message.MessageProtobuf;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pipeline")
public class PipelineController {

    private final PipelineService pipelineService;

    /**
     * 流式聊天（SSE）
     */
    @PostMapping(path = "/chat/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestBody PipelineChatRequest request) {
        SseEmitter emitter = new SseEmitter(0L);
        pipelineService.streamChat(request, emitter);
        return emitter;
    }

    /**
     * 同步聊天（用于不支持流的渠道）
     */
    @PostMapping(path = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public MessageProtobuf chat(@RequestBody PipelineChatRequest request) {
        return pipelineService.syncChat(request);
    }
}
