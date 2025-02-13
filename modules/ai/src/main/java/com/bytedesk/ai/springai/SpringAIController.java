package com.bytedesk.ai.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/spring/ai")
@RequiredArgsConstructor
public class SpringAIController {

    private final ChatClient chatClient;

    // http://127.0.0.1:9003/spring/ai/completion?message=hello&voice=agent
    // https://docs.spring.io/spring-ai/reference/api/chatclient.html
    @GetMapping("/completion")
    ResponseEntity<JsonResult<?>> completion(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message, String voice) {

        String completion = this.chatClient.prompt()
                .system(sp -> sp.param("voice", voice))
                .user(message)
                .call()
                .content();
        
        return ResponseEntity.ok(JsonResult.success(completion));
    }

}
